package arnodenhond.astroclock.imwatch;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Alarms extends PreferenceActivity {

	SharedPreferences alarms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("AstroClock Alarms");
		addPreferencesFromResource(R.xml.alarms);
		
		Preference backday = findPreference("backday");
		Preference backmoon = findPreference("backmoon");
		Preference backyear = findPreference("backyear");
		backday.setOnPreferenceClickListener(backlistener);
		backmoon.setOnPreferenceClickListener(backlistener);
		backyear.setOnPreferenceClickListener(backlistener);

		Preference done = findPreference("done");
		done.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Alarms.this, AstroClock.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return false;
			}
		});

		alarms = getSharedPreferences("alarms", Activity.MODE_PRIVATE);

		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = Float.parseFloat(prefs.getString("latitude", "-35"));
		double longitude = Float.parseFloat(prefs.getString("longitude", "-120"));

		NextCalc nc = new NextCalc(latitude, longitude);

		SimpleDateFormat sdf = new SimpleDateFormat("dd, HH:mm");

		doPref("midday", nc.getNextMidDay(), sdf);
		doPref("midnight", nc.getNextMidNight(), sdf);
		doPref("sunrise", nc.getNextSunRise(), sdf);
		doPref("sunset", nc.getNextSunSet(), sdf);

		sdf = new SimpleDateFormat("MMM dd, HH:mm");
		doPref("fullmoon", nc.getNextFullMoon(), sdf);
		doPref("newmoon", nc.getNextNewMoon(), sdf);
		doPref("firstquarter", nc.getNextFirstQuarter(), sdf);
		doPref("lastquarter", nc.getNextLastQuarter(), sdf);

		sdf = new SimpleDateFormat("MMM dd yyyy, HH:mm");
		doPref("northernsolstice", nc.getNextNSol(), sdf);
		doPref("southernsolstice", nc.getNextSSol(), sdf);
		doPref("northwardequinox", nc.getNextNEq(), sdf);
		doPref("southwardequinox", nc.getNextSEq(), sdf);
	}

	OnPreferenceClickListener backlistener = new OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(Alarms.this, Alarms.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return false;
		}
	};
	
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

			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			Intent intent = new Intent(Alarms.this, AlarmReceiver.class);
			intent.setAction(preference.getKey());
			PendingIntent operation = PendingIntent.getBroadcast(Alarms.this, 0, intent, 0);
			if (checked) {
				am.set(AlarmManager.RTC_WAKEUP, time, operation);
			} else {
				am.cancel(operation);
			}

			Toast.makeText(Alarms.this, checked ? "Alarm Set" : "Alarm Removed", Toast.LENGTH_SHORT).show();
			return true;
		}

	}

}
