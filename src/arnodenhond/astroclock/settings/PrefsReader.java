package arnodenhond.astroclock.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefsReader {

	private double latitude;
	private double longitude;
	private int theme;
	private final Context context;

	public PrefsReader(Context context) {
		this.context = context;
		refresh();
	}

	public void refresh() {
		SharedPreferences latlonpref = context.getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		SharedPreferences themepref = context.getSharedPreferences("theme", Activity.MODE_PRIVATE);
		this.latitude = Float.parseFloat(latlonpref.getString("latitude", "-35"));
		this.longitude = Float.parseFloat(latlonpref.getString("longitude", "-120"));
		this.theme = themepref.getInt("theme", 0);
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

	public static void getBackground() {

	}

	public static void getNotifications() {
	}

}
