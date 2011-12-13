package arnodenhond.astroclock.imwatch;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class Alarms extends PreferenceActivity {
	static final int CIVIL = 0;
	static final int NAUTIC = 1;
	static final int ASTRO = 2;

	double utcoffset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TimeZone tz = SimpleTimeZone.getDefault();
		utcoffset = tz.getOffset(System.currentTimeMillis());
		utcoffset /= 60;
		utcoffset /= 60;
		utcoffset /= 1000;

		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = Float.parseFloat(prefs.getString("latitude", "-35"));
		double longitude = Float.parseFloat(prefs.getString("longitude", "-120"));

		Calendar c = Calendar.getInstance();
		double up = 0;
		double down = 0;
		try {
			up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}

		boolean north = (latitude > 0 ? true : false);

		double moon = getMoon();
		double year = getYear(north);

		double midsun = getSun(up, down);

		addPreferencesFromResource(R.xml.alarms);
		// Get the custom preference
		final SharedPreferences alarms = getSharedPreferences("alarms", Activity.MODE_PRIVATE);

		CheckBoxPreference midday = (CheckBoxPreference) findPreference("midday");
		midday.setChecked(alarms.getBoolean("midday", false));
		midday.setSummary(new Time((midsun - 11)).toString());
		midday.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences.Editor editor = alarms.edit();
				editor.putBoolean("midday", ((CheckBoxPreference) preference).isChecked());
				editor.commit();
				return true;
			}

		});
	}

	private double getYear(boolean north) {
		Calendar top = Calendar.getInstance();
		top.set(Calendar.HOUR_OF_DAY, 12);
		top.set(Calendar.MINUTE, 0);
		if (north)
			top.set(Calendar.MONTH, 5);
		else
			top.set(Calendar.MONTH, 11);
		top.set(Calendar.DAY_OF_MONTH, 21);
		Calendar y = Calendar.getInstance();
		if (y.after(top))
			top.add(Calendar.YEAR, 1);
		double dif = System.currentTimeMillis() - top.getTimeInMillis();
		long start = y.getTimeInMillis();
		y.add(Calendar.YEAR, 1);
		long end = y.getTimeInMillis();
		double year = end - start;
		double result = dif / year;
		return result;
	}

	private double getMoon() {
		double phase = PhaseOfMoon.MoonPhase(System.currentTimeMillis());
		double result = phase / (Math.PI * 2d);
		return result + 0.5d;
	}

	private double getSun(double up, double down) {
		double uptime = (down > up ? (down - up) : 24 - (up - down));
		return (uptime / 12d) / 2d;
	}
}
