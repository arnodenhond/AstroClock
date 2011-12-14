package arnodenhond.astroclock.imwatch;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

public class BitmapMaker {

	float centerx;
	float centery;
	float radius;
	Paint paint;
	Context ctx;
	double utcoffset;
	double latitude;
	double longitude;
	float year;
	float moon;
	float day;
	float sun;
	boolean north;
	int dimension;
	int theme;

	public BitmapMaker(Context context, int d, double lat, double lon, int theme) {
		this.ctx = context;
		this.dimension = d;
		this.latitude = lat;
		this.longitude = lon;
		this.theme = theme;
	}

	Bitmap makeBitmap() {

		TimeZone tz = SimpleTimeZone.getDefault();
		utcoffset = tz.getOffset(System.currentTimeMillis());
		utcoffset /= 60;
		utcoffset /= 60;
		utcoffset /= 1000;

		Calendar c = Calendar.getInstance();
		double up = 0;
		double down = 0;
		try {
			up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}

		Resources r = ctx.getResources();
		int resBack = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "background", null, null);
		int resRise = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "sunrise", null, null);
		int resSet = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "sunset", null, null);
		int resDay = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "day", null, null);
		int resMoon = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "moon", null, null);
		int resYear = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "year", null, null);
		int resTop = r.getIdentifier("arnodenhond.astroclock.imwatch:drawable/t" + (theme + 1) + "cover", null, null);

		north = (latitude > 0 ? true : false);

		sun = (float) getSun(up, down);
		double dday = getDay(utcoffset, up, down);
		double dmoon = getMoon();
		double dyear = getYear(north);

		year = ((float) dyear) * 2f;
		moon = ((float) dmoon) * 2f;
		day = ((float) dday) * 2f;

		float degyear = year * 180f;
		float degmoon = moon * 180f;
		float degday = day * 180f;
		float degsunrise = sun * 180f;
		float degsunset = sun * -180f;

		Bitmap result = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		centerx = canvas.getWidth() / 2f;
		centery = canvas.getHeight() / 2f;

		paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setAntiAlias(true);
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, dimension, dimension, paint);
		paint.setARGB(255, 0, 0, 0);

		BitmapDrawable bd = (BitmapDrawable) r.getDrawable(resBack);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);

		bd = (BitmapDrawable) r.getDrawable(resSet);
		canvas.rotate(degsunrise, centerx, centery);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);
		canvas.rotate(degsunrise * -1f, centerx, centery);

		bd = (BitmapDrawable) r.getDrawable(resRise);
		canvas.rotate(degsunset, centerx, centery);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);
		canvas.rotate(degsunset * -1f, centerx, centery);

		bd = (BitmapDrawable) r.getDrawable(resYear);
		canvas.rotate(degyear, centerx, centery);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);
		canvas.rotate(degyear * -1f, centerx, centery);

		bd = (BitmapDrawable) r.getDrawable(resMoon);
		canvas.rotate(degmoon, centerx, centery);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);
		canvas.rotate(degmoon * -1f, centerx, centery);

		bd = (BitmapDrawable) r.getDrawable(resDay);
		canvas.rotate(degday, centerx, centery);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);
		canvas.rotate(degday * -1f, centerx, centery);

		bd = (BitmapDrawable) r.getDrawable(resTop);
		canvas.drawBitmap(Bitmap.createScaledBitmap(bd.getBitmap(), dimension, dimension, true), 0, 0, paint);

		return result;
	}

	private static double getYear(boolean north) {
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

	private static double getMoon() {
		double phase = PhaseOfMoon.MoonPhase(System.currentTimeMillis());
		double result = phase / (Math.PI * 2d);
		return result + 0.5d;
	}

	private static double getDay(double utcoffset, double up, double down) {
		up += utcoffset;
		down += utcoffset;
		if (down < 0)
			down += 24;
		if (down > 24)
			down -= 24;
		if (up < 0)
			up += 24;
		if (up > 24)
			up -= 24;

		double uptime = down - up;
		if (uptime < 0)
			uptime += 24;
		double center = up + (uptime / 2d);
		// double uptime = (down > up ? (down - up) : (up - down));
		// double center = up + (uptime / 2d) + utcoffset;
		Calendar c = Calendar.getInstance();
		double fhour = (double) c.get(Calendar.HOUR_OF_DAY);
		double fminute = (double) c.get(Calendar.MINUTE);
		double fsecond = (double) c.get(Calendar.SECOND);
		double now = fhour + (fminute / 60.0d) + (fsecond / 3600.0d);
		double dif = (now - center);
		double result = dif / 24d;
		return result;
	}

	private static double getSun(double up, double down) {
		double uptime = (down > up ? (down - up) : 24 - (up - down));
		return (uptime / 12d) / 2d;
	}

}
