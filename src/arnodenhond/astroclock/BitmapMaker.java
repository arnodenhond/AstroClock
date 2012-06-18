package arnodenhond.astroclock;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import arnodenhond.astroclock.calcuator.PhaseOfMoon;
import arnodenhond.astroclock.calcuator.SunTimes;
import arnodenhond.astroclock.calcuator.SunTimesException;

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

	private Bitmap readBitmapDrawable(String name) {
		Bitmap bmp = BitmapFactory.decodeFile(ctx.getFilesDir().getPath() + "/" + name);
		if (bmp != null) {
			return bmp;
		} else {
			Resources r = ctx.getResources();
			int res = r.getIdentifier("arnodenhond.astroclocklite:drawable/t3" + name.substring(0, name.indexOf('.')), null, null);
			return BitmapFactory.decodeResource(ctx.getResources(), res); 
		}
	}

	public Bitmap makeBitmap() {

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

		Bitmap bmp = Bitmap.createScaledBitmap(readBitmapDrawable("background.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();

		canvas.rotate(degsunrise, centerx, centery);
		bmp = Bitmap.createScaledBitmap(readBitmapDrawable("sunset.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();
		canvas.rotate(degsunrise * -1f, centerx, centery);

		canvas.rotate(degsunset, centerx, centery);
		bmp = Bitmap.createScaledBitmap(readBitmapDrawable("sunrise.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();
		canvas.rotate(degsunset * -1f, centerx, centery);

		canvas.rotate(degyear, centerx, centery);
		bmp = Bitmap.createScaledBitmap(readBitmapDrawable("year.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();
		canvas.rotate(degyear * -1f, centerx, centery);

		canvas.rotate(degmoon, centerx, centery);
		bmp = Bitmap.createScaledBitmap(readBitmapDrawable("moon.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();
		canvas.rotate(degmoon * -1f, centerx, centery);

		canvas.rotate(degday, centerx, centery);
		bmp = Bitmap.createScaledBitmap(readBitmapDrawable("day.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();
		canvas.rotate(degday * -1f, centerx, centery);

		bmp = Bitmap.createScaledBitmap(readBitmapDrawable("cover.png"), dimension, dimension, true);
		canvas.drawBitmap(bmp, 0, 0, paint);
		bmp.recycle();
		bmp = null;
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
