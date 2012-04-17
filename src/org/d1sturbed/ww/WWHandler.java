package org.d1sturbed.ww;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

public class WWHandler extends DefaultHandler implements Serializable {
	/**
	 * handler for parsing xml
	 */
	private static final long serialVersionUID = 4793473735545011541L;
	private final String TEMPTAG = "condition";
	private final String FORETAG = "forecast";
	private final String LINKTAG = "link";
	private final String PICTAG = "description";
	private final String ASTRONOMYTAG = "astronomy";
	private final String LOCATIONTAG = "location";
	private final String UNITTAG = "units";
	private final String TEMP  = "temp";
	private final String LOW = "low";
	private final String CITY = "city";
	private final String ICONID = "code";
	private final String TEMPUNIT = "temperature";
	private final String HIGH = "high";
	private final String SUNRISETIME = "sunrise";
	private final String SUNSETTIME = "sunset";
 
	private boolean interested;
	private String data;
	private char tempUnit='C';
	private int temperature;
	private String pic;
	private String desc;
	private String url;
	private String iconid;
	private String sunrise;
	private String sunset;
	private String location;
	
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
		if(localName.equals(TEMPTAG)){
			temperature=Integer.parseInt(atts.getValue(TEMP));
			setIconid(atts.getValue(ICONID));
		} else if(localName.equals(FORETAG)) {
			if(low_temp==0 && high_temp==0) {
				low_temp = Integer.parseInt(atts.getValue(LOW));
				high_temp = Integer.parseInt(atts.getValue(HIGH));
			}
		} else if(localName.equals(PICTAG)) {
			interested=true;
		} else if(localName.equals(LINKTAG)) {
			interested=true;
		} else if(localName.equals(LOCATIONTAG)) {
			if(location==null) {
				location = atts.getValue(CITY);
			}
		} else if(localName.equals(UNITTAG)) {
			tempUnit=atts.getValue(TEMPUNIT).substring(0, 1).toCharArray()[0];
		} else if(localName.equals(ASTRONOMYTAG)) {
			sunrise=atts.getValue(SUNRISETIME);
			sunset=atts.getValue(SUNSETTIME);
		}
	}
 
	@Override
	public void endElement(String namespaceURI, String localName, String qName)throws SAXException {
		if(localName.equals(PICTAG) && interested) {
			setDesc(android.text.Html.fromHtml(getData()).toString().replaceAll("null", "").trim().substring(1));
			debug("setDesc("+ getDesc()+")");
			interested=false;
		} else if(localName.equals(LINKTAG) && interested) {
			url = getData().replaceAll("null", "");
			debug("url:" + url);
			interested=false;
		}
	}
 
	private void setDesc(String desc) {
		this.desc=desc;
	}
	
	public String getDesc() {
		return this.desc;
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

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String toString() {
		return getDesc()+":"+getUrl()+":"+getPic()+"";
		
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
}
