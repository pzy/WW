package org.d1sturbed.ww;


import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class WWActivity extends ListActivity {
	private String TAG = "WWActivity";
	private boolean DEBUG = WW.DEBUG;
	
	
	public WWActivity() {
	}
	
	private void debug(String s) {
		if(DEBUG) {
			Log.v(TAG, s);
		}
	}
	
	//show Activity with given data
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(getPackageName());
		//setContentView(R.layout.dialog);
		Bundle extras=getIntent().getExtras();
		WWBaseHandler h=(WWBaseHandler) extras.getSerializable("h");
		ArrayList<Bitmap> ba=extras.getParcelableArrayList("ba");
		setListAdapter(new WWAdapter(getApplicationContext(), R.layout.dialog, h.getWwf(), ba));
		super.onCreate(savedInstanceState);
		/*Bundle extras=getIntent().getExtras();
		Bitmap b=extras.getParcelable("b");
		ArrayList<Bitmap> ba=extras.getParcelableArrayList("ba");
		String ver  = "Version Information not available";
		String name = "Product Name not available"; 
		try {
			ver = getApplicationContext().getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionName;
			name = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
		} catch (NameNotFoundException e) {
			debug(e.toString());
		}
		WWBaseHandler h=(WWBaseHandler) extras.getSerializable("h");
		debug(b.toString());
		if(ba!=null) {
			debug(ba.toString());
			for (int i=0;i<ba.size();i++) {
				debug(ba.get(i).toString());				
			}
		}
		TextView tv= (TextView) findViewById(R.id.widget_textview);
		ImageView iv= (ImageView) findViewById(R.id.widget_imageview);
		if(b!=null && iv!=null) {
			iv.setImageBitmap(b);
		}
		if(h!=null && tv!=null) {
			tv.setText(h.getDesc()+"\n\nVersion: "+ver+"-"+name);
		}
		tv.setMovementMethod(LinkMovementMethod.getInstance());*/
	}


}
