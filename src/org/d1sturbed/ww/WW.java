package org.d1sturbed.ww;


import android.content.Context;
import android.location.Location;


public class WW extends WWBaseWidget {
	
	public WW(Context context) {
		this.context=context;
		this.l=R.layout.widget;
	}

	public WW(Context context, WWBaseHandler h) {
		this.context=context;
		this.h=h;
		this.l=R.layout.widget;
	}
	

	public WW(Context context, WWBaseHandler h, Location lo) {
		this.context=context;
		this.h=h;
		this.l=R.layout.widget;
		this.lo=lo;
	}
	
	public WW() {
		this.l=R.layout.widget;
	}


}
