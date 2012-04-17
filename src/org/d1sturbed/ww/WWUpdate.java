package org.d1sturbed.ww;

import java.io.DataInputStream;
import java.net.URL;
import java.security.KeyStore.LoadStoreParameter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class WWUpdate extends IntentService implements Runnable {


	public static final String UPDATE = "WWupdate.UPDATE";
	private Context context=null;
	public static final boolean DEBUG = WW.DEBUG;
	public static final String TAG = "WWupdate";
	private String caller;
	public WWUpdate(Context context) {
		super("WWupdate");
		this.context=context;
	}
	
	public WWUpdate(Context context, String c) {
		super("WWupdate");
		this.context=context;
		this.caller=c;
	}
	public WWUpdate() {
		super("WWupdate");
	}
	
	protected void debug(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}
	public long getWoeID(Context context) throws Exception {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location lastKnownLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		DataInputStream theHTML;
		String thisLine = "";
		StringBuffer sb = new StringBuffer();
		URL u = new URL("http://where.yahooapis.com/geocode?location="
				+ String.valueOf(lastKnownLocation.getLatitude()) + "+"
				+ String.valueOf(lastKnownLocation.getLongitude())
				+ "&locale=de_DE&gflags=R&flags=J");
		debug("http://where.yahooapis.com/geocode?location="
				+ String.valueOf(lastKnownLocation.getLatitude()) + "+"
				+ String.valueOf(lastKnownLocation.getLongitude())
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
			woeid = getWoeID(context);
			
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
			debug("test");
			context.sendBroadcast(mintent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getAction().equals(WWUpdate.UPDATE) && intent.hasExtra("className")) {
			new Thread(new WWUpdate(getApplicationContext(),intent.getStringExtra("className"))).start();
		}
	}


}
