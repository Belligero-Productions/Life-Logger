package org.belligero.nautilus.life.logger;

import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.views.EditEventTypeLineView;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EditEventTypesActivity extends Activity {
	private DatabaseAdapter _dbHelper;
	private static final String TAG = "LifeLogger::EditEvents";
	
	private LinearLayout _eventTypeLines;
	
	private int _numSelected;
	
	private Button _btn_save,
					_btn_delete,
					_btn_cancel;
	
	/********************************** Lifecycle Functions ********************************************/
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_event_types);
		
		_dbHelper = new DatabaseAdapter(this).open();
		_eventTypeLines = (LinearLayout) findViewById( R.id.container_editEventType );
		
		_btn_save = (Button) findViewById( R.id.btn_save );
		_btn_delete = (Button) findViewById( R.id.btn_delete );
		_btn_cancel = (Button) findViewById( R.id.btn_cancel );
		
		fillData();
	}
	
	public void finish() {
		_dbHelper.close();
		super.finish();
	}
    
    private void fillData() {    	
    	_eventTypeLines.removeAllViews();
    	
    	EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();
    	for (EventType eventType : iterator) {
    		_eventTypeLines.addView(
    				new EditEventTypeLineView(
    						this,
    						eventType
    					)
    			);
    	}
    }
    
    public void eventTypeSelected( boolean isSelected ) {
    	if (isSelected) {
    		_numSelected++;
    	} else {
    		_numSelected--;
    	}
    	
    	_btn_delete.setEnabled( _numSelected > 0 );
    }
    
    private void saveData() {
    	int count = _eventTypeLines.getChildCount();
    	EditEventTypeLineView eventLine;
    	EventType eventType;
    	
    	for (int i = 0; i < count; i++) {
    		eventLine = (EditEventTypeLineView) _eventTypeLines.getChildAt( i );
    		saveRowData( eventLine );
    	}
    	
    	// TODO Toast popup indicating they've been saved
    }
    
    private void saveRowData(EditEventTypeLineView eventLine) {
    	EventType eventType = eventLine.getEventType();
    	
    	// TODO Delete things
    	if ( eventType.getID() == 0 ) {
    		eventType = _dbHelper.eventTypeHandler.insertEventType( eventType );
    		eventLine.setEventType( eventType );
    	} else {
    		_dbHelper.eventTypeHandler.updateEventType( eventType );
    	}
    }
    
    private OnClickListener btnClick = new OnClickListener() {
		
		public void onClick( View v ) {
			switch ( v.getId() ) {
			
			case R.id.btn_save:
				saveData();
			case R.id.btn_cancel:
				finish();
				break;
			
			case R.id.btn_delete:
				// TODO Delete things
				break;
			}
		}
	};
}
