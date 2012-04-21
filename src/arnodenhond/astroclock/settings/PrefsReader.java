package arnodenhond.astroclock.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefsReader {

	private double latitude;
	private double longitude;
	private int theme;
	private final Context context;

	private boolean audible;
	private boolean vibrate;

	public PrefsReader(Context context) {
		this.context = context;
		refresh();
	}

	public void refresh() {
		SharedPreferences latlonpref = context.getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		SharedPreferences themepref = context.getSharedPreferences("theme", Activity.MODE_PRIVATE);
		SharedPreferences alarmspref = context.getSharedPreferences("alarms", Activity.MODE_PRIVATE);

		this.latitude = Float.parseFloat(latlonpref.getString("latitude", "-35"));
		this.longitude = Float.parseFloat(latlonpref.getString("longitude", "-120"));
		this.theme = themepref.getInt("theme", 2);
		this.audible = alarmspref.getBoolean("audible", true);
		this.vibrate = alarmspref.getBoolean("vibrate", true);
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getTheme() {
		return theme;
	}

	public void getBackground() {

	}

	public void getNotifications() {
	}

	public boolean getNotificationAudible() {
		return audible;

	}
	
	public boolean getNotificationVibrate() {
		return vibrate;
	}


}
