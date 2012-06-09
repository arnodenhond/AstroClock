package arnodenhond.astroclock.settings;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.Help;
import arnodenhond.astroclock.alerts.Alarms;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclock.widget.WidgetProvider;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class Menu extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.settingsoptions));
		setListAdapter(adapter);
		setupAd();

		// hack for widget
		if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) != AppWidgetManager.INVALID_APPWIDGET_ID) {
			Intent resultValue = new Intent();
			final int id = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
			setResult(RESULT_OK, resultValue);
		}
	}

	private void setupAd() {
		PrefsReader prefs = new PrefsReader(this);
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adrequest = new AdRequest();
		adrequest.addKeyword(prefs.getKeywords());
		Location location = new Location("AstroClock");
		location.setLatitude(prefs.getLatitude());
		location.setLongitude(prefs.getLongitude());
		adrequest.setLocation(location);
		// adrequest.addTestDevice("10007c61aeb3");
		adView.loadAd(adrequest);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			startActivity(new Intent(Menu.this, Theme.class));
			break;
		case 1:
			startActivity(new Intent(Menu.this, Alarms.class));
			break;
		case 2:
			startActivity(new Intent(Menu.this, Map.class));
			break;
		case 3:
			startActivity(new Intent(Menu.this, Help.class));
			break;
		}
	}

	@Override
	public void onBackPressed() {
		updateWidget();

		super.onBackPressed();
	}

	private void updateWidget() {
			AppWidgetManager awm = AppWidgetManager.getInstance(this);

			RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
			PrefsReader settings = new PrefsReader(this);
			int height = getResources().getDisplayMetrics().heightPixels;
			int width = getResources().getDisplayMetrics().widthPixels;
			BitmapMaker bmmaker = new BitmapMaker(this, 500, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
			views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
			Intent menuintent = new Intent(this, Menu.class);
			menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(this, 0, menuintent, Intent.FLAG_ACTIVITY_NEW_TASK));
			awm.updateAppWidget(new ComponentName(this, WidgetProvider.class), views);
	}

}
