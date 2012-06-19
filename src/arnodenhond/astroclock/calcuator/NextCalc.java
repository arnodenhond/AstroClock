package arnodenhond.astroclock.calcuator;

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
		long result = getMid(c,false);
		while (result < (System.currentTimeMillis()+1000)) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			result = getMid(c,false);
		}
		return result;
	}

	private long getMid(Calendar corig, boolean night) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, corig.get(Calendar.YEAR));
		c.set(Calendar.MONTH, corig.get(Calendar.MONTH));
		c.set(Calendar.DAY_OF_MONTH, corig.get(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, corig.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, corig.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, corig.get(Calendar.SECOND));
		long sunrise = 0;
		long sunset = 0;
		try {
			double up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			double down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			up += utcoffset;
			down += utcoffset;
			Time tup = new Time(up);
			Calendar cup = Calendar.getInstance();
			cup.set(Calendar.YEAR, c.get(Calendar.YEAR));
			cup.set(Calendar.MONTH, c.get(Calendar.MONTH));
			cup.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			cup.set(Calendar.HOUR_OF_DAY, tup.getHour());
			cup.set(Calendar.MINUTE, tup.getMinute());
			cup.set(Calendar.SECOND, tup.getSecond());
			sunrise = cup.getTimeInMillis();
			Time tdown = new Time(down);
			Calendar cdown = Calendar.getInstance();
			cdown.set(Calendar.YEAR, c.get(Calendar.YEAR));
			cdown.set(Calendar.MONTH, c.get(Calendar.MONTH));
			cdown.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
			cdown.set(Calendar.HOUR_OF_DAY, tdown.getHour());
			cdown.set(Calendar.MINUTE, tdown.getMinute());
			cdown.set(Calendar.SECOND, tdown.getSecond());
			if (cup.getTimeInMillis() > cdown.getTimeInMillis()) {
				c.add(Calendar.DAY_OF_MONTH, 1);
				down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
				down += utcoffset;
				tdown = new Time(down);
				cdown = Calendar.getInstance();
				cdown.set(Calendar.YEAR, c.get(Calendar.YEAR));
				cdown.set(Calendar.MONTH, c.get(Calendar.MONTH));
				cdown.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
				cdown.set(Calendar.HOUR_OF_DAY, tdown.getHour());
				cdown.set(Calendar.MINUTE, tdown.getMinute());
				cdown.set(Calendar.SECOND, tdown.getSecond());
			}
			sunset = cdown.getTimeInMillis();
		} catch (SunTimesException e) {
		}
		long diff = sunset - sunrise;
		return sunrise + (diff / 2) + (night?43200000l:0);
	}

	public long getNextMidNight() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		long result = getMid(c,true);
		while (result < (System.currentTimeMillis()+1000)) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			result = getMid(c,true);
		}
		return result;
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
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		int y = c.get(Calendar.YEAR) - 2011;
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		c.set(Calendar.SECOND, 0);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
	}

	public long getNextNSol() {
		int[] days = new int[] { 21, 20, 21, 21, 21, 20, 21 };
		int[] hours = new int[] { 17, 23, 5, 10, 16, 22, 4 };
		int[] minutes = new int[] { 16, 9, 4, 51, 38, 34, 24 };
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		int y = c.get(Calendar.YEAR) - 2011;
		c.set(Calendar.MONTH, 5);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		c.set(Calendar.SECOND, 0);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
	}

	public long getNextNEq() {
		int[] days = new int[] { 23, 22, 22, 23, 23, 22, 22 };
		int[] hours = new int[] { 9, 14, 20, 2, 8, 14, 20 };
		int[] minutes = new int[] { 4, 49, 44, 29, 20, 21, 2 };
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		int y = c.get(Calendar.YEAR) - 2011;
		c.set(Calendar.MONTH, 8);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		c.set(Calendar.SECOND, 0);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
	}

	public long getNextSEq() {
		int[] days = new int[] { 20, 20, 20, 20, 20, 20, 20 };
		int[] hours = new int[] { 23, 5, 11, 16, 22, 4, 10 };
		int[] minutes = new int[] { 21, 14, 2, 57, 45, 30, 28 };
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		int y = c.get(Calendar.YEAR) - 2011;
		c.set(Calendar.MONTH, 2);
		c.set(Calendar.DATE, days[y]);
		c.set(Calendar.HOUR_OF_DAY, hours[y]);
		c.set(Calendar.MINUTE, minutes[y]);
		c.set(Calendar.SECOND, 0);
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			y++;
			c.set(Calendar.YEAR, 2011 + y);
			c.set(Calendar.DATE, days[y]);
			c.set(Calendar.HOUR_OF_DAY, hours[y]);
			c.set(Calendar.MINUTE, minutes[y]);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTimeInMillis();
	}

}
