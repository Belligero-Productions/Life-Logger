package org.belligero.nautilus.life.logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.belligero.nautilus.life.logger.ojects.*;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
	@SuppressWarnings( "unused" )
	private static final String
		TAG = "Statistics";
	private static final int
		DIALOG_PROGRESS = 1;
	
	public static StatisticsActivity _instance;
	
	private Spinner _spinner_events;
	private ProgressDialog _progressDialog;
	
	private String _eventTypeName;
	
	private DatabaseAdapter _dbHelper;

	/********************************** Life cycle Functions *******************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);

		_dbHelper = new DatabaseAdapter(this).open();
		_instance = this;
		
		_spinner_events = (Spinner)findViewById(R.id.spinner_selectEvent);
		setupSpinner();
	}
	
	/*************************************** Public Functions ******************************************/
	public static void refresh() {
		if (_instance != null) _instance.setupSpinner();
	}
	
	
	/*************************************** Helper Functions ******************************************/
	private void setupSpinner() {
		List<CharSequence> arr = new ArrayList<CharSequence>();
		
		EventTypeIterator iterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();
		for (EventType eventType : iterator) {
			arr.add( eventType.getName() );
		}

		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this,
				android.R.layout.simple_spinner_item,
				arr
			);
		
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		_spinner_events.setAdapter( adapter );
		_spinner_events.setOnItemSelectedListener( new OnEventSelectedListener() );
	}
	
	
	/*************************************** Handlers **************************************************/
	private class OnEventSelectedListener implements OnItemSelectedListener {
		@SuppressWarnings( "deprecation" )
		public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
			_eventTypeName = parent.getItemAtPosition( position ).toString();
			showDialog( DIALOG_PROGRESS );
		}

		public void onNothingSelected( AdapterView<?> arg0 ) {
			// Do nothing
		}
	}
	
	/*************************************** Dialogs ***************************************************/
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			_progressDialog = new ProgressDialog( this );
			_progressDialog.setMessage(
					this.getText( R.string.stats_calculating )
				);
			_progressDialog.setCancelable( false );
			
			return _progressDialog;
		default:
			return null;
		}
	}
	
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_PROGRESS:
			if (_eventTypeName != null) { // Fix for this event being called before the list is populated
				StatsCalculator calc = new StatsCalculator( handler, _eventTypeName );
				calc.start();
			}
		}
	}
	
	/*************************************** Statistics Calculator *************************************/
	// TODO Make this static (see warning on StatsCalculator::_handler)
	final Handler handler = new Handler() {
		@SuppressWarnings( "deprecation" )
		public void handleMessage(Message msg) {
			HashMap<String, Integer> values = (HashMap<String, Integer>)msg.obj;
			
			
			int value = values.get("total").intValue();
			TextView view = (TextView) findViewById( R.id.stats_total );
			view.setText(
					Float.toString( value )
				);

			value = values.get("totalDailyAverage").intValue();
			float display = value/10f;
			view = (TextView) findViewById( R.id.stats_totalDailyAverage );
			view.setText(
					Float.toString( display )
				);

			value = values.get("activeDailyAverage").intValue();
			display = value / 10f;
			view = (TextView) findViewById( R.id.stats_activeDailyAverage );
			view.setText(
					Float.toString( display )
				);
			
			StatisticsActivity.this.dismissDialog( DIALOG_PROGRESS );
		}
	};
	
	private class StatsCalculator extends Thread {
		Handler _handler;
		private String _eventTypeName;
		
		StatsCalculator(Handler h, String eventTypeName) {
			_handler = h;
			_eventTypeName = eventTypeName;
		}
		
		public void run() {
			EventIterator iterator = _dbHelper.eventHandler.fetchAllEventsOfType( _eventTypeName );
			
			Calendar startDate = null, endDate = null, tempDate;
			Calendar currDate = Calendar.getInstance();
			long time;
			int activeDays = 0,
				totalDays = 0,
				totalEvents = 0;

			// Loop through events to find the number of "Active" days
			for (Event event : iterator) {
				totalEvents++;
				time = event.getTimeStamp() * 1000L;
				tempDate = Calendar.getInstance();
				tempDate.setTimeInMillis(time);
				
				// First time in loop, set up everything
				if (startDate == null) {				
					startDate = tempDate;
					endDate = tempDate;
					activeDays = 1;
				} else if (
							tempDate.get(Calendar.DAY_OF_YEAR) != endDate.get(Calendar.DAY_OF_YEAR)
							|| tempDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR)
						) {
					endDate = tempDate;
					activeDays++;
				}
			}

			// Calculate the total numbers of days, since the first entry
			if (startDate != null) {
				totalDays = (int)((currDate.getTimeInMillis() - startDate.getTimeInMillis()) / (1000 * 60 * 60 * 24));
			}
			
			// Avoid potential division by 0
			if (totalDays == 0) totalDays = 1;
			if (activeDays == 0) activeDays = 1;
			
			HashMap<String, Integer> values = new HashMap<String, Integer>();
			values.put("total", totalEvents);
			values.put("totalDailyAverage", (totalEvents*10)/totalDays);
			values.put("activeDailyAverage", (totalEvents*10)/activeDays);
			
			Message msg = new Message();
			msg.obj = values;
			_handler.sendMessage( msg );
		}
	}
}
