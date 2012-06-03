package arnodenhond.astroclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import arnodenhond.astroclock.alerts.Alarms;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclocklite.R;

public class AstroClock extends Activity {

	PendingIntent pi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		final ImageView iv = (ImageView) findViewById(R.id.clock);
		iv.post(new Runnable() {
			@Override
			public void run() {
				PrefsReader pr = new PrefsReader(AstroClock.this);
				final double latitude = pr.getLatitude();
				final double longitude = pr.getLongitude();
				final int theme = pr.getTheme();
				iv.setImageBitmap(new BitmapMaker(AstroClock.this, iv.getHeight(), latitude, longitude, theme).makeBitmap());
			}
		});

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
		// startActivity(new Intent(AstroClock.this, Menu.class));
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.astroclock, menu);
		menu.findItem(R.id.Theme).setIntent(new Intent(AstroClock.this, Theme.class));
		menu.findItem(R.id.Alerts).setIntent(new Intent(AstroClock.this, Alarms.class));
		menu.findItem(R.id.Location).setIntent(new Intent(AstroClock.this, Map.class));
		menu.findItem(R.id.About).setIntent(new Intent(AstroClock.this, Help.class));
		return super.onCreateOptionsMenu(menu);
	}

}
