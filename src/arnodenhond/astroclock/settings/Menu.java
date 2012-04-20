package arnodenhond.astroclock.settings;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import arnodenhond.astroclock.Help;
import arnodenhond.astroclock.Stats;
import arnodenhond.astroclock.alerts.Alarms;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclocklite.R;

public class Menu extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		// hack for widget
		if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) != AppWidgetManager.INVALID_APPWIDGET_ID) {
			Intent resultValue = new Intent();
			final int id = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
			setResult(RESULT_OK, resultValue);
		}
	}
	
	

	public void startTheme(View v) {
		startActivity(new Intent(Menu.this, Theme.class));
	}

	public void startMap(View v) {
		startActivity(new Intent(Menu.this, Map.class));
	}

	public void startStats(View v) {
		startActivity(new Intent(Menu.this, Stats.class));
	}

	public void startHelp(View v) {
		startActivity(new Intent(Menu.this, Help.class));
	}

	public void startAlarms(View v) {
		startActivity(new Intent(Menu.this, Alarms.class));
	}

}
