package org.belligero.nautilus.life.logger;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

@SuppressWarnings( "deprecation" )
public class LoggerTabs extends TabActivity {
	public static final int ACTIVITY_ID_EDIT_EVENT_TYPES = 1;

	protected TabHost tabHost;

	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		Resources res = getResources();
		tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		// Create LifeLogger activity and put it in
		intent = new Intent().setClass( this, LifeLoggerActivity.class );
		spec = tabHost
				.newTabSpec( "logger" )
				.setIndicator(
						res.getString( R.string.tab_log ),
						res.getDrawable( R.drawable.tab_log )
					)
				.setContent( intent );
		tabHost.addTab( spec );

		// Add the Export Data activity
		intent = new Intent().setClass( this, ExportDataActivity.class );
		spec = tabHost
				.newTabSpec( "export" )
				.setIndicator(
						res.getString( R.string.tab_export ),
						res.getDrawable( R.drawable.tab_export )
					)
				.setContent( intent );
		tabHost.addTab( spec );

		// Statistics
		intent = new Intent().setClass( this, StatisticsActivity.class );
		spec = tabHost
				.newTabSpec( "stats" )
				.setIndicator(
						res.getString( R.string.tab_stats ),
						res.getDrawable( R.drawable.tab_stats )
					)
				.setContent( intent );
		tabHost.addTab( spec );
	}

	/*************************************** Menu ******************************************************/
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.menu_main, menu );
		return true;
	}

	/*************************************** Handlers **************************************************/
	// Handle menu items
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Intent intent;

		switch ( item.getItemId() ) {
		case R.id.menu_editEvents:
			intent = new Intent( this, EditEventTypesActivity.class );
			startActivityForResult( intent, ACTIVITY_ID_EDIT_EVENT_TYPES );
			return true;
		default:
			return super.onOptionsItemSelected( item );
		}
	}

	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		if ( requestCode == ACTIVITY_ID_EDIT_EVENT_TYPES ) {			
			if ( resultCode == RESULT_OK ) {
				Toast.makeText(
						this,
						R.string.events_updated,
						Toast.LENGTH_SHORT
					).show();
			}
			
			LifeLoggerActivity.refresh();
		}
	}
}
