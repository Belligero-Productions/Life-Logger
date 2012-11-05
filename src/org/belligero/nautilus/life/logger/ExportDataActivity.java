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
import org.belligero.nautilus.life.logger.views.ExportEventLineView;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExportDataActivity extends Activity {
	private static final String
		TAG = "LifeLogger::EditEvents";

	private DatabaseAdapter _dbHelper;
	
	private CheckBox[] _checkBoxes;
	private TextView[] _eventNames;
	private Button _btn_export;
	private EditText _edit_fileName;
	
	private LinearLayout _exportEventLines;
	
	/********************************** Lifecycle Functions ********************************************/
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_data);

		_dbHelper = new DatabaseAdapter(this).open();
		
		_exportEventLines = (LinearLayout) findViewById( R.id.container_exportEvents );
		_btn_export = (Button) findViewById( R.id.btn_exportData );
		_edit_fileName = (EditText) findViewById( R.id.edit_fileName );
		
    	
    	// Setup handlers
    	_btn_export.setOnClickListener(
    			new View.OnClickListener() {
					public void onClick(View v) {
						exportData();
					}
    			}
			);
		
		fillData();
	}
	
	public void finish() {
		_dbHelper.close();
		super.finish();
	}

	/*************************************** Helper Functions ******************************************/
    private void fillData() {
    	_exportEventLines.removeAllViews();
    	
    	EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();
    	for (EventType eventType : iterator) {
    		_exportEventLines.addView(
    				new ExportEventLineView( this, eventType )
				);
    	}
    }

	/*************************************** Export Functions ******************************************/
    private boolean exportData() {
    	boolean ret = false;
    	PrintStream printStream;
    	String fileName = _edit_fileName.getText().toString();
    	
    	try {
    		File root = Environment.getExternalStorageDirectory();
    		if (root.canWrite()) {
    			printStream = new PrintStream( root + "/" + fileName );
    			String output = getOutputString();
    			printStream.print( output );
    			
    			Toast.makeText(
    					this,
    					R.string.export_complete,
    					Toast.LENGTH_SHORT
					).show();
    			
    			ret = true;
    		} else {
    			Toast.makeText(
    					this,
    					R.string.no_storage,
    					Toast.LENGTH_SHORT
					).show();
    		}
    	} catch (Exception e) {
			Toast.makeText(
					this,
					R.string.export_error,
					Toast.LENGTH_SHORT
				).show();
    		e.printStackTrace();
    		ret = false;
    	}
    	
    	return ret;
    }
    
    private String getOutputString() {
    	StringBuilder ret = new StringBuilder();
    	Calendar cal = Calendar.getInstance();
    	
    	ExportEventLineView eventLine;
    	EventType eventType;
    	
    	int count = _exportEventLines.getChildCount();
    	for (int i = 0; i < count; i++) {
    		eventLine = (ExportEventLineView) _exportEventLines.getChildAt( i );
    		eventType = eventLine.getEventType();
    		
    		if ( eventLine.isSelected() ) {
    			
    			EventIterator iterator = _dbHelper.eventHandler.fetchAllEventsOfType( i+1 );
    			for ( Event event : iterator ) {
					cal.setTimeInMillis( event.getTimeStamp() * 1000 );
					ret.append(
							'"' + Utils.getDateString(cal) + ' ' + Utils.getTimeString(cal)
							+ "\",\"" + eventType.getName()
							+ "\"\n"
						);
    			} // if
				ret.append('\n');
    		} // if
    	} // for
    	
    	return ret.toString();
    }
}
