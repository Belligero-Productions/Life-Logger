package org.belligero.nautilus.life.logger;

import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class EditEventTypesActivity extends Activity {
	private DatabaseAdapter _dbHelper;
	private static final String TAG = "LifeLogger::EditEvents";
	private CheckBox[] _checkBoxes;
	private EditText[] _editTexts;
	
	/********************************** Lifecycle Functions ********************************************/
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_events);
		
		_dbHelper = new DatabaseAdapter(this).open();
		// TODO Find a better way of doing this, something with a list
		// TODO Make this use the new dynamic view control
    	_editTexts = new EditText[]{
	    		(EditText)findViewById(R.id.edit_eventName1),
	    		(EditText)findViewById(R.id.edit_eventName2),
	    		(EditText)findViewById(R.id.edit_eventName3),
	    		(EditText)findViewById(R.id.edit_eventName4),
	    		(EditText)findViewById(R.id.edit_eventName5)
    	};
    	_checkBoxes = new CheckBox[]{
    			(CheckBox)findViewById(R.id.check_active1),
    			(CheckBox)findViewById(R.id.check_active2),
    			(CheckBox)findViewById(R.id.check_active3),
    			(CheckBox)findViewById(R.id.check_active4),
    			(CheckBox)findViewById(R.id.check_active5),
    	};
		
		fillData();
	}
	
	public void finish() {
		saveData();	
		_dbHelper.close();
		super.finish();
	}
    
    private void fillData() {    	
    	EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();
    	
    	// TODO Make this use the new dynamic view control
    	for (int i = 0; i < 5; i++) {
    		EventType eventType = iterator.next();
    		fillRowData(
    				eventType,
    				_checkBoxes[i],
    				_editTexts[i]
				);
    	}
    }
    
    private void fillRowData(EventType eventType, CheckBox checkBox, EditText textField) {
    	checkBox.setChecked( eventType.isActive() );
    	textField.setText( eventType.getName() );
    }
    
    private void saveData() {
    	// TODO Make this use the new dynamic view control
    	for (int i=0; i < 5; i++) {
    		saveRowData(i+1, _checkBoxes[i], _editTexts[i]);
    	}
    }
    
    private void saveRowData(int id, CheckBox checkBox, EditText textField) {
    	EventType eventType = new EventType(
    			id,
    			checkBox.isChecked(),
    			textField.getText().toString()
			);
    	
    	_dbHelper.eventTypeHandler.updateEventType( eventType );
    }
}
