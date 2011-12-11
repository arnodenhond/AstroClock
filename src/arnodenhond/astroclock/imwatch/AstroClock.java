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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AstroClock extends Activity implements OnClickListener {
	
	//TODO uncomment imwatch shortcut
	
	ImageView iv;
	BitmapMaker bmmaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity);
		iv = (ImageView) findViewById(R.id.clock);

		super.onCreate(savedInstanceState);
	}

	PendingIntent pi;

	@Override
	protected void onResume() {
		if (!isImWatch()) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=arnodenhond.astroclocklite")));
			finish();
			return;
		}

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int height = display.getHeight();

		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = prefs.getFloat("latitude", 35.01f);
		double longitude = prefs.getFloat("longitude", -120.01f);
		bmmaker = new BitmapMaker(this, height, latitude, longitude);
		iv.setImageBitmap(bmmaker.makeBitmap());
		
		iv.setOnClickListener(this);

		registerReceiver(alarmReceiver, new IntentFilter("astroclockupdate"));

		pi = PendingIntent.getBroadcast(this, 0, new Intent("astroclockupdate"), 0);

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
		super.onResume();
	}

	private boolean isImWatch() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return (display.getHeight() < 241 && display.getWidth() < 241);
		//return true;
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

	@Override
	public void onClick(View arg0) {
		startActivity(new Intent(AstroClock.this,Menu.class));
	}

}
