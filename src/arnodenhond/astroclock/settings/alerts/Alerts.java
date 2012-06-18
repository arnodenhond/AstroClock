package arnodenhond.astroclock.settings.alerts;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclocklite.R;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Alerts extends ExpandableListActivity {

	GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/Alerts");

		setContentView(R.layout.alerts);
		AstroClock.setupAd(this);

		AlertsAdapter adapter = new AlertsAdapter(this, null, 0, null, null, null, 0, null, null);
		setListAdapter(adapter);
		getExpandableListView().setGroupIndicator(null);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}
