package org.belligero.nautilus.life.logger.views;

import org.belligero.nautilus.life.logger.R;
import org.belligero.nautilus.life.logger.ojects.EventType;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExportEventLineView extends LinearLayout {
	private CheckBox _check_export;
	private TextView _text_eventName;
	
	private EventType _eventType;

	public ExportEventLineView( Context context, EventType eventType ) {
		super( context );
		addView(
				View.inflate(
						context,
						R.layout.view_export_event_line,
						null
					)
			);
		_eventType = eventType;
		
		// Get the controls
		_check_export = (CheckBox) findViewById( R.id.check_export );
		_text_eventName = (TextView) findViewById( R.id.text_eventName );
		
		// Set the display values
		_check_export.setChecked( eventType.isActive() );
		_text_eventName.setText( eventType.getName() );
	}
	
	public EventType getEventType() {
		return _eventType;
	}
	
	public boolean isSelected() {
		return _check_export.isChecked();
	}
	
	public void setSelected( boolean selected ) {
		_check_export.setChecked( selected );
	}
}
