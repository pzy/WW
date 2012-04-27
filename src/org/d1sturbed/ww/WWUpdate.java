package org.d1sturbed.ww;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class WWUpdate extends IntentService implements Runnable {

    //Intent that is send for update
	public static final String UPDATE = "WWupdate.UPDATE";
	//save Application context
	private Context context=null;
	//change provider
	// 0 - YahooWeather
	// 1 - GoogleWeather
	private int PROVIDER=1;
	//debug
	public static final boolean DEBUG = WW.DEBUG;
	//Tag for log output
	public static final String TAG = "WWupdate";
	//Classname of the caller
	private String caller;
	//current Location
	private Location lo;
	
	
	public WWUpdate(Context context) {
		super("WWupdate");
		this.context=context;
	}
	
	public WWUpdate(Context context, String c) {
		super("WWupdate");
		this.context=context;
		this.caller=c;
	}
	
	
	public WWUpdate(Context context, String c, Location lo) {
		super("WWupdate");
		this.context=context;
		this.caller=c;
		this.lo=lo;
	}
	
	public WWUpdate() {
		super("WWupdate");
	}
	
	protected void debug(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}
	

	

	
	//fetch weather by woeid from yahoos weather api
	@Override
	public void run() {
		debug("run()");
		WWBaseHandler h;
		try {
			if(this.lo==null) {
				return;
			}
			switch(PROVIDER) {
				case 0:
					h = new WWYahooHandler();		
					break;
				case 1:
					h = new WWGoogleHandler();	
					break;
				 default:
					h=null;
					break;
			}
			if(h.getUrl(lo)==null || h==null) {
				return;
			}
			//debug(u.toString());
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = null;
			sp = spf.newSAXParser();
			sp.parse(h.getUrl(lo).openStream(), h);
			debug(caller);
			//send intent with weather data to widget
			Intent mintent = new Intent(context, Class.forName(caller));
			mintent.setAction(WW.ACTION_WIDGET_SWITCH);
			mintent.putExtra("h", h);
			context.sendBroadcast(mintent);
		} catch (Exception e) {
			debug(e.toString()+e.getMessage());
		}
		
	}
	

	//handle intents
	@Override
	protected void onHandleIntent(Intent intent) {
		Location l;
		if (intent.getAction().equals(WWUpdate.UPDATE) && intent.hasExtra("className")) {
			//if the intent sends locations data and we have the class name of the caller widget we start the update
			if(intent.hasExtra("location")) {
				l=(Location) intent.getParcelableExtra("location");
				new Thread(new WWUpdate(getApplicationContext(),intent.getStringExtra("className"),l)).start();
			} 
		}
	}


}
