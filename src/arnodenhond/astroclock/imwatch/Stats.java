package arnodenhond.astroclock.imwatch;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Stats extends Activity {

	static final int CIVIL = 0;
	static final int NAUTIC = 1;
	static final int ASTRO = 2;

	double utcoffset;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
	}

	@Override
	protected void onResume() {
		super.onResume();
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

		TextView midday = (TextView) findViewById(R.id.midday);
		TextView midnight = (TextView) findViewById(R.id.midnight);
		TextView daylength = (TextView) findViewById(R.id.daylength);
		TextView nightlength = (TextView) findViewById(R.id.nightlength);
		TextView nextmoondays = (TextView) findViewById(R.id.nextmoondays);
		TextView lastmoondays = (TextView) findViewById(R.id.lastmoondays);
		TextView nextsummerdays = (TextView) findViewById(R.id.nextsummerdays);
		TextView nextsummermoons = (TextView) findViewById(R.id.nextsummermoons);
		TextView lastsummerdays = (TextView) findViewById(R.id.lastsummerdays);
		TextView lastsummermoons = (TextView) findViewById(R.id.lastsummermoons);

		double midsun = getSun(up, down);
		midday.setText(getResources().getString(R.string.midday) + ": " + new Time((midsun - 11)).toString());
		midnight.setText(getResources().getString(R.string.midnight) + ": " + new Time(midsun + 1).toString());

		Time uptime = new Time(down - up);
		Time downtime = new Time((24 - down) + up);
		daylength.setText(getResources().getString(R.string.daylength) + ": " + uptime.toString());
		nightlength.setText(getResources().getString(R.string.nightlength) + ": " + downtime.toString());

		double dlastmoon;
		if (moon > 1d)
			dlastmoon = (moon - 1d) * 29.53d;
		else
			dlastmoon = moon * 29.53d;
		lastmoondays.setText("-" + numshort(dlastmoon) + " " + getResources().getString(R.string.days));
		double dnextmoon = 29.53d - dlastmoon;
		nextmoondays.setText(numshort(dnextmoon) + " " + getResources().getString(R.string.days));

		double dnextyear = year * -365d;
		double dlastyear = 365d - (year * -365d);

		nextsummerdays.setText(numshorter(dnextyear) + " " + getResources().getString(R.string.days));
		lastsummerdays.setText("-" + numshorter(dlastyear) + " " + getResources().getString(R.string.days));

		nextsummermoons.setText(numshort(dnextyear / 29.53d) + " " + getResources().getString(R.string.moons));
		lastsummermoons.setText("-" + numshort(dlastyear / 29.53d) + " " + getResources().getString(R.string.moons));

	}

	private String numshort(double num) {
		String result = "";
		String snum = Double.toString(num);
		result = snum.substring(0, snum.indexOf("."));
		String dec = snum.substring(snum.indexOf("."));
		if (dec.length() > 2)
			result += dec.substring(0, 3);
		else
			result += dec;
		return result;
	}

	private String numshorter(double num) {
		String snum = Double.toString(num);
		return snum.substring(0, snum.indexOf("."));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Clock").setIcon(android.R.drawable.ic_menu_sort_by_size);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		startActivity(new Intent("AstroClockWidgetClock"));
		return true;
	}

}
