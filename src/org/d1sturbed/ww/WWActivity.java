package org.d1sturbed.ww;


import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class WWActivity extends ListActivity {
	private String TAG = "WWActivity";
	private boolean DEBUG = WW.DEBUG;
	
	
	public WWActivity() {
	}
	
	@SuppressWarnings("unused")
	private void debug(String s) {
		if(DEBUG) {
			Log.v(TAG, s);
		}
	}
	
	//show Activity with given data
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(getPackageName());
		Bundle extras=getIntent().getExtras();
		WWBaseHandler h=(WWBaseHandler) extras.getSerializable("h");
		WWAdapter wwa=new WWAdapter(getApplicationContext(), R.layout.dialog, h);
		setListAdapter(wwa);
		super.onCreate(savedInstanceState);
	}


}
