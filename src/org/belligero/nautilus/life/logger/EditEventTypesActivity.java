package org.belligero.nautilus.life.logger;

import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.ConfirmResultListener;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.utils.Utils;
import org.belligero.nautilus.life.logger.views.EditEventTypeLineView;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class EditEventTypesActivity extends Activity {
	@SuppressWarnings( "unused" )
	private static final String
		TAG = "LifeLogger::EditEvents";
	private static final short
		ASK_DELETE = 0,
		CONFIRM_DELETE = 1,
		NO_DELETE = 2;

	private DatabaseAdapter _dbHelper;
	private LinearLayout _eventTypeLines;
	
	private int _numSelected;
	private short _confirmDelete = ASK_DELETE;
	private boolean _deletingSome = false;
	
	private Button _btn_save,
					_btn_cancel,
					_btn_add;
	private ImageButton _btn_delete;
	
	/********************************** Lifecycle Functions ********************************************/
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_event_types);
		
		_dbHelper = new DatabaseAdapter(this).open();
		_eventTypeLines = (LinearLayout) findViewById( R.id.container_editEventType );
		
		_btn_save = (Button) findViewById( R.id.btn_save );
		_btn_delete = (ImageButton) findViewById( R.id.btn_delete );
		_btn_cancel = (Button) findViewById( R.id.btn_cancel );
		_btn_add = (Button) findViewById( R.id.btn_add );
		
		_btn_save.setOnClickListener( btnClick );
		_btn_delete.setOnClickListener( btnClick );
		_btn_cancel.setOnClickListener( btnClick );
		_btn_add.setOnClickListener( btnClick );
		
		_btn_delete.setEnabled( false );
		
		fillData();
	}
	
	public void finish( boolean saved ) {
		_dbHelper.close();
		
		setResult(
				saved ? RESULT_OK : RESULT_CANCELED
			);
		super.finish();
	}
    
    private void fillData() {    	
    	_eventTypeLines.removeAllViews();
    	
    	EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAll();
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
    
    /********************************** Private Functions **********************************************/
    private void addRow() {
    	EventType eventType = new EventType(
    			0,
    			true,
    			this.getString( R.string.default_eventTypeName )
			);
    	EditEventTypeLineView eventLine = new EditEventTypeLineView(
    			this,
    			eventType
			);
    	
    	_eventTypeLines.addView( eventLine );
    }
    
    private void saveData() {
    	int count = _eventTypeLines.getChildCount();
    	EditEventTypeLineView eventLine;
    	
    	// Confirm that we want to delete things
    	if ( _deletingSome && _confirmDelete == ASK_DELETE ) {
    		Utils.confirm(
    				this,
    				getString( R.string.confirm_deleteTitle ),
    				getString( R.string.confirm_delete ),
    				confirmDeleteListener
				);
    		
    		return;
    	}
    	
    	// Loop over the lines and save them
    	for (int i = 0; i < count; i++) {
    		eventLine = (EditEventTypeLineView) _eventTypeLines.getChildAt( i );
    		saveRowData( eventLine );
    	}

    	finish( true );
    }
    
    private void saveRowData( EditEventTypeLineView eventLine ) {
    	EventType eventType = eventLine.getEventType();
    	
    	if ( eventLine.isDeleted() ) {
    		// Only delete if it's not new, and we've confirmed we want to delete
    		if ( eventType.getID() > 0 && _confirmDelete == CONFIRM_DELETE ) {
    			_dbHelper.eventTypeHandler.delete( eventType );
    		}
    	} else {
    		if ( eventType.getID() == 0 ) { // New
        		eventType = _dbHelper.eventTypeHandler.insert( eventType );
        		eventLine.setEventType( eventType );
        	} else { // Updating
        		_dbHelper.eventTypeHandler.update( eventType );
        	}	
    	}
    }
    
    private void flagDeletedRows() {
    	int count = _eventTypeLines.getChildCount();
    	EditEventTypeLineView eventLine;
    	
    	for (int i = 0; i < count; i++) {
    		eventLine = (EditEventTypeLineView)_eventTypeLines.getChildAt( i );
    		
    		_deletingSome = ( _deletingSome || eventLine.isSelected() ); // Flag that we are deleting some
    		eventLine.setDeleted( eventLine.isSelected() );
    	}
    }
    
    /********************************** Event Listeners ************************************************/
    private OnClickListener btnClick = new OnClickListener() {
		
		public void onClick( View v ) {
			switch ( v.getId() ) {
			
			case R.id.btn_save:
				saveData();
				break;
			
			case R.id.btn_cancel:
				finish( false );
				break;
			
			case R.id.btn_delete:
				flagDeletedRows();
				break;
				
			case R.id.btn_add:
				addRow();
				break;
			}
		}
	};
	
	private ConfirmResultListener confirmDeleteListener = new ConfirmResultListener() {
		
		public void deny() {
			_confirmDelete = NO_DELETE;
			saveData();
		}
		
		public void confirm() {
			_confirmDelete = CONFIRM_DELETE;
			saveData();
		}
	};
}
