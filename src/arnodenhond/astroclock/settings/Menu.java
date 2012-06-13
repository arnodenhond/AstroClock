package arnodenhond.astroclock.settings;

import java.util.Arrays;
import java.util.HashSet;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import arnodenhond.astroclock.About;
import arnodenhond.astroclock.settings.alerts.Alerts;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclocklite.R;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Menu extends ListActivity implements AdListener {

	GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/Menu");

		setContentView(R.layout.settings);
		final String[] options = getResources().getStringArray(R.array.settingsoptions);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.menurow, options) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				String option = options[position];
				int icon;
				switch (position) {
				case 0:
					icon = android.R.drawable.ic_menu_view;
					break;
				case 1:
					icon = android.R.drawable.ic_menu_recent_history;
					break;
				case 2:
					icon = android.R.drawable.ic_menu_mapmode;
					break;
				case 3:
					icon = android.R.drawable.ic_menu_help;
					break;
				default:
					icon = android.R.drawable.ic_menu_revert;
					break;
				}
				View view = li.inflate(R.layout.menurow, null);
				((TextView) view.findViewById(R.id.option)).setText(option);
				((ImageView) view.findViewById(R.id.icon)).setImageResource(icon);
				return view;
			}
		};

		setListAdapter(adapter);
		setupAd();

		// hack for widget
		if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) != AppWidgetManager.INVALID_APPWIDGET_ID) {
			tracker.trackPageView("/WidgetInstall");
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
		adrequest.setKeywords(new HashSet<String>(Arrays.asList(prefs.getKeywords().split(","))));
		adView.setAdListener(this);
		adView.loadAd(adrequest);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			startActivity(new Intent(Menu.this, Theme.class));
			break;
		case 1:
			startActivity(new Intent(Menu.this, Alerts.class));
			break;
		case 2:
			startActivity(new Intent(Menu.this, Map.class));
			break;
		case 3:
			startActivity(new Intent(Menu.this, About.class));
			break;
		case 4:
			finish();
			break;
		}
	}

	@Override
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		tracker.trackEvent("noad", "noad", "noad", 0);
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
	}

	@Override
	public void onPresentScreen(Ad arg0) {
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		tracker.trackEvent("ad", "ad", "ad", 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}
