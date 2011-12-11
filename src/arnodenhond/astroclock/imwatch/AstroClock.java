package arnodenhond.astroclock.imwatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AstroClock extends Activity {
	
	//TODO uncomment imwatch shortcut
	
	ImageView iv;
	BitmapMaker bmmaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// SharedPreferences.Editor edit = prefs.edit();
		// edit.putFloat("latitude", (float) latitude);
		// edit.putFloat("longitude", (float) longitude);
		// edit.commit();
	}

	PendingIntent pi;

	@Override
	protected void onResume() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity);

		iv = (ImageView) findViewById(R.id.clock);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if (!isImWatch()) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=arnodenhond.astroclocklite")));
			finish();
			return;
		}
		int height = display.getHeight();

		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = prefs.getFloat("latitude", 35.01f);
		double longitude = prefs.getFloat("longitude", -120.01f);
		bmmaker = new BitmapMaker(this, height, latitude, longitude);
		iv.setImageBitmap(bmmaker.makeBitmap());
		
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		registerReceiver(alarmReceiver, new IntentFilter("astroclockupdate"));
		Intent intent = new Intent("astroclockupdate");
		pi = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
		super.onResume();
	}

	private boolean isImWatch() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		//return (display.getHeight() < 241 && display.getWidth() < 241)
		return true;
	}

	BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			iv.setImageBitmap(bmmaker.makeBitmap());
		}
	};

	@Override
	protected void onPause() {
		unregisterReceiver(alarmReceiver);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		super.onPause();
	}

}
