package arnodenhond.astroclock.imwatch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences prefs = context.getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = Float.parseFloat(prefs.getString("latitude", "-35"));
		double longitude = Float.parseFloat(prefs.getString("longitude", "-120"));

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		NextCalc nc = new NextCalc(latitude, longitude);

		SharedPreferences alarms = context.getSharedPreferences("alarms", Activity.MODE_PRIVATE);

		if (alarms.getBoolean("midday", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("midday");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidDay(), operation);
		}
		if (alarms.getBoolean("midnight", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("midnight");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidNight(), operation);
		}
		if (alarms.getBoolean("sunrise", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("sunrise");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunRise(), operation);
		}
		if (alarms.getBoolean("sunset", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("sunset");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunSet(), operation);
		}
		
		if (alarms.getBoolean("fullmoon", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("fullmoon");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFullMoon(), operation);
		}		
		if (alarms.getBoolean("newmoon", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("newmoon");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNewMoon(), operation);
		}		
		if (alarms.getBoolean("firstquarter", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("firstquarter");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFirstQuarter(), operation);
		}		
		if (alarms.getBoolean("lastquarter", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("lastquarter");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextLastQuarter(), operation);
		}		

		
		if (alarms.getBoolean("northernsolstice", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("northernsolstice");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNSol(), operation);
		}		
		if (alarms.getBoolean("southernsolstice", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("southernsolstice");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSSol(), operation);
		}		
		if (alarms.getBoolean("northwardequinox", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("northwardequinox");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNEq(), operation);
		}		
		if (alarms.getBoolean("southwardequinox", false)) {
			Intent pintent = new Intent(context, AlarmReceiver.class);
			pintent.setAction("southwardequinox");
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSEq(), operation);
		}		

		
	}

}
