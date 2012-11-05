package org.belligero.nautilus.life.logger.views;

import org.belligero.nautilus.life.logger.LifeLoggerActivity;
import org.belligero.nautilus.life.logger.R;
import org.belligero.nautilus.life.logger.ojects.EventType;

import android.content.Context;
import android.view.View;
import android.widget.*;

public class LogEventLineView extends RelativeLayout {
	public static final int BTN_LOG = 1,
							BTN_VIEW = 2;
	
	private EventType _eventType;
	private LifeLoggerActivity _lifeLogger;
	
	public LogEventLineView(Context context, EventType eventType) {
		super(context);
		addView(View.inflate(context, R.layout.view_log_event_line, null));
		
		_eventType = eventType;
		_lifeLogger = (LifeLoggerActivity)context;
		
		// Get our controls
		TextView text_eventName = (TextView)this.findViewById(R.id.text_eventName);
		Button btn_log = (Button)this.findViewById(R.id.btn_log);
		Button btn_viewRecent = (Button)this.findViewById(R.id.btn_viewRecent);
		
		// Set the text
		text_eventName.setText(eventType.getName());
		
		// Add the event handlers
		btn_log.setOnClickListener(btnOnClickListener);
		btn_viewRecent.setOnClickListener(btnOnClickListener);
	}
	
	private OnClickListener btnOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_log:
				_lifeLogger.logEvent(_eventType);
				break;
			case R.id.btn_viewRecent:
				_lifeLogger.showRecent(_eventType);
				break;
			default:
				throw new RuntimeException("Unknown OnClick");
			}
		}
	};
}
