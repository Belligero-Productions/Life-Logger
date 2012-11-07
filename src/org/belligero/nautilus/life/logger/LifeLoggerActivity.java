package org.belligero.nautilus.life.logger;

import java.util.Calendar;
import java.util.HashMap;

import org.belligero.nautilus.life.logger.ojects.Event;
import org.belligero.nautilus.life.logger.ojects.EventIterator;
import org.belligero.nautilus.life.logger.ojects.EventType;
import org.belligero.nautilus.life.logger.ojects.EventTypeIterator;
import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.utils.EventListLoader;
import org.belligero.nautilus.life.logger.utils.Utils;
import org.belligero.nautilus.life.logger.views.LogEventLineView;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class LifeLoggerActivity extends Activity {
	@SuppressWarnings( "unused" )
	private static final String
		TAG = "LifeLogger";
	private static final int
		DIALOG_ID_DATE = 1,
		DIALOG_ID_TIME = 2;
	
	private Button btn_date,
				btn_time;
	
	private int _year, _month, _day,
				_hour, _minute;
	
	private DatabaseAdapter _dbHelper;
	private EventListLoader _eventListLoader;
	
	private LinearLayout _logButtons;
	
	private static LifeLoggerActivity instance;

	/********************************** Life cycle Functions *******************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_events);
		
		_dbHelper = new DatabaseAdapter(this).open();
		
		_eventListLoader = new EventListLoader(
				this,
				(ViewGroup) findViewById( R.id.container_recent )
			);
		
		// Setup the logging buttons
		_logButtons = (LinearLayout) findViewById(R.id.container_logButtons);
		loadData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setupDateTime();
	}
	
	/*************************************** Public Functions ******************************************/
	public void logEvent( EventType eventType ) {
		Calendar cal = Calendar.getInstance();
		cal.set(_year, _month, _day, _hour, _minute, 0);
		
		Event event = new Event(
				eventType,
				cal.getTimeInMillis() / 1000
			);
		
		_dbHelper.eventHandler.insertEvent( event );
		
		_eventListLoader.addRow( event, eventType, true );
	}
	
	public void showRecent() {
		showRecent(null);
	}
	
	public void showRecent( EventType eventType ) {		
		EventIterator iterator;
		if ( eventType == null ) {
			iterator = _dbHelper.eventHandler.fetchRecentEvents();
		} else {
			iterator = _dbHelper.eventHandler.fetchRecentEvents( eventType.getID() );
		}

		EventListLoader loader
				= new EventListLoader(
						this,
						(ViewGroup) this.findViewById( R.id.container_recent )
					);
		loader.populateList( iterator );
	}
	
	public static void refresh() {
		LifeLoggerActivity.instance.loadData();
	}
	
	/*************************************** Helper Functions ******************************************/
	private void loadData() {
		if (LifeLoggerActivity.instance == null) LifeLoggerActivity.instance = this;
		
		_logButtons.removeAllViews();

		Calendar cal = Calendar.getInstance();
		
		// Load the buttons
		EventTypeIterator eventTypeIterator = _dbHelper.eventTypeHandler.fetchAllEventTypes();
		for (EventType eventType : eventTypeIterator) {
			if (eventType.isActive()) {
				_logButtons.addView(
						new LogEventLineView(
								this,
								eventType
							)
					);
			}
		}
		
		// Load recent logs
		showRecent();
	}
	
	private void setupDateTime() {
		btn_date = (Button)findViewById(R.id.btn_date);
		btn_time = (Button)findViewById(R.id.btn_time);
		
		final Calendar cal = Calendar.getInstance();
		_year = cal.get(Calendar.YEAR);
		_month = cal.get(Calendar.MONTH);
		_day = cal.get(Calendar.DAY_OF_MONTH);
		_hour = cal.get(Calendar.HOUR_OF_DAY);
		_minute = cal.get(Calendar.MINUTE);
		updateDateTime();
		
		btn_date.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings( "deprecation" )
			public void onClick(View v) {
				showDialog(DIALOG_ID_DATE);
			}
		});
		
		btn_time.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings( "deprecation" )
			public void onClick(View v) {
				showDialog(DIALOG_ID_TIME);
			}
		});
	}

	private void updateDateTime() {
		btn_date.setText(Utils.getDateString(_year, _month, _day));
		btn_time.setText(Utils.getTimeString(_hour, _minute));
	}

	/*************************************** Handlers **************************************************/
	
	/*************************************** Dialogs ***************************************************/
	// Launch the various dialogs
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ID_DATE:
			return new DatePickerDialog(this, _dateSetListener, _year, _month, _day);
		case DIALOG_ID_TIME:
			return new TimePickerDialog(this, _timeSetListener, _hour, _minute, true); // true = is 24 hour
		}
		return null;
	}
	private DatePickerDialog.OnDateSetListener _dateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			_year = year;
			_month = monthOfYear;
			_day = dayOfMonth;
			updateDateTime();
		}
	};
	
	private TimePickerDialog.OnTimeSetListener _timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			_hour = hourOfDay;
			_minute = minute;
			updateDateTime();
		}
	};
}