package org.d1sturbed.ww;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class WWAdapter extends ArrayAdapter<WWForecast> implements ListAdapter {
	ArrayList<WWForecast> wwf;
	ArrayList<Bitmap> ba;
	Context context;

	public WWAdapter(Context context, int view, ArrayList<WWForecast> wwf, ArrayList<Bitmap> ba) {
		super(context, view, wwf);
		this.context=context;
		this.wwf=wwf;
		this.ba=ba;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
        	WWForecast p=null;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.dialog, null);
            }
            TextView tt = (TextView) v.findViewById(R.id.high);
            TextView bt = (TextView) v.findViewById(R.id.low);
            TextView ct = (TextView) v.findViewById(R.id.cond);
            TextView st = (TextView) v.findViewById(R.id.day);
            ImageView image = (ImageView) v.findViewById(R.id.imageview);
            p = wwf.get(position);
            Bitmap b=ba.get(position);
            if (p != null) {
        		if (image != null && b != null) {
        			image.setImageBitmap(b);
        		}
                if (tt != null) {
                	tt.setTextColor(Color.RED);
                    tt.setText(String.format("%d", p.getHigh()));           
                }
                if(bt != null){
                	bt.setTextColor(Color.GREEN);
                    bt.setText(String.format("%d", p.getLow()));
                }
                if(ct != null) {
                    ct.setText(p.getCondition());                    	
                }
                if (st != null) {
                	st.setText(p.getDay());
                }
            }
            return v;
    }

}
