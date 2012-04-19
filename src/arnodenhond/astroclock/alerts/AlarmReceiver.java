package arnodenhond.astroclock.alerts;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import arnodenhond.astroclock.calcuator.NextCalc;
import arnodenhond.astroclocklite.R;

public class AlarmReceiver extends BroadcastReceiver {
	double utcoffset;

	public static int NOTID_DAY = 12388;
	public static int NOTID_MOON = 12389;
	public static int NOTID_YEAR = 12390;
	
	final String middayType = "midday";
	final String midnightType = "midnight";
	final String sunriseType = "sunrise";
	final String sunsetType = "sunset";
	final String fullmoonType = "fullmoon";
	final String newmoonType = "newmoon";
	final String firstquarterType = "firstquarter";
	final String lastquarterType = "lastquarter";
	final String northernsolsticeType = "northernsolstice";
	final String southersolsticeType = "southernsolstice";
	final String northwardequinoxType = "northwardequinox";
	final String southwardequinoxType = "southwardequinox";

	@Override
	public void onReceive(Context context, Intent intent) {
		String type = intent.getAction();
		String message = new String();
		SharedPreferences prefs = context.getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		double latitude = Float.parseFloat(prefs.getString("latitude", "-35"));
		double longitude = Float.parseFloat(prefs.getString("longitude", "-120"));

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(type);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);

		int notidtype =0;
		NextCalc nc = new NextCalc(latitude, longitude);
		if (type.equals(middayType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidDay(), operation);
			message = "Mid Day";
			notidtype=NOTID_DAY;
		}
		if (type.equals(midnightType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidNight(), operation);
			message = "Mid Night";
			notidtype=NOTID_DAY;
		}
		if (type.equals(sunriseType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunRise(), operation);
			message = "Sun Rise";
			notidtype=NOTID_DAY;
		}
		if (type.equals(sunsetType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunSet(), operation);
			message = "Sun Set";
			notidtype=NOTID_DAY;
		}
		if (type.equals(fullmoonType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFullMoon(), operation);
			message = "Full Moon";
			notidtype=NOTID_MOON;
		}
		if (type.equals(newmoonType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNewMoon(), operation);
			message = "New Moon";
			notidtype=NOTID_MOON;
		}
		if (type.equals(firstquarterType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFirstQuarter(), operation);
			message = "First Quarter";
			notidtype=NOTID_MOON;
		}
		if (type.equals(lastquarterType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextLastQuarter(), operation);
			message = "Last Quarter";
			notidtype=NOTID_MOON;
		}

		if (type.equals(northernsolsticeType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNSol(), operation);
			message = "Northern Solstice";
			notidtype=NOTID_YEAR;
		}
		if (type.equals(southersolsticeType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSSol(), operation);
			message = "Southern Solstice";
			notidtype=NOTID_YEAR;
		}
		if (type.equals(northwardequinoxType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNEq(), operation);
			message = "Northward Equinox";
			notidtype=NOTID_YEAR;
		}
		if (type.equals(southwardequinoxType)) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSEq(), operation);
			message = "Southward Equinox";
			notidtype=NOTID_YEAR;
		}

		Notification not = new Notification(R.drawable.icon, message, System.currentTimeMillis());
		not.defaults |= Notification.DEFAULT_SOUND;
		not.flags |= Notification.FLAG_AUTO_CANCEL;
		// docs contradict name: will alert sound play on each alert (while
		// older notification is still pending)?
		// not.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		String datestring = new SimpleDateFormat("MMM dd, HH:mm").format(new Date());
		not.setLatestEventInfo(context, message, datestring, PendingIntent.getActivity(context, 0, new Intent(), 0));
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notidtype, not);
	}

}
