package arnodenhond.astroclock.imwatch;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class NextCalc {
	double latitude;
	double longitude;
	double utcoffset;

	public NextCalc(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		TimeZone tz = SimpleTimeZone.getDefault();
		utcoffset = tz.getOffset(System.currentTimeMillis());
		utcoffset /= 60;
		utcoffset /= 60;
		utcoffset /= 1000;

	}

	public long getNextMidDay() {
		Calendar c = Calendar.getInstance();
		long result = getMidDay(c);
		while (result<System.currentTimeMillis()) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			result = getMidDay(c);
		}
		return result;
	}
	
	private long getMidDay(Calendar c) {
		long sunrise = 0;
		long sunset = 0;
		try {
			double up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			double down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			up+=utcoffset;
			down+=utcoffset;
			Time tup = new Time(up);
			Calendar cup = Calendar.getInstance();
			cup.set(Calendar.YEAR, c.get(Calendar.YEAR));
			cup.set(Calendar.MONTH, c.get(Calendar.MONTH));
			cup.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			cup.set(Calendar.HOUR_OF_DAY, tup.getHour());
			cup.set(Calendar.MINUTE, tup.getMinute());
			sunrise = cup.getTimeInMillis();
			Time tdown = new Time(down);
			Calendar cdown = Calendar.getInstance();
			cdown.set(Calendar.YEAR, c.get(Calendar.YEAR));
			cdown.set(Calendar.MONTH, c.get(Calendar.MONTH));
			cdown.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			cdown.set(Calendar.HOUR_OF_DAY, tdown.getHour());
			cdown.set(Calendar.MINUTE, tdown.getMinute());
			if (cup.getTimeInMillis()>cdown.getTimeInMillis())
				cdown.add(Calendar.DAY_OF_MONTH, 1);
			sunset = cdown.getTimeInMillis();
		} catch (SunTimesException e) {
		}
		long diff = sunset-sunrise;
		return sunrise+(diff/2);
		
	}

	public long getNextMidNight() {
		long midday = getNextMidDay();
		final long HALFDAY = 43200000l;
		if (midday + HALFDAY > System.currentTimeMillis()+(HALFDAY*2))
			return midday - HALFDAY;
		else
			return midday + HALFDAY;
//		Calendar c = Calendar.getInstance();
//		double up = 0;
//		double down = 0;
//		try {
//			up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
//			down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
//		} catch (SunTimesException e) {
//		}
//		double midsun = getSun(up, down);
//		midsun+=utcoffset;
//
//		Time time = new Time(midsun + 1);
//		c.set(Calendar.HOUR_OF_DAY, time.getHour());
//		c.set(Calendar.MINUTE, time.getMinute());
//		c.set(Calendar.SECOND, time.getSecond());
//		if (c.getTimeInMillis() < System.currentTimeMillis()) {
//			c.add(Calendar.DAY_OF_MONTH, 1);
//			try {
//				up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
//				down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
//			} catch (SunTimesException e) {
//			}
//			midsun = getSun(up, down);
//			midsun+=utcoffset;
//			 time = new Time(midsun + 1);
//			c.set(Calendar.HOUR_OF_DAY, time.getHour());
//			c.set(Calendar.MINUTE, time.getMinute());
//			c.set(Calendar.SECOND, time.getSecond());
//		}
//		return c.getTimeInMillis();
	}

	public long getNextSunRise() {
		Calendar c = Calendar.getInstance();
		double up = 0;
		try {
			up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}
		Time time = new Time(up + utcoffset);
		c.set(Calendar.HOUR_OF_DAY, time.getHour());
		c.set(Calendar.MINUTE, time.getMinute());
		c.set(Calendar.SECOND, time.getSecond());
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			try {
				up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			} catch (SunTimesException e) {
			}
			time = new Time(up + utcoffset);
			c.set(Calendar.HOUR_OF_DAY, time.getHour());
			c.set(Calendar.MINUTE, time.getMinute());
			c.set(Calendar.SECOND, time.getSecond());
		}
		return c.getTimeInMillis();
	}

	public long getNextSunSet() {
		Calendar c = Calendar.getInstance();
		double down = 0;
		try {
			down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}
		Time time = new Time(down + utcoffset);
		c.set(Calendar.HOUR_OF_DAY, time.getHour());
		c.set(Calendar.MINUTE, time.getMinute());
		c.set(Calendar.SECOND, time.getSecond());
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			try {
				down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			} catch (SunTimesException e) {
			}
			time = new Time(down + utcoffset);
			c.set(Calendar.HOUR_OF_DAY, time.getHour());
			c.set(Calendar.MINUTE, time.getMinute());
			c.set(Calendar.SECOND, time.getSecond());
		}
		return c.getTimeInMillis();
	}

	private double getSun(double up, double down) {
		double uptime = (down > up ? (down - up) : 24 - (up - down));
		return (uptime / 12d) / 2d;
	}

	public long getNextFullMoon() {
		long time = PhaseOfMoon.TimeOf(3.1416d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(3.1416d, System.currentTimeMillis() + 2551392000l);
		return time;
	}

	public long getNextNewMoon() {
		long time = PhaseOfMoon.TimeOf(0d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(0d, System.currentTimeMillis() + 2551392000l);
		return time;
	}

	public long getNextFirstQuarter() {
		long time = PhaseOfMoon.TimeOf(1.5708d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(1.5708d, System.currentTimeMillis() + 2551392000l);
		return time;
	}

	public long getNextLastQuarter() {
		long time = PhaseOfMoon.TimeOf(4.7124d, System.currentTimeMillis());
		if (time < System.currentTimeMillis())
			time = PhaseOfMoon.TimeOf(4.7124d, System.currentTimeMillis() + 2551392000l);
		return time;
	}

	public long getNextSSol() {
		int[] days = new int[] { 22, 21, 21, 21, 22, 21, 21 };
		int[] hours = new int[] { 5, 11, 17, 23, 4, 10, 16 };
		int[] minutes = new int[] { 30, 11, 11, 3, 48, 44, 28 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	public long getNextNSol() {
		int[] days = new int[] { 21, 20, 21, 21, 21, 20, 21 };
		int[] hours = new int[] { 17, 23, 5, 10, 16, 22, 4 };
		int[] minutes = new int[] { 16, 9, 4, 51, 38, 34, 24 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	public long getNextNEq() {
		int[] days = new int[] { 23, 22, 22, 23, 23, 22, 22 };
		int[] hours = new int[] { 9, 14, 20, 2, 8, 14, 20 };
		int[] minutes = new int[] { 4, 49, 44, 29, 20, 21, 2 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 8);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

	public long getNextSEq() {
		int[] days = new int[] { 20, 20, 20, 20, 20, 20, 20 };
		int[] hours = new int[] { 23, 5, 11, 16, 22, 4, 10 };
		int[] minutes = new int[] { 21, 14, 2, 57, 45, 30, 28 };
		Calendar c = Calendar.getInstance();
		int y = 2011 - c.get(Calendar.YEAR);
		c.set(Calendar.MONTH, 2);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
		}
		return c.getTimeInMillis();
	}

}
