package arnodenhond.astroclock.livefolder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.format.DateFormat;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.calcuator.NextCalc;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

public class ACProvider extends ContentProvider {

	public static final String COLUMN_TIMEUTC ="time_utc";
	
	@Override
	public String getType(Uri uri) {
		return "astroclock/astroclock";
	}

	@Override
	public boolean onCreate() {
		PrefsReader settings = new PrefsReader(getContext());
		
		if (settings.isRefreshLatLon()) {
			AstroClock.getLocation(getContext(),settings);
		}
		if (settings.isFirstnewversion()) {
			AstroClock.setAlarms(getContext());
			settings.setFirstnewversion(false);
		}
		if (settings.isFirstrun()) {
			AstroClock.getLocation(getContext(),settings);
			AstroClock.setAlerts(getContext());
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		PrefsReader settings = new PrefsReader(getContext());
		
		NextCalc nc = new NextCalc(settings.getLatitude(), settings.getLongitude());
		

		MatrixCursor cursor = new MatrixCursor(new String[] { LiveFolders._ID, LiveFolders.NAME, LiveFolders.DESCRIPTION, COLUMN_TIMEUTC });
		
		Object[][] rows = new Object[12][];
		rows[0] = makeRow(0, PrefsReader.KEY_ALERT_MIDDAY, nc);
		rows[1] = makeRow(1, PrefsReader.KEY_ALERT_MIDNIGHT, nc);
		rows[2] = makeRow(2, PrefsReader.KEY_ALERT_SUNRISE, nc);
		rows[3] = makeRow(3, PrefsReader.KEY_ALERT_SUNSET, nc);

		rows[4] = makeRow(4, PrefsReader.KEY_ALERT_FULLMOON, nc);
		rows[5] = makeRow(5, PrefsReader.KEY_ALERT_NEWMOON, nc);
		rows[6] = makeRow(6, PrefsReader.KEY_ALERT_FIRSTQUARTER, nc);
		rows[7] = makeRow(7, PrefsReader.KEY_ALERT_LASTQUARTER, nc);

		rows[9] = makeRow(8, PrefsReader.KEY_ALERT_NORTHERNSOLSTICE, nc);
		rows[8] = makeRow(9, PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE, nc);
		rows[10] = makeRow(10, PrefsReader.KEY_ALERT_NORTHWARDEQUINOX, nc);
		rows[11] = makeRow(11, PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX, nc);

		Arrays.sort(rows,comparator);
		
		for (int i= 0; i < rows.length; i++) {
			cursor.addRow(rows[i]);
		}
		
		return cursor;

	}

	Comparator<Object[]> comparator = new Comparator<Object[]>() {
		@Override
		public int compare(Object[] object1, Object[] object2) {
			long lhs = (Long) object1[3];
			long rhs = (Long) object2[3];
			long diff = rhs-lhs;
			if (lhs<rhs)
				return -1;
			else if (lhs>rhs)
				return 1;
			else return 0;
		}
	};
	
	private Object[] makeRow(int id, String key, NextCalc nc) {
		Object[] result = new Object[4];
		result[0] = id;
		result[1] = getTitle(key);
		long nextlong = getNext(key, nc);
		Date date = new Date(nextlong);
		result[2] = DateFormat.getDateFormat(getContext()).format(date) + " " + DateFormat.getTimeFormat(getContext()).format(date);
		result[3] = nextlong;
		return result;
	}
	
	// TODO duplicate code in alertsadapter
	private long getNext(String key, NextCalc nc) {
		if (key == PrefsReader.KEY_ALERT_MIDDAY) {
			return nc.getNextMidDay();
		}
		if (key == PrefsReader.KEY_ALERT_MIDNIGHT) {
			return nc.getNextMidNight();
		}
		if (key == PrefsReader.KEY_ALERT_SUNRISE) {
			return nc.getNextSunRise();
		}
		if (key == PrefsReader.KEY_ALERT_SUNSET) {
			return nc.getNextSunSet();
		}

		if (key == PrefsReader.KEY_ALERT_FULLMOON) {
			return nc.getNextFullMoon();
		}
		if (key == PrefsReader.KEY_ALERT_NEWMOON) {
			return nc.getNextNewMoon();
		}
		if (key == PrefsReader.KEY_ALERT_FIRSTQUARTER) {
			return nc.getNextFirstQuarter();
		}
		if (key == PrefsReader.KEY_ALERT_LASTQUARTER) {
			return nc.getNextLastQuarter();
		}

		if (key == PrefsReader.KEY_ALERT_NORTHERNSOLSTICE) {
			return nc.getNextNSol();
		}
		if (key == PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE) {
			return nc.getNextSSol();
		}
		if (key == PrefsReader.KEY_ALERT_NORTHWARDEQUINOX) {
			return nc.getNextNEq();
		}
		if (key == PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX) {
			return nc.getNextSEq();
		}
		return 0;
	}

	private String getTitle(String key) {
		Context context = getContext();

		if (key == PrefsReader.KEY_ALERT_MIDDAY)
			return context.getString(R.string.midday);
		if (key == PrefsReader.KEY_ALERT_MIDNIGHT)
			return context.getString(R.string.midnight);
		if (key == PrefsReader.KEY_ALERT_SUNRISE)
			return context.getString(R.string.sunrise);
		if (key == PrefsReader.KEY_ALERT_SUNSET)
			return context.getString(R.string.sunset);
		if (key == PrefsReader.KEY_ALERT_FULLMOON)
			return context.getString(R.string.fullmoon);
		if (key == PrefsReader.KEY_ALERT_NEWMOON)
			return context.getString(R.string.newmoon);
		if (key == PrefsReader.KEY_ALERT_FIRSTQUARTER)
			return context.getString(R.string.firstquarter);
		if (key == PrefsReader.KEY_ALERT_LASTQUARTER)
			return context.getString(R.string.lastquarter);
		if (key == PrefsReader.KEY_ALERT_NORTHERNSOLSTICE)
			return context.getString(R.string.northernsolstice);
		if (key == PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE)
			return context.getString(R.string.southernsolstice);
		if (key == PrefsReader.KEY_ALERT_NORTHWARDEQUINOX)
			return context.getString(R.string.northwardequinox);
		if (key == PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX)
			return context.getString(R.string.southwardequinox);
		return "error";
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}
}