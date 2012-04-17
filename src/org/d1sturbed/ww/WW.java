package org.d1sturbed.ww;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public class WW extends WWBaseWidget {
	
	public WW(Context context) {
		this.context=context;
	}

	public WW(Context context, WWHandler h) {
		this.context=context;
		this.h=h;
	}
	
	protected void setWidget(WWHandler ha, Bitmap b) {
		RemoteViews updateViews;
		AppWidgetManager manager;
		ComponentName widget;

		updateViews = new RemoteViews(context.getClass().getName(), R.layout.widget);
		manager = AppWidgetManager.getInstance(context);
		widget = new ComponentName(context, WW.class);
		PendingIntent pendingShowActivityIntent;
		Intent intent = new Intent(context, WW.class);
		intent.setAction(WWBaseWidget.ACTION_START_ACTIVITY);
		intent.putExtra("h", ha);
		intent.putExtra("b", b);
		pendingShowActivityIntent = PendingIntent.getBroadcast(context, new Random().nextInt(),
				intent, 0);
		updateViews.setOnClickPendingIntent(R.id.widget_textview,
				pendingShowActivityIntent);
		try {
			if(b!=null) {
				updateViews.setImageViewBitmap(R.id.widget_imageview, b);
			}
			updateViews.setTextViewText(R.id.widget_textview, "Act:" + ha.getTemperature() + " °"+h.getTempUnit()+"\nN: "+ ha.getLow_temp()+" °"+h.getTempUnit()+"\nH: "+ha.getHigh_temp()+" °"+h.getTempUnit());
		} catch (Exception e) {
			debug("Fehler: " + e.toString());
			updateViews.setTextViewText(R.id.widget_textview, "Fehler:" + e.toString());
		}
		manager.updateAppWidget(widget, updateViews);
	}
	
	public WW() {
		super();
	}	
	public void onReceive(Context context, Intent intent) {
		debug(getClass().getName()+":"+intent.getAction());
		if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
			Intent mintent = new Intent(context, WWUpdate.class);
			mintent.setAction(WWUpdate.UPDATE);
			mintent.putExtra("className", this.getClass().getName());
			context.startService(mintent);
		} else if (intent.getAction().equals(WW.ACTION_WIDGET_SWITCH)) {
			if(intent.hasExtra("h")) {
				WWHandler h= (WWHandler)intent.getExtras().getSerializable("h");
				Thread t=new Thread(new WW(context,h));
				t.start();
			}
		} else if (intent.getAction().equals(WW.ACTION_START_ACTIVITY)) {
			Intent mintent = new Intent(context, WWActivity.class);
			mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			if(intent.hasExtra("h")) {
				WWHandler h=(WWHandler) intent.getSerializableExtra("h");
				mintent.putExtra("h", h);
			}
			if(intent.hasExtra("b")) {
				mintent.putExtra("b", (Bitmap) intent.getParcelableExtra("b"));
			}
			context.startActivity(mintent);
		}
	}
}
