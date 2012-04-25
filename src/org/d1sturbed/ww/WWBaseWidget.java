package org.d1sturbed.ww;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import android.graphics.PorterDuff.Mode;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

public abstract class WWBaseWidget extends AppWidgetProvider implements LocationListener,Runnable {
	
	//debugging
	public static final boolean DEBUG = true;
	public static final String TAG = "WW";
	//intent actions
	public static final String ACTION_WIDGET_SWITCH = "WW.ACTION_WIDGET_SWITCH";
	public static String ACTION_START_ACTIVITY = "WW.ACTION_START_ACTIVITY";
	
	//application context
	protected Context context;
	//weather handler data
	protected WWBaseHandler h;
	//layout of the widget
	protected int l;
	//location
	protected Location lo;


	
	@Override
	public void onEnabled(Context context) {
		debug("onEnabled-: " + context.getPackageName());
		super.onEnabled(context);
	}

	protected void debug(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}
   public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
   
    
	//update the widget data
	protected void setWidget(WWBaseHandler ha, Bitmap b) {
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
		if(b!=null) {
			intent.putExtra("b", b);	
		}

		pendingShowActivityIntent = PendingIntent.getBroadcast(context, new Random().nextInt(),	intent, 0);
		updateViews.setOnClickPendingIntent(R.id.widget_textview, pendingShowActivityIntent);
		
		//set Widget
		try {
			if(b!=null) {
				updateViews.setImageViewBitmap(R.id.widget_imageview, getRoundedCornerBitmap(b, 15));
				updateViews.setInt(R.id.widget_imageview, "setAlpha", 50);
			}
			updateViews.setTextViewText(R.id.widget_textview, ha.getShortString());
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
				URL u=new URL(h.getPic());
				HttpURLConnection connection = (HttpURLConnection) u.openConnection();
				connection.setDoInput(true);
				connection.connect();
				//connection.setUseCaches(true);
				InputStream input = connection.getInputStream();
				Bitmap b=BitmapFactory.decodeStream(input);
				connection.disconnect();
				input.close();
				debug(h.getPic());
				ArrayList<Bitmap> ba=new ArrayList<Bitmap>();
				ArrayList<String> sa=h.getForcecastPics();
				debug(sa.size()+"-");
				for(int i=0;i<sa.size();i++) {
					debug(sa.get(i));
					if(sa.get(i)==null) {
						break;
					}
					URL u2=new URL(sa.get(i));
					HttpURLConnection c2 = (HttpURLConnection) u2.openConnection();
					c2.setDoInput(true);
					c2.connect();
					//c2.setUseCaches(true);
					InputStream i2 = c2.getInputStream();
					ba.add(BitmapFactory.decodeStream(i2));
					c2.disconnect();
					i2.close();
				}
				if(b!=null && ba!=null) {
					debug("ba set!");
					setWidget(this.h, b);
				} else {
					setWidget(this.h, null);					
				}
			}
		} catch(Exception e) {
			setWidget(h, null);
			debug("Could not get y" + e.toString());
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
		WWBaseHandler h=null;
		//get Handler Extra, contains weather data
		if(intent.hasExtra("h")) {
			h=(WWBaseHandler)intent.getExtras().getSerializable("h");
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
			if(intent.hasExtra("ba")) {
				debug("baba");
				mintent.putExtra("ba", intent.getParcelableExtra("ba"));
			}
			context.startActivity(mintent);
		}
	}

}