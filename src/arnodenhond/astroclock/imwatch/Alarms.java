package arnodenhond.astroclock.imwatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

		CheckBoxPreference cbp = (CheckBoxPreference) findPreference("midday");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(new Time((midsun - 11)).toString());
		cbp.setOnPreferenceClickListener(prefsaver);

		cbp = (CheckBoxPreference) findPreference("midnight");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(new Time(midsun + 1).toString());
		cbp.setOnPreferenceClickListener(prefsaver);

		cbp = (CheckBoxPreference) findPreference("sunrise");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(new Time(up + utcoffset).toString());
		cbp.setOnPreferenceClickListener(prefsaver);

		cbp = (CheckBoxPreference) findPreference("sunset");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(new Time(down + utcoffset).toString());
		cbp.setOnPreferenceClickListener(prefsaver);

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");

		long time = PhaseOfMoon.TimeOf(3.1416d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(3.1416d, System.currentTimeMillis() + 2551392000l);
		cbp = (CheckBoxPreference) findPreference("fullmoon");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(time)));
		cbp.setOnPreferenceClickListener(prefsaver);

		time = PhaseOfMoon.TimeOf(0d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(0d, System.currentTimeMillis() + 2551392000l);
		cbp = (CheckBoxPreference) findPreference("newmoon");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(time)));
		cbp.setOnPreferenceClickListener(prefsaver);

		time = PhaseOfMoon.TimeOf(1.5708d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(1.5708d, System.currentTimeMillis() + 2551392000l);
		cbp = (CheckBoxPreference) findPreference("firstquarter");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(time)));
		cbp.setOnPreferenceClickListener(prefsaver);

		time = PhaseOfMoon.TimeOf(4.7124d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(4.7124d, System.currentTimeMillis() + 2551392000l);
		cbp = (CheckBoxPreference) findPreference("lastquarter");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(time)));
		cbp.setOnPreferenceClickListener(prefsaver);

		sdf = new SimpleDateFormat("MMM dd yyyy, HH:mm");

		cbp = (CheckBoxPreference) findPreference("northernsolstice");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(getNextNSol())));
		cbp.setOnPreferenceClickListener(prefsaver);

		cbp = (CheckBoxPreference) findPreference("southernsolstice");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(getNextSSol())));
		cbp.setOnPreferenceClickListener(prefsaver);

		cbp = (CheckBoxPreference) findPreference("northwardequinox");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(getNextNEq())));
		cbp.setOnPreferenceClickListener(prefsaver);

		cbp = (CheckBoxPreference) findPreference("southwardequinox");
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(sdf.format(new Date(getNextSEq())));
		cbp.setOnPreferenceClickListener(prefsaver);

	}

	private long getNextNSol() {
		int[] days = new int[] { 22, 21, 21, 21, 22, 21, 21 };
		int[] hours = new int[] { 5, 11, 17, 23, 4, 10, 16 };
		int[] minutes = new int[] { 30, 11, 11, 3, 48, 44, 28 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	private long getNextSSol() {
		int[] days = new int[] { 21, 20, 21, 21, 21, 20, 21 };
		int[] hours = new int[] { 17, 23, 5, 10, 16, 22, 4 };
		int[] minutes = new int[] { 16, 9, 4, 51, 38, 34, 24 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	private long getNextNEq() {
		int[] days = new int[] { 23, 22, 22, 23, 23, 22, 22 };
		int[] hours = new int[] { 9, 14, 20, 2, 8, 14, 20 };
		int[] minutes = new int[] { 4, 49, 44, 29, 20, 21, 2 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 8);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	private long getNextSEq() {
		int[] days = new int[] { 20, 20, 20, 20, 20, 20, 20 };
		int[] hours = new int[] { 23, 5, 11, 16, 22, 4, 10 };
		int[] minutes = new int[] { 21, 14, 2, 57, 45, 30, 28 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 2);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	OnPreferenceClickListener prefsaver = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {
			getSharedPreferences("alarms", Activity.MODE_PRIVATE).edit().putBoolean(preference.getKey(), ((CheckBoxPreference) preference).isChecked()).commit();
			return true;
		}
	};

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
		// return phase;
	}

	private double getSun(double up, double down) {
		double uptime = (down > up ? (down - up) : 24 - (up - down));
		return (uptime / 12d) / 2d;
	}
}
