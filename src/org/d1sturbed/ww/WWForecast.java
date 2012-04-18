package org.d1sturbed.ww;

import java.io.Serializable;

public class WWForecast implements Serializable {

	private static final long serialVersionUID = -8778026920747731489L;
	
	private int high;
	private int low;
	private String day;
	private String icon;
	private String condition;
	public int getHigh() {
		return high;
	}
	public void setHigh(int high) {
		this.high = high;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getLow() {
		return low;
	}
	public void setLow(int low) {
		this.low = low;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}

}
