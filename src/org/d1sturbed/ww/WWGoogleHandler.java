package org.d1sturbed.ww;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.location.Location;
import android.util.Log;

public class WWGoogleHandler extends WWBaseHandler implements Serializable {
	/**
	 * handler for parsing xml
	 */
	private static final long serialVersionUID = 4793473735545011541L;
	private final String CURRENTTAG = "current_conditions";
	private final String TEMPTAG = "temp_c";
	private final String HUMTAG = "humidity";
	private final String FORETAG = "forecast_conditions";
	//private final String LINKTAG = "icon";
	private final String PICTAG = "icon";
	/*private final String ASTRONOMYTAG = "astronomy";
	private final String LOCATIONTAG = "location";
	private final String UNITTAG = "units";
	private final String TEMP  = "temp";*/
	private final String HIGHTAG = "high";
	private final String DOWTAG = "day_of_week";
	private final String LOWTAG = "low";
	private final String CONDITIONTAG = "condition";
	/*private final String CITY = "city";
	private final String ICONID = "code";
	private final String TEMPUNIT = "temperature";

	private final String SUNRISETIME = "sunrise";
	private final String SUNSETTIME = "sunset";*/
 
	private boolean interested;
	private String data;
	private char tempUnit='C';
	private boolean current;
	private int temperature;
	private String pic;
	private String humidity;
	private String iconid;
	private String sunrise;
	private String sunset;
	private String location;

	private WWForecast f;
	
	private int low_temp=0;
	private int high_temp=0;
	public static final boolean DEBUG = WW.DEBUG;
	public static final String TAG = "WWHandler";


	protected void debug(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}
 
 
	@Override
	public void startDocument() throws SAXException {
		interested = false;
		wwf=new ArrayList<WWForecast>();
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
		data=null;
		Log.d("icky", localName);
		if(localName.equals(CURRENTTAG)){
			interested=true;
			current=true;
			debug("hit");
		} else if(localName.equals(TEMPTAG) && interested && current) {
		    temperature=Integer.parseInt(atts.getValue("data"));
		} else if(localName.equals(PICTAG) && interested && current) {
			pic=atts.getValue("data");
		} else if(localName.equals(HUMTAG) && interested && current) {
			setHumidity(atts.getValue("data"));
		} else if(localName.equals(FORETAG)) {
			interested=true;
			current=false;
			f=new WWForecast();
		} else if(localName.equals(HIGHTAG) && interested && !current) {
			debug("licky: " + atts.getValue("data"));
			f.setHigh((Integer.parseInt(atts.getValue("data"))-32)*5/9);
		} else if(localName.equals(LOWTAG) && interested && !current) {
			f.setLow((Integer.parseInt(atts.getValue("data"))-32)*5/9);
		} else if(localName.equals(PICTAG) && interested && !current) {
			debug("licky: " + atts.getValue("data"));
			f.setIcon(atts.getValue("data"));
		} else if(localName.equals(CONDITIONTAG) && interested && !current) {
			f.setCondition(atts.getValue("data"));
		} else if(localName.equals(DOWTAG) && interested && !current) {
			f.setDay(atts.getValue("data"));
		}
		
	}
 
	@Override
	public void endElement(String namespaceURI, String localName, String qName)throws SAXException {
		if(localName.equals(CURRENTTAG) && interested && current) {
			interested=false;
			current=false;
		} else if(localName.equals(FORETAG)) {
			wwf.add(f);
			interested=false;
		} 
	}
	
	public String getShortString() {
		return "Temp: "+getTemperature()+"°"+getTempUnit()+" \n"+getHumidity().replaceAll("Humidity:", "Hum:");
	}
 
	
	public String getDesc() {
		StringBuffer desc=new StringBuffer();
		for(int i=0;i<wwf.size();i++) {
			f=wwf.get(i);
			desc.append(f.getDay()+":\n\tHigh: "+ f.getHigh() + "°" + getTempUnit() + "\n\tLow: " + f.getLow() + "°" + getTempUnit() + "\n\tCondition: " + f.getCondition() + "\n\n");
		}
		return desc.toString();
	}

	public boolean isDay() {
		long acttime=2*60+time2minutes(new SimpleDateFormat("K:m a").format(new Date()).replaceAll("vorm.", "am").replaceAll("nachm.", "pm"));
		if(time2minutes(getSunrise())<acttime  && time2minutes(getSunset())>acttime) {
			return true;
		} else {
			return false;
		}
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

	public String getPic(String pic) {
		return "http://www.google.com"+pic;
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


	public String getHumidity() {
		return humidity;
	}


	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}


	@Override
	public URL getUrl(Location lo) {
		try {
			debug("http://www.google.com/ig/api?weather=,,,"+ String.format("%.0f", lo.getLatitude()*1000000) + ","
					+ String.format("%.0f", lo.getLongitude()*1000000));
			return new URL("http://www.google.com/ig/api?weather=,,,"+ String.format("%.0f", lo.getLatitude()*1000000) + ","
					+ String.format("%.0f", lo.getLongitude()*1000000));
		} catch (MalformedURLException e) {
			return null;
		}
	}


	@Override
	public ArrayList<String> getForecastPics() {
		ArrayList<String> al=new ArrayList<String>();
		for(int i=0;i<wwf.size();i++) {
			debug(wwf.get(i).getIcon());
			al.add(getPic(wwf.get(i).getIcon()));
		}
		return al;
	}
}
