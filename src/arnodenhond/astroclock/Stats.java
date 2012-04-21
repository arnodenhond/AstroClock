package arnodenhond.astroclock;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import arnodenhond.astroclock.calcuator.PhaseOfMoon;
import arnodenhond.astroclock.calcuator.SunTimes;
import arnodenhond.astroclock.calcuator.SunTimesException;
import arnodenhond.astroclock.calcuator.Time;
import arnodenhond.astroclocklite.R;

public class Stats extends Activity {

	static final int CIVIL = 0;
	static final int NAUTIC = 1;
	static final int ASTRO = 2;

	double utcoffset;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);
		findViewById(R.id.outerstats).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Stats.this, AstroClock.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
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

		TextView daylength = (TextView) findViewById(R.id.daylength);
		TextView nightlength = (TextView) findViewById(R.id.nightlength);
		TextView nextdayhours = (TextView) findViewById(R.id.nextdayhours);
		TextView lastdayhours = (TextView) findViewById(R.id.lastdayhours);
		TextView nextmoondays = (TextView) findViewById(R.id.nextmoondays);
		TextView lastmoondays = (TextView) findViewById(R.id.lastmoondays);
		TextView nextsummerdays = (TextView) findViewById(R.id.nextsummerdays);
		TextView nextsummermoons = (TextView) findViewById(R.id.nextsummermoons);
		TextView lastsummerdays = (TextView) findViewById(R.id.lastsummerdays);
		TextView lastsummermoons = (TextView) findViewById(R.id.lastsummermoons);

		Time uptime = new Time(down - up);
		Time downtime = new Time((24 - down) + up);
		daylength.setText(getResources().getString(R.string.daylength) + ": " + uptime.toString());
		nightlength.setText(getResources().getString(R.string.nightlength) + ": " + downtime.toString());

		double midsun = getSun(up, down);
		Time tmidsun = new Time((midsun - 11));
		Time tnow = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		String nextmiddayhours = new String();
		String lastmiddayhours = new String();
		if (tnow.getFractionalHours() < tmidsun.getFractionalHours()) {
			nextmiddayhours = numshort(new Time(tmidsun.getFractionalHours() - tnow.getFractionalHours()).getFractionalHours());
			lastmiddayhours = numshort(new Time(24 - (tmidsun.getFractionalHours() - tnow.getFractionalHours())).getFractionalHours());
		} else {
			nextmiddayhours = numshort(new Time(24 - (tnow.getFractionalHours() - tmidsun.getFractionalHours())).getFractionalHours());
			lastmiddayhours = numshort(new Time(tnow.getFractionalHours() - tmidsun.getFractionalHours()).getFractionalHours());
		}
		nextdayhours.setText(nextmiddayhours + " " + getResources().getString(R.string.hours));
		lastdayhours.setText("-" + lastmiddayhours + " " + getResources().getString(R.string.hours));

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

}
