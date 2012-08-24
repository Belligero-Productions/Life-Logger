package org.belligero.nautilus.life.logger.views;

import org.belligero.nautilus.life.logger.LifeLoggerActivity;
import org.belligero.nautilus.life.logger.R;

import android.content.Context;
import android.view.View;
import android.widget.*;

public class EventButtonView extends RelativeLayout {
	public static final int BTN_LOG = 1,
							BTN_VIEW = 2;
	private Button btn_log,
				btn_view;
	private int _id;
	private String _text;
	private LifeLoggerActivity _lifeLogger;
	
	public EventButtonView(Context context, int id, String text) {
		super(context);
		
		_id = id;
		_text = text;
		_lifeLogger = (LifeLoggerActivity)context;

		// View button
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		btn_view = new Button(context);
		btn_view.setId(BTN_VIEW);
		btn_view.setText(R.string.view);
		addView(btn_view, params);
		
		// Log button
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.LEFT_OF, BTN_VIEW);
		
		btn_log = new Button(context);
		btn_log.setId(BTN_LOG);
		btn_log.setText(text);
		addView(btn_log, params);
		
		// Button Handlers
		btn_log.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				_lifeLogger.logEvent(_id, _text);
			}
		});
		
		btn_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				_lifeLogger.showRecent(_id, _text);
			}
		});
	}
}
