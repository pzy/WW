package org.d1sturbed.ww;


import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class WWActivity extends Activity {

	
	
	public WWActivity() {
	}
	
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
		Log.v("icky", extras.get("h").toString());
		WWHandler h=(WWHandler) extras.getSerializable("h");
		Bitmap b=(Bitmap)extras.getParcelable("b");
		TextView tv= (TextView) findViewById(R.id.widget_textview);
		ImageView iv= (ImageView) findViewById(R.id.widget_imageview);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setText(h.getLocation()+"\n" + h.getDesc()+"\n"+h.getUrl()+"\nVersion: "+ver+"-"+name);
		iv.setImageBitmap(b);
		super.onCreate(savedInstanceState);
	}


}
