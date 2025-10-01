package arnodenhond.astroclock.alerts;

import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;

import androidx.core.app.NotificationCompat;

import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.calcuator.NextCalc;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.R;

public class AlarmReceiver extends BroadcastReceiver {
	double utcoffset;

	public static final int NOTID_DAY = 12388;
	public static final int NOTID_MOON = 12389;
	public static final int NOTID_YEAR = 12390;
	public static final String CHANNEL_ID = "astroclock_alerts_channel";

	@Override
	public void onReceive(Context context, Intent intent) {
		String type = intent.getAction();
		String message = null;
		PrefsReader prefs = new PrefsReader(context);
		if (prefs.isRefreshLatLon()) {
			AstroClock.updateLocationData(context,prefs);
		}
		
		double latitude = prefs.getLatitude();
		double longitude = prefs.getLongitude();

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(type);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); 

		int notidtype = -1;
		NextCalc nc = new NextCalc(latitude, longitude);
		if (type.equals(PrefsReader.KEY_ALERT_MIDDAY)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidDay(), operation);
			if (prefs.isAlert_midday()) {
				message = context.getString(R.string.midday);
				notidtype = NOTID_DAY;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_MIDNIGHT)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidNight(), operation);
			if (prefs.isAlert_midnight()) {
				message = context.getString(R.string.midnight);
				notidtype = NOTID_DAY;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_SUNRISE)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunRise(), operation);
			if (prefs.isAlert_sunrise()) {
				message = context.getString(R.string.sunrise);
				notidtype = NOTID_DAY;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_SUNSET)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunSet(), operation);
			if (prefs.isAlert_sunset()) {
				message = context.getString(R.string.sunset);
				notidtype = NOTID_DAY;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_FULLMOON)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFullMoon(), operation);
			if (prefs.isAlert_fullmoon()) {
				message = context.getString(R.string.fullmoon);
				notidtype = NOTID_MOON;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_NEWMOON)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNewMoon(), operation);
			if (prefs.isAlert_newmoon()) {
				message = context.getString(R.string.newmoon);
				notidtype = NOTID_MOON;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_FIRSTQUARTER)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFirstQuarter(), operation);
			if (prefs.isAlert_firstquarter()) {
				message = context.getString(R.string.firstquarter);
				notidtype = NOTID_MOON;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_LASTQUARTER)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextLastQuarter(), operation);
			if (prefs.isAlert_lastquarter()) {
				message = context.getString(R.string.lastquarter);
				notidtype = NOTID_MOON;
			}
		}

		if (type.equals(PrefsReader.KEY_ALERT_NORTHERNSOLSTICE)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNSol(), operation);
			if (prefs.isAlert_northernsolstice()) {
				message = context.getString(R.string.northernsolstice);
				notidtype = NOTID_YEAR;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSSol(), operation);
			if (prefs.isAlert_southernsolstice()) {
				message = context.getString(R.string.southernsolstice);
				notidtype = NOTID_YEAR;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_NORTHWARDEQUINOX)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNEq(), operation);
			if (prefs.isAlert_northwardequinox()) {
				message = context.getString(R.string.northwardequinox);
				notidtype = NOTID_YEAR;
			}
		}
		if (type.equals(PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSEq(), operation);
			if (prefs.isAlert_southwardequinox()) {
				message = context.getString(R.string.southwardequinox);
				notidtype = NOTID_YEAR;
			}
		}

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = context.getString(R.string.channel_name); // You should add this string to your resources
			String description = context.getString(R.string.channel_description); // You should add this string to your resources
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			nm.createNotificationChannel(channel);
		}

		if (message != null) {
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, 
                new Intent(context, AstroClock.class), PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

			String datestring = DateFormat.getDateFormat(context).format(new Date())+" "+DateFormat.getTimeFormat(context).format(new Date());

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.statusbaricon) // TODO: Consider different icons per event type
				.setContentTitle(message)
				.setContentText(datestring)
				.setContentIntent(contentIntent)
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis());

			int defaults = 0;
			if (prefs.getNotificationAudible()) {
				defaults |= Notification.DEFAULT_SOUND;
			}
			if (prefs.getNotificationVibrate()) {
				defaults |= Notification.DEFAULT_VIBRATE;
			}
			builder.setDefaults(defaults);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            }
            
			nm.notify(notidtype, builder.build());
		}
	}

}
