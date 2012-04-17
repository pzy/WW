package org.d1sturbed.ww;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
	
	//debugging
	public static final boolean DEBUG = true;
	public static final String TAG = "WW";
	//intent actions
	public static final String ACTION_WIDGET_SWITCH = "WW.ACTION_WIDGET_SWITCH";
	public static String ACTION_START_ACTIVITY = "WW.ACTION_START_ACTIVITY";
	
	public static String PATH = "http://l.yimg.com/us.yimg.com/i/us/nws/weather/gr/";
	//application context
	protected Context context;
	//weather handler data
	protected WWHandler h;
	//layout of the widget
	protected int l;
	//location
	protected Location lo;


	
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

	//update the widget data
	protected void setWidget(WWHandler ha, Bitmap b) {
		RemoteViews updateViews;
		AppWidgetManager manager;
		ComponentName widget;
		
		updateViews = new RemoteViews(context.getPackageName(), l);
		debug("Base" + getClass().getName());
		manager = AppWidgetManager.getInstance(context);
		widget = new ComponentName(context, getClass());
		
		//start WWActivity onclick
		PendingIntent pendingShowActivityIntent;
		Intent intent = new Intent(context, getClass());
		intent.setAction(WWBaseWidget.ACTION_START_ACTIVITY);
		intent.putExtra("h", ha);
		intent.putExtra("b", b);
		pendingShowActivityIntent = PendingIntent.getBroadcast(context, new Random().nextInt(),	intent, 0);
		updateViews.setOnClickPendingIntent(R.id.widget_textview, pendingShowActivityIntent);
		
		//set Widget
		try {
			if(b!=null) {
				updateViews.setImageViewBitmap(R.id.widget_imageview, b);
			}
			updateViews.setTextViewText(R.id.widget_textview, "Act:" + ha.getTemperature() + " °"+h.getTempUnit()+"\nN: "+ ha.getLow_temp()+" °"+h.getTempUnit()+"\nH: "+ha.getHigh_temp()+" °"+h.getTempUnit());
		} catch (Exception e) {
			debug("Fehler: " + e.toString());
			updateViews.setTextViewText(R.id.widget_textview, "Fehler:" + e.toString());
		}
		
		//update View
		manager.updateAppWidget(widget, updateViews);
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onLocationChanged(Location location) {
		updateWeather(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		debug(provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}



	@Override
	//get Weather icon and update Widget
	public void run() {
		try {
			if(h!=null) {
				URL u;
				if(h.isDay()) {
					u=new URL(PATH+h.getIconid()+"d.png");					
				} else {
					u=new URL(PATH+h.getIconid()+"n.png");
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
	
	
	//send update intent to update service
	private void updateWeather(Location myl) {
		Intent mintent = new Intent(this.context, WWUpdate.class);
		mintent.setAction(WWUpdate.UPDATE);
		mintent.putExtra("location", myl);
		mintent.putExtra("className", this.getClass().getName());
		context.startService(mintent);		
	}
	

	//receive incoming intents
	public void onReceive(Context context, Intent intent) {
		debug(getClass().getName()+":"+intent.getAction());
		WWBaseWidget w=null;
		WWHandler h=null;
		//get Handler Extra, contains weather data
		if(intent.hasExtra("h")) {
			h=(WWHandler)intent.getExtras().getSerializable("h");
		}
		
		//which widget are we? (ugly)
		switch(l) {
			case R.layout.widget:
				w=new WW(context,h);
				break;
			case R.layout.widget2x1:
				w=new WW2x1(context,h);
				break;
			default:
				w=null;
				break;						
		}
		
		//weather data update 
		if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Location myl=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000000, 1000, w);
			if(myl!=null) {
				w.updateWeather(myl);
			}
			
		//widget view update
		} else if (intent.getAction().equals(WW.ACTION_WIDGET_SWITCH)) {
			if(w!=null) {
				new Thread(w).start();
			}
		//start application with containing contents
		} else if (intent.getAction().equals(WW.ACTION_START_ACTIVITY)) {
			Intent mintent = new Intent(context, WWActivity.class);
			mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if(h!=null) {
				mintent.putExtra("h", h);
			}
			if(intent.hasExtra("b")) {
				mintent.putExtra("b", (Bitmap) intent.getParcelableExtra("b"));
			}
			context.startActivity(mintent);
		}
	}

}