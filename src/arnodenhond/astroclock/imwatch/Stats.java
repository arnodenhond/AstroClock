package arnodenhond.astroclock.imwatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RemoteViews;
import android.widget.TextView;
import arnodenhond.astroclock.imwatch.R;

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
		double latitude = 0.0d;
		double longitude = 0.0d;

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		if (latitude == 0.0d && longitude == 0.0d) {
			latitude = prefs.getFloat("latitude", 0.01f);
			longitude = prefs.getFloat("longitude", 0.01f);
		}

		Calendar c = Calendar.getInstance();
		double upastro = 0;
		double downastro = 0;
		double upnautic = 0;
		double downnautic = 0;
		double upcivil = 0;
		double downcivil = 0;
		try {
			upastro = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ASTRONOMICAL_ZENITH).getFractionalHours();
			downastro = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ASTRONOMICAL_ZENITH).getFractionalHours();
			upnautic = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.NAUTICAL_ZENITH).getFractionalHours();
			downnautic = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.NAUTICAL_ZENITH).getFractionalHours();
			upcivil = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.CIVIL_ZENITH).getFractionalHours();
			downcivil = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.CIVIL_ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}

		boolean north = (latitude > 0 ? true : false);

		double moon = getMoon();
		double year = getYear(north);

		TextView midday = (TextView) findViewById(R.id.midday);
		TextView midnight = (TextView) findViewById(R.id.midnight);
		TextView daylength = (TextView) findViewById(R.id.daylength);
		TextView nightlength = (TextView) findViewById(R.id.nightlength);
		TextView sunriseastro = (TextView) findViewById(R.id.sunriseastro);
		TextView sunsetastro = (TextView) findViewById(R.id.sunsetastro);
		TextView sunrisenautic = (TextView) findViewById(R.id.sunrisenautic);
		TextView sunsetnautic = (TextView) findViewById(R.id.sunsetnautic);
		TextView sunrisecivil = (TextView) findViewById(R.id.sunrisecivil);
		TextView sunsetcivil = (TextView) findViewById(R.id.sunsetcivil);
		TextView nextmoon = (TextView) findViewById(R.id.nextmoon);
		TextView lastmoon = (TextView) findViewById(R.id.lastmoon);
		TextView nextmoondays = (TextView) findViewById(R.id.nextmoondays);
		TextView lastmoondays = (TextView) findViewById(R.id.lastmoondays);
		TextView nextsummerdays = (TextView) findViewById(R.id.nextsummerdays);
		TextView nextsummermoons = (TextView) findViewById(R.id.nextsummermoons);
		TextView lastsummerdays = (TextView) findViewById(R.id.lastsummerdays);
		TextView lastsummermoons = (TextView) findViewById(R.id.lastsummermoons);

		sunriseastro.setText(new Time(upastro + utcoffset).toString());
		sunsetastro.setText(new Time(downastro + utcoffset).toString());
		sunrisenautic.setText(new Time(upnautic + utcoffset).toString());
		sunsetnautic.setText(new Time(downnautic + utcoffset).toString());
		sunrisecivil.setText(new Time(upcivil + utcoffset).toString());
		sunsetcivil.setText(new Time(downcivil + utcoffset).toString());

		double up = 0;
		double down = 0;
		prefs = getSharedPreferences("settings", Activity.MODE_PRIVATE);
		int twilightmode = prefs.getInt("twilight", CIVIL);
		switch (twilightmode) {
		case ASTRO:
			up = upastro;
			down = downastro;
			break;
		case NAUTIC:
			up = upnautic;
			down = downnautic;
			break;
		case CIVIL:
			up = upcivil;
			down = downcivil;
			break;
		}
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

		Calendar nextmoonCal = Calendar.getInstance();
		Calendar lastmoonCal = Calendar.getInstance();
		dnextmoon *= 24d;
		nextmoonCal.add(Calendar.HOUR_OF_DAY, (int) dnextmoon);
		dlastmoon *= -24d;
		lastmoonCal.add(Calendar.HOUR_OF_DAY, (int) dlastmoon);

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

		nextmoon.setText(getResources().getString(R.string.next) + ": " + sdf.format(nextmoonCal.getTime()));
		lastmoon.setText(getResources().getString(R.string.last) + ": " + sdf.format(lastmoonCal.getTime()));

		double dnextyear = year * -365d;
		double dlastyear = 365d - (year * -365d);

		nextsummerdays.setText(numshorter(dnextyear) + " " + getResources().getString(R.string.days));
		lastsummerdays.setText("-" + numshorter(dlastyear) + " " + getResources().getString(R.string.days));

		nextsummermoons.setText(numshort(dnextyear / 29.53d) + " " + getResources().getString(R.string.moons));
		lastsummermoons.setText("-" + numshort(dlastyear / 29.53d) + " " + getResources().getString(R.string.moons));

		Button moreButton = (Button) findViewById(R.id.Button);
		moreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:\"Arno den Hond\"")));
			}
		});

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
