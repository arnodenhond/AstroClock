package arnodenhond.astroclock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import arnodenhond.astroclock.alerts.BootReceiver;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.settings.alerts.Alerts;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;

public class AstroClock extends Activity {

	private static final int DIALOG_WELCOME = 1;
	PendingIntent pi;
	GoogleAnalyticsTracker tracker;

	private boolean supportsAPILevel11() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (supportsAPILevel11()) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
			requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
			// getWindow().getDecorView().setSystemUiVisibility(1);
			tracker.setCustomVar(2, "11plus", "true", 2);
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			tracker.setCustomVar(2, "11plus", "false", 2);
		}
		tracker.trackPageView("/AstroClockActivity");

		PrefsReader pr = new PrefsReader(this);
		if (pr.isFirstrun()) {
			showDialog(DIALOG_WELCOME);
			getLocation();
			setAlarms();
			pr.setFirstrun(false);
		}
		setContentView(R.layout.activity);
		if (supportsAPILevel11()) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) findViewById(R.id.adView).getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}
		setupAd();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WELCOME: {
			tracker.trackPageView("/AstroClockActivityFirstRun");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.welcometitle);
			builder.setMessage(R.string.welcomebody);
			builder.setNeutralButton(R.string.welcomebutton, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(DIALOG_WELCOME);
				}
			});
			return builder.create();
		}
		}
		return super.onCreateDialog(id);
	}

	private void setAlarms() {
		SharedPreferences prefs = getSharedPreferences(PrefsReader.PREF_ALERTS, MODE_PRIVATE);
		prefs.edit().putBoolean(PrefsReader.KEY_ALERT_VIBRATE, true).putBoolean(PrefsReader.KEY_ALERT_NORTHERNSOLSTICE, true).putBoolean(PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE, true).putBoolean(PrefsReader.KEY_ALERT_NORTHWARDEQUINOX, true).putBoolean(PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX, true).putBoolean(PrefsReader.KEY_ALERT_FULLMOON, true).commit();
		sendBroadcast(new Intent(this, BootReceiver.class));
	}

	private void getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(new Criteria(), true);
		if (!providers.isEmpty()) {
			Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
			if (location != null) {
				int lat = (int) (location.getLatitude() * 1E6);
				int lon = (int) (location.getLongitude() * 1E6);
				GeoPoint gp = new GeoPoint(lat, lon);
				new PrefsReader(this).setGeoPoint(gp);
			}
		}
	}

	public static Location getLatestOrSaved(Context ctx) {
		Location storedlocation = new Location("Manual");
		PrefsReader prefs = new PrefsReader(ctx);
		storedlocation.setLatitude(prefs.getLatitude());
		storedlocation.setLongitude(prefs.getLongitude());

		LocationManager locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(new Criteria(), true);
		if (!providers.isEmpty()) {
			Location lastlocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
			if (lastlocation != null) {
				return lastlocation;
			} else {
				return storedlocation;
			}
		} else {
			return storedlocation;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		tracker.trackPageView("/AstroClockActivityReload");
		findViewById(R.id.adView).setVisibility(View.GONE);

		final ImageView iv = (ImageView) findViewById(R.id.clock);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.loading);
		iv.postDelayed(new Runnable() {
			@Override
			public void run() {
				PrefsReader pr = new PrefsReader(AstroClock.this);
				final double latitude = pr.getLatitude();
				final double longitude = pr.getLongitude();
				final int theme = pr.getTheme();
				pb.setVisibility(View.VISIBLE);
				iv.setImageBitmap(new BitmapMaker(AstroClock.this, iv.getHeight(), latitude, longitude, theme).makeBitmap());
				pb.setVisibility(View.GONE);
			}
		}, 100);

		Intent pintent = new Intent(this, AstroClock.class);
		pintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pi = PendingIntent.getActivity(this, 0, pintent, 0);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);

	}

	@Override
	protected void onPause() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		super.onPause();
	}

	public void clicked(View arg0) {
		View adView = this.findViewById(R.id.adView);
		if (adView.getVisibility() == View.GONE)
			adView.setVisibility(View.VISIBLE);
		else
			adView.setVisibility(View.GONE);
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.menu, menu);
		menu.findItem(R.id.Theme).setIntent(new Intent(AstroClock.this, Theme.class));
		menu.findItem(R.id.Alerts).setIntent(new Intent(AstroClock.this, Alerts.class));
		menu.findItem(R.id.Location).setIntent(new Intent(AstroClock.this, Map.class));
		menu.findItem(R.id.About).setIntent(new Intent(AstroClock.this, About.class));
		return super.onCreateOptionsMenu(menu);
	}

	private void setupAd() {
		PrefsReader prefs = new PrefsReader(this);
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adrequest = new AdRequest();
		adrequest.addKeyword(prefs.getKeywords());
		Location location = new Location("Manual");
		location.setLatitude(prefs.getLatitude());
		location.setLongitude(prefs.getLongitude());
		adrequest.setLocation(AstroClock.getLatestOrSaved(this));
		adrequest.setKeywords(new HashSet<String>(Arrays.asList(prefs.getKeywords().split(","))));
		// adView.setAdListener(this);
		adView.loadAd(adrequest);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}
