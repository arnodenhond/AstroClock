package arnodenhond.astroclock.imwatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

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

//		if (!isImWatch()) {
//			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=arnodenhond.astroclocklite")));
//			finish();
//			return;
//		}
		
		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = Float.parseFloat(prefs.getString("latitude", "-35"));
		double longitude = Float.parseFloat(prefs.getString("longitude", "-120"));
		prefs = getSharedPreferences("theme", Activity.MODE_PRIVATE);
		bmmaker = new BitmapMaker(this, 240, latitude, longitude, prefs.getInt("theme", 0));
		iv.setImageBitmap(bmmaker.makeBitmap());

		iv.setOnClickListener(this);
		Intent pintent = new Intent(this, AstroClock.class);
		pintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pi = PendingIntent.getActivity(this, 0, pintent, 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
	}

	private boolean isImWatch() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return (display.getHeight() < 241 && display.getWidth() < 241);
		//return true;
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
