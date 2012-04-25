package org.d1sturbed.ww;


import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class WWActivity extends Activity {

	
	
	public WWActivity() {
	}
	
	
	//show Activity with given data
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(getPackageName());
		setContentView(R.layout.dialog);
		Bundle extras=getIntent().getExtras();
		String ver  = "Version Information not available";
		String name = "Product Name not available"; 
		try {
			ver = getApplicationContext().getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionName;
			name = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
		} catch (NameNotFoundException e) {
			Log.v("WWActivity", e.toString());
		}
		WWBaseHandler h=(WWBaseHandler) extras.getSerializable("h");
		//h.
		TextView tv= (TextView) findViewById(R.id.widget_textview);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setText(h.getDesc()+"\n\nVersion: "+ver+"-"+name);
		super.onCreate(savedInstanceState);
	}


}
