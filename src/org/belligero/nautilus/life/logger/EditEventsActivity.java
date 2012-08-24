package org.belligero.nautilus.life.logger;

import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class EditEventsActivity extends Activity {
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
    	Cursor cursor = _dbHelper.fetchEventTypes();
    	cursor.moveToFirst();
    	for (int i = 0; i < 5; i++) {
    		fillRowData(cursor, _checkBoxes[i], _editTexts[i]);
    		cursor.moveToNext();
    	}
    	cursor.close();
    }
    
    private void fillRowData(Cursor cursor, CheckBox checkBox, EditText textField) {
    	int active = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ACTIVE));
    	String name = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE_NAME));
    	
    	checkBox.setChecked(active != 0);
    	textField.setText(name);
    }
    
    private void saveData() {
    	for (int i=0; i < 5; i++) {
    		saveRowData(i+1, _checkBoxes[i], _editTexts[i]);
    	}
    }
    
    private void saveRowData(int id, CheckBox checkBox, EditText textField) {
    	boolean active = checkBox.isChecked();
    	String name = textField.getText().toString();
    	
    	_dbHelper.editEventType(id, name, active);
    }
}
