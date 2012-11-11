package org.belligero.nautilus.life.logger;

import java.io.File;
import java.io.PrintStream;
import java.util.Calendar;

import org.belligero.nautilus.LifeLogger;
import org.belligero.nautilus.life.logger.ojects.Event;
import org.belligero.nautilus.life.logger.ojects.EventIterator;
import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.ConfirmResultListener;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.utils.Utils;
import org.belligero.nautilus.life.logger.views.ExportEventLineView;
import org.belligero.nautilus.life.logger.R;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ExportDataActivity extends Activity {
	@SuppressWarnings( "unused" )
	private static final String
		TAG = "LifeLogger::EditEvents";

	private DatabaseAdapter _dbHelper;
	
	private Button _btn_export;
	private EditText _edit_fileName;
	private ViewGroup _exportEventLines;
	
	private boolean _confirmOverwrite = false;
	
	private static ExportDataActivity _instance;
	
	/********************************** Lifecycle Functions ********************************************/
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart( this ); 
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop( this );
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_data);

		_dbHelper = new DatabaseAdapter(this).open();
		_instance = this;
		
		_exportEventLines = (ViewGroup) findViewById( R.id.container_exportEvents );
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
	public static void refresh() {
		if (_instance != null) _instance.fillData();
	}
	
    private void fillData() {
    	_exportEventLines.removeAllViews();
    	
    	EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAll();
    	for (EventType eventType : iterator) {
    		_exportEventLines.addView(
    				new ExportEventLineView( this, eventType )
				);
    	}
    }
    
    protected String getDateString( Calendar cal ) {
    	return String.format(
    			"%4d-%02d-%02d",
    			cal.get( Calendar.YEAR ),
    			cal.get( Calendar.MONTH ),
    			cal.get( Calendar.DAY_OF_MONTH )
			);
    }
    
    protected String getTimeString( Calendar cal ) {
    	return DateUtils.formatDateTime(
    			LifeLogger.getContext(),
    			cal.getTimeInMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR
			);
    }

	/*************************************** Export Functions ******************************************/
    private boolean exportData() {
    	boolean ret = false;
    	PrintStream printStream;
    	String fileName = _edit_fileName.getText().toString();
    	
    	try {
    		File root = Environment.getExternalStorageDirectory();
    		String filePath = root + "/" + fileName;
    		if (root.canWrite()) {
    			File outFile = new File( filePath );
    			// Make sure we want to overwrite
    			if ( !_confirmOverwrite && outFile.exists() ) {
    				Utils.confirm(
    						this,
    						R.string.confirm_overwriteTitle,
    						R.string.confirm_overwrite,
    						confirmOverwriteListener
						);
    				return false;
    			}
    			
    			printStream = new PrintStream( filePath );
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
    	
    	_confirmOverwrite = false;
    	return ret;
    }
    
    private String getOutputString() {
    	// TODO Run this in a background thread
    	StringBuilder ret = new StringBuilder();
    	Calendar cal = Calendar.getInstance();
    	
    	ExportEventLineView eventLine;
    	EventType eventType;
    	
    	int count = _exportEventLines.getChildCount();
    	for (int i = 0; i < count; i++) {
    		eventLine = (ExportEventLineView) _exportEventLines.getChildAt( i );
    		eventType = eventLine.getEventType();
    		
    		if ( eventLine.isSelected() ) {
    			
    			EventIterator iterator = _dbHelper.eventHandler.fetchByType( i+1 );
    			for ( Event event : iterator ) {
					cal.setTimeInMillis( event.getTimeStamp() * 1000 );
					
					ret.append(
							'"' + this.getDateString( cal )
							+ "\",\"" + this.getTimeString( cal )
							+ "\",\"" + eventType.getName()
							+ "\"\n"
						);
    			} // if
				ret.append('\n');
    		} // if
    	} // for
    	
    	return ret.toString();
    }
    

	/*************************************** Handlers **************************************************/
    
    private ConfirmResultListener confirmOverwriteListener = new ConfirmResultListener() {
		
		public void deny() {
			_confirmOverwrite = false;
		}
		
		public void confirm() {
			_confirmOverwrite = true;
			exportData();
		}
	};
}
