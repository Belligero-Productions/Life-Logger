package org.belligero.nautilus.life.logger.views;

import org.belligero.nautilus.life.logger.LifeLoggerActivity;
import org.belligero.nautilus.life.logger.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class EventButtonView extends RelativeLayout {
	public static final int BTN_LOG = 1,
							BTN_VIEW = 2;
	
	private int _eventTypeID;
	private String _eventName;
	private LifeLoggerActivity _lifeLogger;
	
	public EventButtonView(Context context, int eventTypeID, String eventName) {
		super(context);
		addView(View.inflate(context, R.layout.view_log_buttons, null));
		
		_eventTypeID = eventTypeID;
		_eventName = eventName;
		_lifeLogger = (LifeLoggerActivity)context;
		
		// Get our controls
		TextView text_eventName = (TextView)this.findViewById(R.id.text_eventName);
		Button btn_log = (Button)this.findViewById(R.id.btn_log);
		Button btn_viewRecent = (Button)this.findViewById(R.id.btn_viewRecent);
		
		// Set the text
		text_eventName.setText(_eventName);
		
		// Add the event handlers
		btn_log.setOnClickListener(btnOnClickListener);
		btn_viewRecent.setOnClickListener(btnOnClickListener);
	}
	
	private OnClickListener btnOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_log:
				_lifeLogger.logEvent(_eventTypeID, _eventName);
				break;
			case R.id.btn_viewRecent:
				_lifeLogger.showRecent(_eventTypeID, _eventName);
				break;
			default:
				throw new RuntimeException("Unknown OnClick");
			}
		}
	};
}
