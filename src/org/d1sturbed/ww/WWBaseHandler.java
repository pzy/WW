package org.d1sturbed.ww;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.location.Location;
import android.util.Log;

public abstract class WWBaseHandler extends DefaultHandler implements Serializable {
	/**
	 * handler for parsing xml
	 */
	private static final long serialVersionUID = 4793473735545011541L;

 
	private boolean interested;
	private String data;
	private char tempUnit='C';
	private int temperature;
	private String pic;
	private String iconid;
	private String sunrise;
	private String sunset;
	private String location;
	
	private int low_temp=0;
	private int high_temp=0;
	public static final boolean DEBUG = WW.DEBUG;
	public static final String TAG = "WWHandler";
	
	public abstract String getShortString();

	
	protected void debug(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}
 
 
	@Override
	public void startDocument() throws SAXException {
		interested = false;
	}
 
	@Override
	public void endDocument() throws SAXException {

	}
	//make times comparable
	public long time2minutes(String time)
	{
	    long minuts = 0;
	    debug(time);
	    String[] atime = time.split(" ");
	    if (atime[1].toLowerCase().equals("pm")) {
	    	debug("pm");
	        minuts = 12 * 60;
	    }
	    String[] ttime = atime[0].split(":");
	    minuts = minuts + Long.parseLong(ttime[0]) * 60 + Long.parseLong(ttime[1]);
	    debug(""+minuts);
	    return minuts;
	}
	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException {

	}
 
	@Override
	public void endElement(String namespaceURI, String localName, String qName)throws SAXException {

	}

	
	public abstract String getDesc();

	public boolean isDay() {
		return false;
	}

	@Override
    public void characters(char ch[], int start, int length)throws SAXException {
		String s=new String(ch, start, length);
		if(interested) {
			if(s!=null) {
				setData(s);
			}
		}
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data += data;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getLow_temp() {
		return low_temp;
	}

	public void setLow_temp(int low_temp) {
		this.low_temp = low_temp;
	}

	public int getHigh_temp() {
		return high_temp;
	}

	public void setHigh_temp(int high_temp) {
		this.high_temp = high_temp;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	
	public String toString() {
		return getDesc()+":"+getPic()+"";
		
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getIconid() {
		return iconid;
	}


	public void setIconid(String iconid) {
		this.iconid = iconid;
	}


	public char getTempUnit() {
		return tempUnit;
	}


	public void setTempUnit(char tempunit) {
		this.tempUnit = tempunit;
	}


	public String getSunrise() {
		return sunrise;
	}


	public void setSunrise(String sunrise) {
		this.sunrise = sunrise;
	}


	public String getSunset() {
		return sunset;
	}


	public void setSunset(String sunset) {
		this.sunset = sunset;
	}


	abstract public URL getUrl(Location lo);


	abstract public ArrayList<String> getForcecastPics();
}
