package org.d1sturbed.ww;

import android.content.Context;
import android.location.Location;


public class WW2x1 extends WWBaseWidget {
	public static final String TAG = "WW2x1";
	public WW2x1(Context context) {
		this.context=context;
		this.l=R.layout.widget2x1;
	}

	public WW2x1(Context context, WWHandler h) {
		this.context=context;
		this.h=h;
		this.l=R.layout.widget2x1;
	}
	
	public WW2x1(Context context, WWHandler h, Location lo) {
		this.context=context;
		this.h=h;
		this.l=R.layout.widget;
		this.lo=lo;
	}
	
	public WW2x1() {
		this.l=R.layout.widget2x1;
	}
}