package arnodenhond.astroclock.alerts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import arnodenhond.astroclock.calcuator.NextCalc;
import arnodenhond.astroclock.settings.PrefsReader;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", context);
		tracker.trackEvent("Startup", "Broadcast", "Receiver", 0);

		PrefsReader pr = new PrefsReader(context);

		tracker.setCustomVar(1, "Theme", String.valueOf(pr.getTheme()), 1);
		tracker.setCustomVar(2, "Vibrate", String.valueOf(pr.getNotificationVibrate()), 1);
		tracker.setCustomVar(3, "Audible", String.valueOf(pr.getNotificationAudible()), 1);
		tracker.setCustomVar(4, "Keywords", pr.getKeywords(), 1);
		tracker.trackPageView("/StartupBroadcastReceiver");
		tracker.dispatch();

		double latitude = pr.getLatitude();
		double longitude = pr.getLongitude();

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		NextCalc nc = new NextCalc(latitude, longitude);

		Intent pintent = new Intent(PrefsReader.KEY_ALERT_MIDDAY);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidDay(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_MIDNIGHT);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidNight(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_SUNRISE);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunRise(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_SUNSET);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunSet(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_FULLMOON);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextFullMoon(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_NEWMOON);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextNewMoon(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_FIRSTQUARTER);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextFirstQuarter(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_LASTQUARTER);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextLastQuarter(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_NORTHERNSOLSTICE);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextNSol(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextSSol(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_NORTHWARDEQUINOX);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextNEq(), operation);

		pintent = new Intent(PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.set(AlarmManager.RTC_WAKEUP, nc.getNextSEq(), operation);

	}

}
