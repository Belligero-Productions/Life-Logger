package org.belligero.nautilus.life.logger.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.belligero.nautilus.LifeLogger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

public class Utils {
	public static final String getTimeString( long timestamp ) {
		return DateUtils.formatDateTime(
				LifeLogger.getContext(),
				timestamp * 1000,
				DateUtils.FORMAT_SHOW_TIME
			);
	}
	
	public static final String getTimeString(Calendar cal) {
		return getTimeString( cal.getTimeInMillis() / 1000 ); // Dividing by 1000 so getTimeString(long) doesn't have to care
	}
	
	public static final String getTimeString(int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR_OF_DAY, hour );
		cal.set( Calendar.MINUTE, minute );
		
		return getTimeString( cal );
	}
	
	public static final String getDateString( long timestamp ) {
		
		return DateUtils.formatDateTime(
				LifeLogger.getContext(),
				timestamp * 1000,
				DateUtils.FORMAT_ABBREV_ALL
			);
	}
	
	public static final String getDateString(Calendar cal) {
		return getDateString( cal.getTimeInMillis() / 1000 ); // Dividing by 1000 so getDateString(long) doesn't have to care
	}
	
	public static final String getDateString(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set( year, month, day );
		
		return getDateString( cal );
	}
	
	/**
	 * Create a confirmation dialog which will use a ConfirmResultListener to return the results.
	 * 
	 * @param context The activity context to create a dialog with
	 * @param titleResId String resource ID of the title for the dialog
	 * @param messageResId String resource ID of the message to show in the dialog
	 * @param confirmResultListener A ConfirmResultListener to call the "confirm" or "deny" methods of
	 */
	public static final void confirm( Context context, int titleResId, int messageResId, final ConfirmResultListener confirmResultListener ) {
		confirm(
				context,
				context.getString( titleResId ),
				context.getString( messageResId ),
				confirmResultListener
			);
	}
	
	/**
	 * Create a confirmation dialog which will use a ConfirmResultListener to return the results.
	 * 
	 * @param context The activity context to create a dialog with
	 * @param title The title for the dialog
	 * @param message The message to display in the dialog
	 * @param confirmResultListener A ConfirmResultListener to call the "confirm" or "deny" methods of
	 */
	public static final void confirm( Context context, String title, String message, final ConfirmResultListener confirmResultListener ) {
		
		AlertDialog dialog = new AlertDialog.Builder( context ).create();
		dialog.setTitle( title );
		dialog.setMessage( message );
		dialog.setCancelable( false );
		
		dialog.setButton(
				DialogInterface.BUTTON_POSITIVE,
				context.getText( android.R.string.yes ),
				new DialogInterface.OnClickListener() {	
					public void onClick( DialogInterface dialog, int which ) {
						confirmResultListener.confirm();
					}
				}
			);
		
		dialog.setButton(
				DialogInterface.BUTTON_NEGATIVE,
				context.getText( android.R.string.no ),
				new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int which ) {
						confirmResultListener.deny();
					}
				}
			);
		
		dialog.setIcon( android.R.drawable.ic_dialog_alert );
		dialog.show();
	}
}
