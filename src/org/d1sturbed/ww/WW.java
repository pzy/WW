package org.d1sturbed.ww;


import android.content.Context;


public class WW extends WWBaseWidget {
	
	public WW(Context context) {
		this.context=context;
		this.l=R.layout.widget;
	}

	public WW(Context context, WWHandler h) {
		this.context=context;
		this.h=h;
		this.l=R.layout.widget;
	}
	
	public WW() {
		this.l=R.layout.widget;
	}


}
