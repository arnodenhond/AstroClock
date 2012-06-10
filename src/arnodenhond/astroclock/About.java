package arnodenhond.astroclock;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import android.app.TabActivity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import arnodenhond.astroclock.calcuator.SunTimes;
import arnodenhond.astroclock.calcuator.SunTimesException;
import arnodenhond.astroclock.calcuator.Time;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

@SuppressWarnings("deprecation")
public class About extends TabActivity {

	TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setupAd();
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("AboutApp").setContent(R.id.aboutapp).setIndicator(getString(R.string.aboutapptitle)));
		mTabHost.addTab(mTabHost.newTabSpec("AboutDev").setContent(R.id.aboutdev).setIndicator(getString(R.string.aboutdevtitle)));
		calcDayNightLength();
		final PrefsReader pr = new PrefsReader(this);
		EditText keywords = (EditText) findViewById(R.id.keywords);
		keywords.setText(pr.getKeywords());
		keywords.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				pr.setKeywords(s.toString());
			}
		});
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
		adView.loadAd(adrequest);
	}

	public void circleplus(View v) {
		final Intent plusintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/b/114430171409774482800/"));
		plusintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(plusintent);
	}

	public void sendfeedback(View v) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "arnodenhond+astroclock@gmail.com" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This app would be even better if....");
		startActivity(emailIntent);
	}

	public void postcomment(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.setData(Uri.parse("market://details?id=arnodenhond.astroclocklite"));
		startActivity(intent);
	}

	public void shareurl(View v) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name)+" http://play.google.com/store/apps/details?id=arnodenhond.astroclocklite");
		startActivity(intent);
	}

	public void getalarm(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.setData(Uri.parse("market://details?id=arnodenhond.astroclock"));
		startActivity(intent);
	}

}
