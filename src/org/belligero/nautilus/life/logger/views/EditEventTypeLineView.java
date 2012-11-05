package org.belligero.nautilus.life.logger.views;

import org.belligero.nautilus.life.logger.EditEventTypesActivity;
import org.belligero.nautilus.life.logger.R;
import org.belligero.nautilus.life.logger.ojects.EventType;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class EditEventTypeLineView extends RelativeLayout {
	private EventType _eventType;
	private EditEventTypesActivity _editTypesActivity;
	private boolean _isDeleted = false;
	
	private CheckBox _check_selected;
	private EditText _edit_name;
	private ToggleButton _toggle_isActive;
	
	public EditEventTypeLineView(Context context, EventType eventType) {
		super(context);
		addView(
				View.inflate(
						context,
						R.layout.view_edit_event_type_line,
						null
					)
			);
		
		_eventType = eventType;
		_editTypesActivity = (EditEventTypesActivity)context;
		
		// Get our controls
		_check_selected = (CheckBox)this.findViewById( R.id.check_selectEventType );
		_edit_name = (EditText)this.findViewById( R.id.edit_eventName );
		_toggle_isActive = (ToggleButton)this.findViewById( R.id.toggle_isActive );
		
		// Set their values
		_check_selected.setChecked( false );
		_edit_name.setText( eventType.getName() );
		_toggle_isActive.setChecked( eventType.isActive() );
		
		_check_selected.setOnCheckedChangeListener( new OnCheckedChangeListener() {
				public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
						_editTypesActivity.eventTypeSelected( isChecked );
					}
				}
			);
	}

	/**
	 * Get an event type containing the new, edited, values.
	 * @return EventType containing the new edited values.
	 */
	public EventType getEventType() {
		EventType ret = new EventType(
				_eventType.getID(),
				_toggle_isActive.isChecked(),
				_edit_name.getText().toString()
			);
		
		return ret;
	}
	
	public void setEventType( EventType eventType ) {
		_eventType = eventType;

		_edit_name.setText( eventType.getName() );
		_toggle_isActive.setChecked( eventType.isActive() );
	}
	
	public boolean isDeleted() {
		return _isDeleted;
	}
	
	public void setDeleted( boolean isDeleted ) {
		_isDeleted = isDeleted;
		_check_selected.setEnabled( !isDeleted );
		_edit_name.setEnabled( !isDeleted );
		_toggle_isActive.setEnabled( !isDeleted );
		
		if (isDeleted) {
			// Add strikethrough
			_edit_name.setPaintFlags( _edit_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
			_check_selected.setChecked( false ); // unselect it, so it doesn't get processed again later
			_toggle_isActive.setChecked( false );
		} else {
			// Remove strikethrough
			_edit_name.setPaintFlags( _edit_name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG );
		}
	}
	
	/**
	 * Whether this Event Type has been selected (via checkbox) or not.
	 * @return boolean true if selected, false if not
	 */
	public boolean isSelected() {
		return _check_selected.isChecked();
	}
	
	public void setSelected( boolean isSelected ) {
		_check_selected.setChecked( isSelected );
	}
}
