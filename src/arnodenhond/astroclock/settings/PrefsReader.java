package arnodenhond.astroclock.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.maps.GeoPoint;

public class PrefsReader {

	private final Context context;

	private static final String PREF_LATLON = "latlon";

	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private double latitude;
	private double longitude;

	private static final String PREF_THEME = "theme";

	public static final String KEY_THEME = "theme";
	public static final String KEY_USEBACKGROUND = "usebackground";
	public static final String KEY_BACKGROUNDIMAGE = "backgroundimage";
	private int theme;
	private boolean usebackground;
	private String backgroundimage;

	private static final String PREF_KEYWORDS = "keywords";

	private static final String KEY_KEYWORDS = "keywords";

	private String keywords;

	public static final String PREF_ALERTS = "alarms";

	public static final String KEY_ALERT_AUDIBLE = "audible";
	public static final String KEY_ALERT_VIBRATE = "vibrate";

	public static final String KEY_ALERT_MIDDAY = "ASTROCLOCK_MIDDAY";
	public static final String KEY_ALERT_MIDNIGHT = "ASTROCLOCK_MIDNIGHT";
	public static final String KEY_ALERT_SUNRISE = "ASTROCLOCK_SUNRISE";
	public static final String KEY_ALERT_SUNSET = "ASTROCLOCK_SUNSET";

	public static final String KEY_ALERT_FULLMOON = "ASTROCLOCK_FULLMOON";
	public static final String KEY_ALERT_NEWMOON = "ASTROCLOCK_NEWMOON";
	public static final String KEY_ALERT_FIRSTQUARTER = "ASTROCLOCK_FIRSTQUARTER";
	public static final String KEY_ALERT_LASTQUARTER = "ASTROCLOCK_LASTQUARTER";

	public static final String KEY_ALERT_NORTHERNSOLSTICE = "ASTROCLOCK_NORTHERNSOLSTICE";
	public static final String KEY_ALERT_SOUTHERNSOLSTICE = "ASTROCLOCK_SOUTHERNSOLSTICE";
	public static final String KEY_ALERT_NORTHWARDEQUINOX = "ASTROCLOCK_NORTHWARDEQUINOX";
	public static final String KEY_ALERT_SOUTHWARDEQUINOX = "ASTROCLOCK_SOUTHWARDEQUINOX";

	private boolean audible;
	private boolean vibrate;

	private boolean alert_midday;
	private boolean alert_midnight;
	private boolean alert_sunrise;
	private boolean alert_sunset;
	private boolean alert_fullmoon;
	private boolean alert_newmoon;
	private boolean alert_firstquarter;
	private boolean alert_lastquarter;
	private boolean alert_northernsolstice;
	private boolean alert_southernsolstice;
	private boolean alert_northwardequinox;
	private boolean alert_southwardequinox;

	private static final String PREF_FIRSTRUN = "firstrun";

	private static final String KEY_FIRSTRUN = "firstrun";

	private boolean firstrun;

	public PrefsReader(Context context) {
		this.context = context;
		refresh();
	}

	public void refresh() {
		SharedPreferences latlonpref = context.getSharedPreferences(PREF_LATLON, Activity.MODE_PRIVATE);
		SharedPreferences themepref = context.getSharedPreferences(PREF_THEME, Activity.MODE_PRIVATE);
		SharedPreferences alarmspref = context.getSharedPreferences(PREF_ALERTS, Activity.MODE_PRIVATE);
		SharedPreferences keywordspref = context.getSharedPreferences(PREF_KEYWORDS, Activity.MODE_PRIVATE);
		SharedPreferences firstrunpref = context.getSharedPreferences(PREF_FIRSTRUN, Activity.MODE_PRIVATE);

		this.latitude = latlonpref.getFloat(KEY_LATITUDE, 35f);
		this.longitude = latlonpref.getFloat(KEY_LONGITUDE, -120f);
		this.theme = themepref.getInt(KEY_THEME, 2);
		this.usebackground = themepref.getBoolean(KEY_USEBACKGROUND, false);
		this.backgroundimage = themepref.getString(KEY_BACKGROUNDIMAGE, "");
		
		this.audible = alarmspref.getBoolean(KEY_ALERT_AUDIBLE, false);
		this.vibrate = alarmspref.getBoolean(KEY_ALERT_VIBRATE, false);

		this.keywords = keywordspref.getString(KEY_KEYWORDS, "");

		this.alert_midday = alarmspref.getBoolean(KEY_ALERT_MIDDAY, false);
		this.alert_midnight = alarmspref.getBoolean(KEY_ALERT_MIDNIGHT, false);
		this.alert_sunrise = alarmspref.getBoolean(KEY_ALERT_SUNRISE, false);
		this.alert_sunset = alarmspref.getBoolean(KEY_ALERT_SUNSET, false);

		this.alert_fullmoon = alarmspref.getBoolean(KEY_ALERT_FULLMOON, false);
		this.alert_newmoon = alarmspref.getBoolean(KEY_ALERT_NEWMOON, false);
		this.alert_firstquarter = alarmspref.getBoolean(KEY_ALERT_FIRSTQUARTER, false);
		this.alert_lastquarter = alarmspref.getBoolean(KEY_ALERT_LASTQUARTER, false);

		this.alert_northernsolstice = alarmspref.getBoolean(KEY_ALERT_NORTHERNSOLSTICE, false);
		this.alert_southernsolstice = alarmspref.getBoolean(KEY_ALERT_SOUTHERNSOLSTICE, false);
		this.alert_northwardequinox = alarmspref.getBoolean(KEY_ALERT_NORTHWARDEQUINOX, false);
		this.alert_southwardequinox = alarmspref.getBoolean(KEY_ALERT_SOUTHWARDEQUINOX, false);

		this.firstrun = firstrunpref.getBoolean(KEY_FIRSTRUN, true);
	}

	public GeoPoint getGeoPoint() {
		int lat = (int) (latitude * 1E6);
		int lon = (int) (longitude * 1E6);
		return new GeoPoint(lat, lon);
	}

	public void setGeoPoint(GeoPoint geopoint) {
		latitude = geopoint.getLatitudeE6() / 1E6;
		longitude = geopoint.getLongitudeE6() / 1E6;
		storeLatLon();
	}

	private void storeLatLon() {
		context.getSharedPreferences(PREF_LATLON, Activity.MODE_PRIVATE).edit().putFloat(KEY_LATITUDE, (float) latitude).putFloat(KEY_LONGITUDE, (float) longitude).commit();
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

	public void setTheme(int theme) {
		this.theme = theme;
		storeTheme();
	}

	private void storeTheme() {
		context.getSharedPreferences(PREF_THEME, Activity.MODE_PRIVATE).edit().putInt(KEY_THEME, theme).commit();
	}

	public boolean isUseBackground() {
		return usebackground;
	}
	
	public void setUseBackground(boolean usebackground) {
		this.usebackground = usebackground;
		context.getSharedPreferences(PREF_THEME, Activity.MODE_PRIVATE).edit().putBoolean(KEY_USEBACKGROUND, usebackground).commit();
	}
	
	public String getBackgroundImage() {
		return backgroundimage;
	}
	
	public void setBackgroundImage(String backgroundimage) {
		this.backgroundimage = backgroundimage;
		context.getSharedPreferences(PREF_THEME, Activity.MODE_PRIVATE).edit().putString(KEY_BACKGROUNDIMAGE, backgroundimage).commit();
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

	public boolean isAlert_midday() {
		return alert_midday;
	}

	public void setAlert_midday(boolean alert_midday) {
		this.alert_midday = alert_midday;
	}

	public boolean isAlert_midnight() {
		return alert_midnight;
	}

	public void setAlert_midnight(boolean alert_midnight) {
		this.alert_midnight = alert_midnight;
	}

	public boolean isAlert_sunrise() {
		return alert_sunrise;
	}

	public void setAlert_sunrise(boolean alert_sunrise) {
		this.alert_sunrise = alert_sunrise;
	}

	public boolean isAlert_sunset() {
		return alert_sunset;
	}

	public void setAlert_sunset(boolean alert_sunset) {
		this.alert_sunset = alert_sunset;
	}

	public boolean isAlert_fullmoon() {
		return alert_fullmoon;
	}

	public void setAlert_fullmoon(boolean alert_fullmoon) {
		this.alert_fullmoon = alert_fullmoon;
	}

	public boolean isAlert_newmoon() {
		return alert_newmoon;
	}

	public void setAlert_newmoon(boolean alert_newmoon) {
		this.alert_newmoon = alert_newmoon;
	}

	public boolean isAlert_firstquarter() {
		return alert_firstquarter;
	}

	public void setAlert_firstquarter(boolean alert_firstquarter) {
		this.alert_firstquarter = alert_firstquarter;
	}

	public boolean isAlert_lastquarter() {
		return alert_lastquarter;
	}

	public void setAlert_lastquarter(boolean alert_lastquarter) {
		this.alert_lastquarter = alert_lastquarter;
	}

	public boolean isAlert_northernsolstice() {
		return alert_northernsolstice;
	}

	public void setAlert_northernsolstice(boolean alert_northernsolstice) {
		this.alert_northernsolstice = alert_northernsolstice;
	}

	public boolean isAlert_southernsolstice() {
		return alert_southernsolstice;
	}

	public void setAlert_southernsolstice(boolean alert_southernsolstice) {
		this.alert_southernsolstice = alert_southernsolstice;
	}

	public boolean isAlert_northwardequinox() {
		return alert_northwardequinox;
	}

	public void setAlert_northwardequinox(boolean alert_northwardequinox) {
		this.alert_northwardequinox = alert_northwardequinox;
	}

	public boolean isAlert_southwardequinox() {
		return alert_southwardequinox;
	}

	public void setAlert_southwardequinox(boolean alert_southwardequinox) {
		this.alert_southwardequinox = alert_southwardequinox;
	}

	public boolean isFirstrun() {
		return firstrun;
	}

	public void setFirstrun(boolean firstrun) {
		this.firstrun = firstrun;
		storeFirstrun();
	}

	private void storeFirstrun() {
		context.getSharedPreferences(PREF_FIRSTRUN, Activity.MODE_PRIVATE).edit().putBoolean(KEY_FIRSTRUN, firstrun).commit();
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
		storeKeywords();
	}

	private void storeKeywords() {
		context.getSharedPreferences(PREF_KEYWORDS, Activity.MODE_PRIVATE).edit().putString(KEY_KEYWORDS, keywords).commit();
	}

}
