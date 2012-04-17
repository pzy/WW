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


	public static final String UPDATE = "WWupdate.UPDATE";
	private Context context=null;
	public static final boolean DEBUG = WW.DEBUG;
	public static final String TAG = "WWupdate";
	private String caller;
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
			woeid = getWoeID(this.lo);
			
			u = new URL("http://weather.yahooapis.com/forecastrss?w="
					+ String.valueOf(woeid) + "&u=c");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = null;
			sp = spf.newSAXParser();
			sp.parse(u.openStream(), h);
			debug(caller);
			Intent mintent = new Intent(context, Class.forName(caller));
			mintent.setAction(WW.ACTION_WIDGET_SWITCH);
			mintent.putExtra("h", h);
			context.sendBroadcast(mintent);
		} catch (Exception e) {
			debug(e.toString());
		}
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Location l;
		if (intent.getAction().equals(WWUpdate.UPDATE) && intent.hasExtra("className")) {
			if(intent.hasExtra("location")) {
				l=(Location) intent.getParcelableExtra("location");
				new Thread(new WWUpdate(getApplicationContext(),intent.getStringExtra("className"),l)).start();
			} 
		}
	}


}
