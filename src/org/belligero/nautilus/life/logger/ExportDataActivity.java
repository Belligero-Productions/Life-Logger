package org.belligero.nautilus.life.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

import org.belligero.nautilus.life.logger.ojects.Event;
import org.belligero.nautilus.life.logger.ojects.EventIterator;
import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.utils.Utils;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ExportDataActivity extends Activity {
	private DatabaseAdapter _dbHelper;
	private static final String TAG = "LifeLogger::EditEvents";
	private CheckBox[] _checkBoxes;
	private TextView[] _eventNames;
	private Button btn_export;
	private EditText edit_fileName;
	
	/********************************** Lifecycle Functions ********************************************/
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_data);
		
		btn_export = (Button)findViewById(R.id.btn_exportData);
		edit_fileName = (EditText)findViewById(R.id.edit_fileName);
		_dbHelper = new DatabaseAdapter(this).open();
		
		// TODO Make this use the new dynamic view control
    	_checkBoxes = new CheckBox[]{
    			(CheckBox)findViewById(R.id.check_export1),
    			(CheckBox)findViewById(R.id.check_export2),
    			(CheckBox)findViewById(R.id.check_export3),
    			(CheckBox)findViewById(R.id.check_export4),
    			(CheckBox)findViewById(R.id.check_export5),
    	};
		// TODO Make this use the new dynamic view control
    	_eventNames = new TextView[]{
	    		(TextView)findViewById(R.id.text_exportName1),
	    		(TextView)findViewById(R.id.text_exportName2),
	    		(TextView)findViewById(R.id.text_exportName3),
	    		(TextView)findViewById(R.id.text_exportName4),
	    		(TextView)findViewById(R.id.text_exportName5),
    	};
    	
    	// Setup handlers
    	btn_export.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				exportData();
			}
		});
		
		fillData();
	}
	
	public void finish() {
		_dbHelper.close();
		super.finish();
	}

	/*************************************** Helper Functions ******************************************/
    private void fillData() {    	
    	EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();

		// TODO Make this use the new dynamic view control
    	for (int i = 0; i < 5; i++) {
    		fillRowData(iterator.next(), _checkBoxes[i], _eventNames[i]);
    	}
    }
    
    private void fillRowData(EventType eventType, CheckBox checkBox, TextView textField) {
		// TODO Make this use the new dynamic view control
    	checkBox.setChecked(eventType.isActive());
    	textField.setText(eventType.getName());
    }

	/*************************************** Export Functions ******************************************/
    private boolean exportData() {
    	boolean ret = false;
    	FileOutputStream fileOutStream;
    	PrintStream printStream;
    	String fileName = edit_fileName.getText().toString();
    	
    	try {
    		File root = Environment.getExternalStorageDirectory();
    		if (root.canWrite()) {
    			printStream = new PrintStream(root + "/" + fileName);
    			String output = getOutputString();
    			printStream.print(output);
    			Toast.makeText(this, R.string.export_complete, Toast.LENGTH_SHORT).show();
    			ret = true;
    		} else {
    			Toast.makeText(this, R.string.no_storage, Toast.LENGTH_SHORT).show();
    		}
    	} catch (Exception e) {
			Toast.makeText(this, R.string.export_error, Toast.LENGTH_SHORT).show();
    		e.printStackTrace();
    		ret = false;
    	}
    	
    	return ret;
    }
    
    private String getOutputString() {
    	StringBuilder ret = new StringBuilder();
    	String name;
    	Calendar cal = Calendar.getInstance();
    	
    	for (int i = 0; i < 5; i++) {
    		if (_checkBoxes[i].isChecked()) {
    			name = _eventNames[i].getText().toString();
    			
    			EventIterator iterator = _dbHelper.eventHandler.fetchAllEventsOfType( i+1 );
    			for (Event event : iterator) {
					cal.setTimeInMillis(event.getTimeStamp()*1000);
					ret.append(
							'"' + Utils.getDateString(cal) + ' ' + Utils.getTimeString(cal)
							+ "\",\"" + name
							+ "\"\n"
						);
    			} // if
				ret.append('\n');
    		} // if
    	} // for
    	
    	return ret.toString();
    }
}
