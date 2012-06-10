package arnodenhond.astroclock.settings.alerts;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import arnodenhond.astroclock.calcuator.NextCalc;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

public class Alarms extends PreferenceActivity {

	// TODO use prefsreader
	SharedPreferences alarms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.alarms);
		alarms = getSharedPreferences("alarms", Activity.MODE_PRIVATE);
		PrefsReader prefs = new PrefsReader(this);
		double latitude = prefs.getLatitude();
		double longitude = prefs.getLongitude();

		NextCalc nc = new NextCalc(latitude, longitude);

		SimpleDateFormat sdf = new SimpleDateFormat("dd, HH:mm");
		((CheckBoxPreference) findPreference("audible")).setChecked(prefs.getNotificationAudible());
		((CheckBoxPreference) findPreference("vibrate")).setChecked(prefs.getNotificationVibrate());
		findPreference("audible").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				boolean checked = ((CheckBoxPreference) preference).isChecked();
				getSharedPreferences("alarms", Activity.MODE_PRIVATE).edit().putBoolean("audible", checked).commit();
				return true;
			}
		});
		findPreference("vibrate").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				boolean checked = ((CheckBoxPreference) preference).isChecked();
				getSharedPreferences("alarms", Activity.MODE_PRIVATE).edit().putBoolean("vibrate", checked).commit();
				return true;
			}
		});

		doPref(PrefsReader.KEY_ALERT_MIDDAY, nc.getNextMidDay(), sdf);
		doPref(PrefsReader.KEY_ALERT_MIDNIGHT, nc.getNextMidNight(), sdf);
		doPref(PrefsReader.KEY_ALERT_SUNRISE, nc.getNextSunRise(), sdf);
		doPref(PrefsReader.KEY_ALERT_SUNSET, nc.getNextSunSet(), sdf);

		sdf = new SimpleDateFormat("MMM dd, HH:mm");
		doPref(PrefsReader.KEY_ALERT_FULLMOON, nc.getNextFullMoon(), sdf);
		doPref(PrefsReader.KEY_ALERT_NEWMOON, nc.getNextNewMoon(), sdf);
		doPref(PrefsReader.KEY_ALERT_FIRSTQUARTER, nc.getNextFirstQuarter(), sdf);
		doPref(PrefsReader.KEY_ALERT_LASTQUARTER, nc.getNextLastQuarter(), sdf);

		sdf = new SimpleDateFormat("MMM dd yyyy, HH:mm");
		doPref(PrefsReader.KEY_ALERT_NORTHERNSOLSTICE, nc.getNextNSol(), sdf);
		doPref(PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE, nc.getNextSSol(), sdf);
		doPref(PrefsReader.KEY_ALERT_NORTHWARDEQUINOX, nc.getNextNEq(), sdf);
		doPref(PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX, nc.getNextSEq(), sdf);
	}

	private void doPref(String key, long time, SimpleDateFormat formatter) {
		CheckBoxPreference cbp = (CheckBoxPreference) findPreference(key);
		cbp.setChecked(alarms.getBoolean(cbp.getKey(), false));
		cbp.setSummary(formatter.format(new Date(time)));
		PrefSaver prefsaver = new PrefSaver(time);
		cbp.setOnPreferenceClickListener(prefsaver);
	}

	class PrefSaver implements OnPreferenceClickListener {
		long time;

		PrefSaver(long time) {
			this.time = time;
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			boolean checked = ((CheckBoxPreference) preference).isChecked();
			getSharedPreferences("alarms", Activity.MODE_PRIVATE).edit().putBoolean(preference.getKey(), checked).commit();
			return true;
		}

	}

}
