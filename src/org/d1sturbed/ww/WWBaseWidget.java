package org.d1sturbed.ww;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public abstract class WWBaseWidget extends AppWidgetProvider implements LocationListener,Runnable {
	public static final boolean DEBUG = true;
	public static final String TAG = "WW";
	public static final String ACTION_WIDGET_SWITCH = "WW.ACTION_WIDGET_SWITCH";
	public static String ACTION_START_ACTIVITY = "WW.ACTION_START_ACTIVITY";
	protected Context context;
	protected WWHandler h;


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

	protected abstract void setWidget(WWHandler ha, Bitmap b);
	
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
				setWidget(this.h, BitmapFactory.decodeStream(input));
			}
		} catch(Exception e) {
			setWidget(h, null);
			debug("Could not get weather icon" + e.toString());
		}
	}

}