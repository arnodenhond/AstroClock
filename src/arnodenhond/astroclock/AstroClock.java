package arnodenhond.astroclock;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
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
import arnodenhond.astroclock.alerts.BootReceiver;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.settings.alerts.Alarms;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclocklite.R;

import com.google.android.maps.GeoPoint;

public class AstroClock extends Activity {

	private static final int DIALOG_WELCOME = 1;
	PendingIntent pi;

	private boolean supportsAPILevel11() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (supportsAPILevel11()) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
			requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
			//getWindow().getDecorView().setSystemUiVisibility(1);
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		PrefsReader pr = new PrefsReader(this);
		if (pr.isFirstrun()) {
			showDialog(DIALOG_WELCOME);
			getLocation();
			setAlarms();
			pr.setFirstrun(false);
		}
		setContentView(R.layout.activity);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WELCOME: {
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

	@Override
	protected void onResume() {
		super.onResume();

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
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.menu, menu);
		menu.findItem(R.id.Theme).setIntent(new Intent(AstroClock.this, Theme.class));
		menu.findItem(R.id.Alerts).setIntent(new Intent(AstroClock.this, Alarms.class));
		menu.findItem(R.id.Location).setIntent(new Intent(AstroClock.this, Map.class));
		menu.findItem(R.id.About).setIntent(new Intent(AstroClock.this, Help.class));
		return super.onCreateOptionsMenu(menu);
	}

}
