package org.belligero.nautilus.life.logger.views;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatsRowView extends RelativeLayout {

	public StatsRowView(Context context, String name, String value) {
		super(context);
		
		// Name text
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		
		TextView text = new TextView(context);
		text.setText(name);
		addView(text, params);
		
		// value text
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		text = new TextView(context);
		text.setText(value);
		addView(text, params);
	}

}
