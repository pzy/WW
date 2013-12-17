package org.d1sturbed.ww;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Intent intent = new Intent();
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.update:
			Intent mintent = new Intent(this, WWUpdate.class);
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location myl=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			mintent.setAction(WWUpdate.UPDATE);
			mintent.putExtra("location", myl);
			mintent.putExtra("className", this.getClass().getName());
			startService(mintent);	
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	//show Activity with given data
	@Override
	public void onCreate(Bundle savedInstanceState) {
		WWAdapter wwa=null;
		WWBaseHandler h=null;
		Bundle extras=getIntent().getExtras();
		setTitle(getPackageName());
		if(extras!=null) {
			h=(WWBaseHandler) extras.getSerializable("h");
		}
		if(h!=null) {
			wwa=new WWAdapter(getApplicationContext(), R.layout.dialog, h);
		}
		if(wwa!=null) {
			setListAdapter(wwa);
		}
		super.onCreate(savedInstanceState);
	}


}
