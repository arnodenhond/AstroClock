package arnodenhond.astroclock.alerts;

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

	public static int NOTID_DAY=12388;
	public static int NOTID_MOON=12389;
	public static int NOTID_YEAR=12390;
	
	
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

		NextCalc nc = new NextCalc(latitude, longitude);
		if (type.equals("midday")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidDay(), operation);
			message = "Mid Day";
		}
		if (type.equals("midnight")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextMidNight(), operation);
			message = "Mid Night";
		}
		if (type.equals("sunrise")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunRise(), operation);
			message = "Sun Rise";
		}
		if (type.equals("sunset")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSunSet(), operation);
			message = "Sun Set";
		}
		
		if (type.equals("fullmoon")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFullMoon(), operation);
			message = "Full Moon";
		}
		if (type.equals("newmoon")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNewMoon(), operation);
			message = "New Moon";
		}
		if (type.equals("firstquarter")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextFirstQuarter(), operation);
			message = "First Quarter";
		}
		if (type.equals("lastquarter")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextLastQuarter(), operation);
			message = "Last Quarter";
		}

		if (type.equals("northernsolstice")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNSol(), operation);
			message = "Northern Solstice";
		}
		if (type.equals("southernsolstice")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSSol(), operation);
			message = "Southern Solstice";
		}
		if (type.equals("northwardequinox")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextNEq(), operation);
			message = "Northward Equinox";
		}
		if (type.equals("southwardequinox")) {
			am.set(AlarmManager.RTC_WAKEUP, nc.getNextSEq(), operation);
			message = "Southward Equinox";
		}
		
		
		Notification not = new Notification(R.drawable.icon, message, System.currentTimeMillis());
		not.defaults |= Notification.DEFAULT_SOUND;
		not.flags |= Notification.FLAG_AUTO_CANCEL;
		//docs contradict name: will alert sound play on each alert (while older notification is still pending)?
		//not.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		not.setLatestEventInfo(context, message, message, PendingIntent.getActivity(context, 0, new Intent(), 0));
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);		
		nm.notify(NOTID_DAY, not);
	}

}
