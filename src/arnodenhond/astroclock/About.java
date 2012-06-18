package arnodenhond.astroclock;

import java.util.Calendar;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import arnodenhond.astroclock.calcuator.SunTimes;
import arnodenhond.astroclock.calcuator.SunTimesException;
import arnodenhond.astroclock.calcuator.Time;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyFeaturedAppNotifier;
import com.tapjoy.TapjoyFeaturedAppObject;

@SuppressWarnings("deprecation")
public class About extends TabActivity implements TapjoyFeaturedAppNotifier {

	TabHost mTabHost;
	GoogleAnalyticsTracker tracker;
	AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/About");

		TapjoyConnect.requestTapjoyConnect(this, "c8097a42-10d5-4032-a4dd-b2de7d2f8bae", "5XPWT9KbXabrv7pwOo4k");
		TapjoyConnect.getTapjoyConnectInstance().getFeaturedApp(this);

		setContentView(R.layout.about);
		adView = AstroClock.setupAd(this);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("AboutApp").setContent(R.id.aboutapp).setIndicator(getString(R.string.aboutapptitle)));
		mTabHost.addTab(mTabHost.newTabSpec("AboutDev").setContent(R.id.aboutdev).setIndicator(getString(R.string.aboutdevtitle)));
		calcDayNightLength();
		final PrefsReader pr = new PrefsReader(this);
		EditText keywords = (EditText) findViewById(R.id.keywords);
		keywords.setText(pr.getKeywords());
	}

	@Override
	protected void onPause() {
		EditText keywords = (EditText) findViewById(R.id.keywords);
		PrefsReader pr = new PrefsReader(this);
		pr.setKeywords(keywords.getText().toString());
		super.onPause();
	}

	private void calcDayNightLength() {

		PrefsReader pr = new PrefsReader(this);
		double latitude = pr.getLatitude();
		double longitude = pr.getLongitude();

		Calendar c = Calendar.getInstance();
		double up = 0;
		double down = 0;
		try {
			up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}
		Time uptime = new Time(down - up);
		Time downtime = new Time((24 - down) + up);

		ProgressBar pb = (ProgressBar) findViewById(R.id.daynightlength);
		int max = ((uptime.getHour() * 60) + uptime.getMinute()) + ((downtime.getHour() * 60) + downtime.getMinute());
		int progress = ((uptime.getHour() * 60) + uptime.getMinute());
		pb.setMax(max);
		pb.setProgress(progress);

	}

	public void tapjoyfeaturedapp(View v) {
		tracker.trackPageView("/Tapjoy");
		if (mFeaturedAppAvailable)
			TapjoyConnect.getTapjoyConnectInstance().showFeaturedAppFullScreenAd();
		else
			TapjoyConnect.getTapjoyConnectInstance().showOffers();
	}

	public void sendfeedback(View v) {
		tracker.trackPageView("/Feedback");
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "arnodenhond+astroclock@gmail.com" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This app would be even better if....");
		startActivity(emailIntent);
	}

	public void postcomment(View v) {
		tracker.trackPageView("/Comment");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.setData(Uri.parse("market://details?id=arnodenhond.astroclocklite"));
		startActivity(intent);
	}

	public void shareurl(View v) {
		tracker.trackPageView("/Share");
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " http://play.google.com/store/apps/details?id=arnodenhond.astroclocklite");
		startActivity(intent);
	}

//	public void getalarm(View v) {
//		tracker.trackPageView("/SunriseAlarm");
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//		intent.setData(Uri.parse("market://details?id=arnodenhond.sunrisealarm"));
//		startActivity(intent);
//	}

	boolean mFeaturedAppAvailable = false;

	@Override
	public void getFeaturedAppResponse(TapjoyFeaturedAppObject featuredAppObject) {
		mFeaturedAppAvailable = true;
		// findViewById(R.id.tapjoylayout).setVisibility(View.VISIBLE);
	}

	@Override
	public void getFeaturedAppResponseFailed(String error) {
		// ((TextView)findViewById(R.id.donateheader)).setText(error);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
		adView.destroy();
	}
}
