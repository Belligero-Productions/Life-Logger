package org.belligero.nautilus.life.logger;

import java.util.Calendar;
import java.util.HashMap;

import org.belligero.nautilus.life.logger.utils.DatabaseAdapter;
import org.belligero.nautilus.life.logger.utils.Utils;
import org.belligero.nautilus.life.logger.views.EventButtonView;
import org.belligero.nautilus.life.logger.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class LifeLoggerActivity extends Activity {
	private static final String TAG = "LifeLogger";
	private static final int DIALOG_ID_DATE = 1,
							DIALOG_ID_TIME = 2;
	private Button btn_date,
				btn_time;
	private TextView text_recent;
	
	private int _year, _month, _day,
				_hour, _minute;
	private DatabaseAdapter _dbHelper;
	private LinearLayout _logButtons;
	private HashMap<Integer, String> _idNameMapping;
	
	private static LifeLoggerActivity instance;

	/********************************** Life cycle Functions *******************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_events);
		
		_dbHelper = new DatabaseAdapter(this).open();
		text_recent = (TextView)findViewById(R.id.text_recent);
		
		// Setup the logging buttons
		_logButtons = (LinearLayout)findViewById(R.id.container_logButtons);
		loadData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setupDateTime();
	}
	
	/*************************************** Public Functions ******************************************/
	public void logEvent(int id, String eventText) {
		String date = Utils.getDateString(_year, _month, _day);
		String time = Utils.getTimeString(_hour, _minute);
		
		String text = date + " " + time + ": " + eventText + "\n" + text_recent.getText();
		text_recent.setText(text);
		
		
		Calendar cal = Calendar.getInstance();
		cal.set(_year, _month, _day, _hour, _minute, 0);
		long timeStamp = cal.getTimeInMillis()/1000;
		_dbHelper.insertEvent(id, (int)timeStamp);
	}
	
	public void showRecent(int id, String eventText) {
		Calendar cal = Calendar.getInstance();
		StringBuilder eventString = new StringBuilder();
		
		Cursor cursor = _dbHelper.fetchRecentEvents(id);
		// TODO move this into a function so no duplication
		if (cursor.moveToFirst()) {
			do {
				long timeStamp = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TIME));
				int eventType = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TYPE));
				
				cal.setTimeInMillis(timeStamp*1000);
				String typeName = _idNameMapping.get(eventType);
				eventString.append(Utils.getDateString(cal)+ " " + Utils.getTimeString(cal) + ": " + typeName + "\n");
			} while (cursor.moveToNext());
		}
		cursor.close();
		this.text_recent.setText(eventString);
	}
	
	public static void refresh() {
		LifeLoggerActivity.instance.loadData();
	}
	
	/*************************************** Helper Functions ******************************************/
	private void loadData() {
		if (LifeLoggerActivity.instance == null) LifeLoggerActivity.instance = this;
		_logButtons.removeAllViews();
		_idNameMapping = new HashMap<Integer, String>();

		Calendar cal = Calendar.getInstance();
		
		// Load the buttons
		Cursor cursor = _dbHelper.fetchEventTypes();
		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				String name = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TYPE_NAME));
				int active = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ACTIVE));
				_idNameMapping.put(new Integer(id), name);
				
				if (active == 1) {
					_logButtons.addView(new EventButtonView(this, id, name));
				}
			} while(cursor.moveToNext());
		}
		cursor.close();
		
		// Load recent logs
		cursor = _dbHelper.fetchRecentEvents();
		StringBuilder eventString = new StringBuilder();
		
		if (cursor.moveToFirst()) {
			do {
				long timeStamp = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TIME));
				int eventType = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_EVENT_TYPE));
								
				cal.setTimeInMillis(timeStamp*1000);
				String typeName = _idNameMapping.get(eventType);
				eventString.append(Utils.getDateString(cal)+ " " + Utils.getTimeString(cal) + ": " + typeName + "\n");
			} while (cursor.moveToNext());
		}
		cursor.close();
		this.text_recent.setText(eventString.toString());
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
			public void onClick(View v) {
				showDialog(DIALOG_ID_DATE);
			}
		});
		
		btn_time.setOnClickListener(new View.OnClickListener() {
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