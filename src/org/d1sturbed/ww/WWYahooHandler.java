package org.d1sturbed.ww;

import java.io.DataInputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.location.Location;
import android.util.Log;

public class WWYahooHandler extends WWBaseHandler implements Serializable {
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
	private final String HUMTAG = "atmosphere";
	private final String HUM = "humidity";
	private final String DAY = "day";
	private final String CODE = "code";
	private final String CONDITION = "text";
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
	private String desc;
	private int humidity;
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
 
	//getWoeID by Location from yahoos
	public long getWoeID(Location location) throws Exception {
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
		while ((thisLine = theHTML.readUTF()) != null) {
			sb.append(thisLine);
		}
		String pat = ".*(,\"woeid\":)(.+?)(,\"woetype).*";
		thisLine = new String(sb).replaceAll("\\r\\n|\\r|\\n|\\t|\\ ", "");
		thisLine = thisLine.replaceAll(pat, "$2");
		return Long.parseLong(thisLine);
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
	    if (atime[1].toLowerCase(Locale.GERMAN).equals("pm")) {
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
			setTemperature(Integer.parseInt(atts.getValue(TEMP)));
			setIconid(atts.getValue(ICONID));
		} else if(localName.equals(HUMTAG)) {
			setHumidity(Integer.parseInt(atts.getValue(HUM)));
		} else if(localName.equals(FORETAG)) {
			WWForecast wf=new WWForecast();
			wf.setLow(Integer.parseInt(atts.getValue(LOW)));
			wf.setCondition(atts.getValue(CONDITION));
			wf.setHigh(Integer.parseInt(atts.getValue(HIGH)));
			wf.setDay(atts.getValue(DAY));
			wf.setIcon(atts.getValue(CODE));
			wwf.add(wf);
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
		long acttime=2*60+time2minutes(new SimpleDateFormat("K:m a", Locale.getDefault()).format(new Date()).replaceAll("vorm.", "am").replaceAll("nachm.", "pm"));
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
		String picurl = String.valueOf(getIconid());
		debug(picurl);
		return picurl;
	}


	public void setUrl(String url) {
		this.url = url;
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


	public String getShortString() {
		return "Temp: "+getTemperature()+"°"+getTempUnit()+" \nHum: "+getHumidity();
	}

	@Override
	public URL getUrl(Location lo) {
		long woeid=0;
		//getWoeID from yahoos geocoding api
		try {
			woeid = getWoeID(lo);
			return new URL("http://weather.yahooapis.com/forecastrss?w="
					+ String.valueOf(woeid) + "&u=c");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		//get weather xml for current location
	}

	@Override
	public ArrayList<String> getForecastPics() {
		ArrayList<String> al=new ArrayList<String>();
		for(int i=0;i<wwf.size();i++) {
			debug(wwf.get(i).getIcon());
			al.add(getPicUrl(wwf.get(i).getIcon()));
		}
		return al;
	}

	public String getPicUrl(String icon) {
		return "http://l.yimg.com/us.yimg.com/i/us/nws/weather/gr/"+icon+"d.png";
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	
}
