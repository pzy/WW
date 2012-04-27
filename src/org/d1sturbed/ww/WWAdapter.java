package org.d1sturbed.ww;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class WWAdapter extends ArrayAdapter<WWForecast> implements ListAdapter {
	ArrayList<WWForecast> wwf;
	ArrayList<Bitmap> ba = new ArrayList<Bitmap>();
	WWBaseHandler h;
	Context context;

	public WWAdapter(Context context, int view, WWBaseHandler h) {
		super(context, view, h.getWwf());
		this.context = context;
		this.h = h;
		this.wwf = h.getWwf();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		WWForecast p = null;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.dialog, null);
		}
		TextView tt = (TextView) v.findViewById(R.id.high);
		TextView bt = (TextView) v.findViewById(R.id.low);
		TextView ct = (TextView) v.findViewById(R.id.cond);
		TextView st = (TextView) v.findViewById(R.id.day);
		ImageView image = (ImageView) v.findViewById(R.id.imageview);
		p = wwf.get(position);
		if (p != null) {
			if (image!=null && p.getIcon()!=null) {
				File tmp=null;
				File f =null;
				tmp = new File(p.getIcon());
				File cacheDir = context.getCacheDir();
				if(tmp!=null) {
					f = new File(cacheDir, tmp.getName());
				}
                if(f!=null && f.exists()) {
                	try {
						image.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
					} catch (FileNotFoundException e) {
	                	image.setImageBitmap(get(h.getPicUrl(p.getIcon())));						
					}
                } else {
                	image.setImageBitmap(get(h.getPicUrl(p.getIcon())));
                }
			}
			if (tt != null) {
				tt.setTextColor(Color.RED);
				tt.setText(context.getResources().getString(R.string.low)
						+ ": " + String.format("%d", p.getHigh()) + " "
						+ context.getResources().getString(R.string.unit));
			}
			if (bt != null) {
				bt.setTextColor(Color.BLUE);
				bt.setText(context.getResources().getString(R.string.low)
						+ ": " + String.format("%d", p.getLow()) + " "
						+ context.getResources().getString(R.string.unit));
			}
			if (ct != null) {
				ct.setText(p.getCondition());
			}
			if (st != null) {
				st.setText(p.getDay());
			}
		}
		return v;
	}


	public Bitmap get(String img) {
		Bitmap b;
		try {
			URL u2 = new URL(img);

			HttpURLConnection c2 = (HttpURLConnection) u2
					.openConnection();
			c2.setUseCaches(true);
			c2.setDoInput(true);
			c2.connect();
			InputStream i2 = c2.getInputStream();
			b=Bitmap.createScaledBitmap(BitmapFactory.decodeStream(i2), 120,120,false);
			c2.disconnect();
			i2.close();
			
		} catch (Exception e) {
			Log.v("WWAdapter", e.toString());
			b=null;
		}
		return b;

	}
}
