package arnodenhond.astroclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

public class AstroClock extends Activity implements OnClickListener {

	// TODO uncomment imwatch shortcut

	ImageView iv;
	BitmapMaker bmmaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity);
		iv = (ImageView) findViewById(R.id.clock);
		// getSharedPreferences("theme", MODE_PRIVATE).edit().clear().commit();

		super.onCreate(savedInstanceState);
	}

	PendingIntent pi;

	@Override
	protected void onResume() {
		super.onResume();

		// startActivity(new Intent(Intent.ACTION_VIEW,
		// Uri.parse("market://details?id=arnodenhond.astroclocklite")));

		
		PrefsReader pr = new PrefsReader(this);
		
		final double latitude = pr.getLatitude();
		final double longitude = pr.getLongitude();
		final int theme = pr.getTheme();

		iv.post(new Runnable() {
			@Override
			public void run() {
				bmmaker = new BitmapMaker(AstroClock.this, iv.getHeight(), latitude, longitude, theme);
				iv.setImageBitmap(bmmaker.makeBitmap());
			}
		});

		iv.setOnClickListener(this);
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

	@Override
	public void onClick(View arg0) {
		startActivity(new Intent(AstroClock.this, Menu.class));
	}

}
