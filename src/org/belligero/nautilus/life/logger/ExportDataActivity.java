package org.belligero.nautilus.life.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

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
		// TODO Find a better way of doing this, something with a list
    	_checkBoxes = new CheckBox[]{
    			(CheckBox)findViewById(R.id.check_export1),
    			(CheckBox)findViewById(R.id.check_export2),
    			(CheckBox)findViewById(R.id.check_export3),
    			(CheckBox)findViewById(R.id.check_export4),
    			(CheckBox)findViewById(R.id.check_export5),
    	};
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
    	Cursor cursor = _dbHelper.fetchEventTypes();
    	cursor.moveToFirst();
    	for (int i = 0; i < 5; i++) {
    		fillRowData(cursor, _checkBoxes[i], _eventNames[i]);
    		cursor.moveToNext();
    	}
    	cursor.close();
    }
    
    private void fillRowData(Cursor cursor, CheckBox checkBox, TextView textField) {
    	int active = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ACTIVE));
    	String name = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE_NAME));
    	
    	checkBox.setChecked(active != 0);
    	textField.setText(name);
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
    	Cursor cursor;
    	String name;
    	Calendar cal = Calendar.getInstance();
    	
    	for (int i = 0; i < 5; i++) {
    		if (_checkBoxes[i].isChecked()) {
    			name = _eventNames[i].getText().toString();
    			cursor = _dbHelper.fetchAllEvents(i+1);
    			if (cursor.moveToFirst()) {
    				do {
    					long time = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TIME));
    					cal.setTimeInMillis(time*1000);
    					String line = "\"" + Utils.getDateString(cal) + ' ' + Utils.getTimeString(cal) + "\",\"" + name + "\"\n";
    					ret.append(line);
    				} while (cursor.moveToNext());
    				ret.append('\n');
    			} // if
    		} // if
    	} // for
    	
    	return ret.toString();
    }
}
