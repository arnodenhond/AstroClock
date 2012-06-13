package arnodenhond.astroclock.settings.alerts;

import java.util.Arrays;
import java.util.HashSet;

import android.app.ExpandableListActivity;
import android.location.Location;
import android.os.Bundle;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Alerts extends ExpandableListActivity implements AdListener {

	GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/Alerts");

		setContentView(R.layout.alerts);
		setupAd();

		AlertsAdapter adapter = new AlertsAdapter(this, null, 0, null, null, null, 0, null, null);
		setListAdapter(adapter);
		getExpandableListView().setGroupIndicator(null);

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
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		tracker.trackEvent("noad", "noad", "noad", 2);
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
	}

	@Override
	public void onPresentScreen(Ad arg0) {
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		tracker.trackEvent("ad", "ad", "ad", 2);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}
