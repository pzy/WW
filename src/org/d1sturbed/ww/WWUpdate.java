package org.d1sturbed.ww;

import java.io.DataInputStream;
import java.net.URL;

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
	

	
	//getWoeID by Location from yahoos
	public long getWoeID(Location location) throws Exception {
		debug(context.toString());
		DataInputStream theHTML;
		String thisLine = "";
		StringBuffer sb = new StringBuffer();

		debug("http://where.yahooapis.com/geocode?location="
				+ String.valueOf(location.getLatitude()) + "+"
				+ String.valueOf(location.getLongitude())
				+ "&locale=de_DE&gflags=R&flags=J");
		URL u = new URL("http://where.yahooapis.com/geocode?location="
				+ String.valueOf(location.getLatitude()) + "+"
				+ String.valueOf(location.getLongitude())
				+ "&locale=de_DE&gflags=R&flags=J");
		theHTML = new DataInputStream(u.openStream());
		while ((thisLine = theHTML.readLine()) != null) {
			sb.append(thisLine);
		}
		String pat = ".*(,\"woeid\":)(.+?)(,\"woetype).*";
		thisLine = new String(sb).replaceAll("\\r\\n|\\r|\\n|\\t|\\ ", "");
		thisLine = thisLine.replaceAll(pat, "$2");
		return Long.parseLong(thisLine);
	}
	
	//fetch weather by woeid from yahoos weather api
	@Override
	public void run() {
		URL u;
		debug("run()");
		long woeid = 0;
		WWHandler h = new WWHandler();
		try {
			if(this.lo==null) {
				return;
			}
			//getWoeID from yahoos geocoding api
			woeid = getWoeID(this.lo);
			
			
			//get weather xml for current location
			u = new URL("http://weather.yahooapis.com/forecastrss?w="
					+ String.valueOf(woeid) + "&u=c");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = null;
			sp = spf.newSAXParser();
			sp.parse(u.openStream(), h);
			debug(caller);
			//send intent with weather data to widget
			Intent mintent = new Intent(context, Class.forName(caller));
			mintent.setAction(WW.ACTION_WIDGET_SWITCH);
			mintent.putExtra("h", h);
			context.sendBroadcast(mintent);
		} catch (Exception e) {
			debug(e.toString());
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
