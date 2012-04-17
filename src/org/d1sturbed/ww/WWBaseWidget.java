package org.d1sturbed.ww;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public abstract class WWBaseWidget extends AppWidgetProvider implements LocationListener,Runnable {
	public static final boolean DEBUG = true;
	public static final String TAG = "WW";
	public static final String ACTION_WIDGET_SWITCH = "WW.ACTION_WIDGET_SWITCH";
	public static String ACTION_START_ACTIVITY = "WW.ACTION_START_ACTIVITY";
	protected Context context;
	protected WWHandler h;
	protected int l;


	@Override
	public void onEnabled(Context context) {
		debug("onEnabled: " + context.getPackageName());
		super.onEnabled(context);
	}

	protected void debug(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	protected void setWidget(WWHandler ha, Bitmap b) {
		RemoteViews updateViews;
		AppWidgetManager manager;
		ComponentName widget;
		
		updateViews = new RemoteViews(context.getPackageName(), l);
		debug("Base" + getClass().getName());
		manager = AppWidgetManager.getInstance(context);
		widget = new ComponentName(context, getClass());
		PendingIntent pendingShowActivityIntent;
		Intent intent = new Intent(context, getClass());
		intent.setAction(WWBaseWidget.ACTION_START_ACTIVITY);
		intent.putExtra("h", ha);
		intent.putExtra("b", b);
		pendingShowActivityIntent = PendingIntent.getBroadcast(context, new Random().nextInt(),
				intent, 0);
		updateViews.setOnClickPendingIntent(R.id.widget_textview,
				pendingShowActivityIntent);
		try {
			if(b!=null) {
				updateViews.setImageViewBitmap(R.id.widget_imageview, b);
			}
			updateViews.setTextViewText(R.id.widget_textview, "Act:" + ha.getTemperature() + " °"+h.getTempUnit()+"\nN: "+ ha.getLow_temp()+" °"+h.getTempUnit()+"\nH: "+ha.getHigh_temp()+" °"+h.getTempUnit());
		} catch (Exception e) {
			debug("Fehler: " + e.toString());
			updateViews.setTextViewText(R.id.widget_textview, "Fehler:" + e.toString());
		}
		manager.updateAppWidget(widget, updateViews);
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		debug("test");
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		Intent intent = new Intent(context, WWUpdate.class);
		intent.setAction(WWUpdate.UPDATE);
		context.startService(intent);
	}

	@Override
	public void onLocationChanged(Location location) {
		Intent mintent = new Intent(context, WWUpdate.class);
		mintent.setAction(WWUpdate.UPDATE);
		context.startService(mintent);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	
	public long time2minuts(String time)
	{
	    long minuts = 0;
	    debug(time);
	    String[] atime = time.split(" ");
	    if (atime[1].toLowerCase().equals("pm")) {
	        minuts = 12 * 60;
	    }
	    String[] ttime = atime[0].split(":");
	    minuts = minuts + Long.parseLong(ttime[0]) * 60 + Long.parseLong(ttime[1]);
	    debug(""+minuts);
	    return minuts;
	}

	@Override
	public void run() {
		try {
			if(h!=null) {
				long acttime=time2minuts(new SimpleDateFormat("k:m a").format(new Date()).replaceAll("vorm.", "am").replaceAll("nachm.", "pm"));
				URL u;
				if(time2minuts(h.getSunrise())<acttime  && time2minuts(h.getSunset())>acttime) {
					u=new URL("http://l.yimg.com/us.yimg.com/i/us/nws/weather/gr/"+h.getIconid()+"d.png");					
				} else {
					u=new URL("http://l.yimg.com/us.yimg.com/i/us/nws/weather/gr/"+h.getIconid()+"n.png");
				}
				HttpURLConnection connection = (HttpURLConnection) u.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap b=BitmapFactory.decodeStream(input);
				if(b!=null) {
					setWidget(this.h, b);
				} else {
					setWidget(this.h, null);					
				}
			}
		} catch(Exception e) {
			setWidget(h, null);
			debug("Could not get weather icon" + e.toString());
		}
	}
	

	public void onReceive(Context context, Intent intent) {
		debug(getClass().getName()+":"+intent.getAction());
		if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
			Intent mintent = new Intent(context, WWUpdate.class);
			mintent.setAction(WWUpdate.UPDATE);
			mintent.putExtra("className", this.getClass().getName());
			context.startService(mintent);
		} else if (intent.getAction().equals(WW.ACTION_WIDGET_SWITCH)) {
			if(intent.hasExtra("h")) {
				WWHandler h= (WWHandler)intent.getExtras().getSerializable("h");
				if(l==R.layout.widget) {
					new Thread(new WW(context,h)).start();
				} else {
					new Thread(new WW2x1(context,h)).start();					
				}
			}
		} else if (intent.getAction().equals(WW.ACTION_START_ACTIVITY)) {
			Intent mintent = new Intent(context, WWActivity.class);
			mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			if(intent.hasExtra("h")) {
				WWHandler h=(WWHandler) intent.getSerializableExtra("h");
				mintent.putExtra("h", h);
			}
			if(intent.hasExtra("b")) {
				mintent.putExtra("b", (Bitmap) intent.getParcelableExtra("b"));
			}
			context.startActivity(mintent);
		}
	}

}