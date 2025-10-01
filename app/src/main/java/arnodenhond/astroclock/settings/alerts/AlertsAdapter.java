package arnodenhond.astroclock.settings.alerts;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import arnodenhond.astroclock.calcuator.NextCalc;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.R;

public class AlertsAdapter extends SimpleExpandableListAdapter {

	LayoutInflater li;
	SharedPreferences prefs;
	Context context;
	NextCalc nc;

	public AlertsAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
		this.context = context;
		prefs = context.getSharedPreferences(PrefsReader.PREF_ALERTS, Context.MODE_PRIVATE);
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PrefsReader pr = new PrefsReader(context);
		nc = new NextCalc(pr.getLatitude(), pr.getLongitude());
	}

	private String getKey(int group, int child) {
		switch (group) {
		case 0:
			return PrefsReader.KEY_ALERT_AUDIBLE;
		case 1:
			return PrefsReader.KEY_ALERT_VIBRATE;
		case 2: {
			switch (child) {
			case 0:
				return PrefsReader.KEY_ALERT_MIDDAY;
			case 1:
				return PrefsReader.KEY_ALERT_MIDNIGHT;
			case 2:
				return PrefsReader.KEY_ALERT_SUNRISE;
			case 3:
				return PrefsReader.KEY_ALERT_SUNSET;
			}
		}
		case 3: {
			switch (child) {
			case 0:
				return PrefsReader.KEY_ALERT_FULLMOON;
			case 1:
				return PrefsReader.KEY_ALERT_NEWMOON;
			case 2:
				return PrefsReader.KEY_ALERT_FIRSTQUARTER;
			case 3:
				return PrefsReader.KEY_ALERT_LASTQUARTER;
			}
		}
		case 4: {
			switch (child) {
			case 0:
				return PrefsReader.KEY_ALERT_NORTHERNSOLSTICE;
			case 1:
				return PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE;
			case 2:
				return PrefsReader.KEY_ALERT_NORTHWARDEQUINOX;
			case 3:
				return PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX;
			}
		}
		}
		return null;
	}

	private String getTitle(String key) {
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

	private String getNext(String key) {
		Date date = new Date();
		if (key == PrefsReader.KEY_ALERT_MIDDAY) {
			 date = new Date(nc.getNextMidDay());
		}
		if (key == PrefsReader.KEY_ALERT_MIDNIGHT) {
			 date = new Date(nc.getNextMidNight());
		}
		if (key == PrefsReader.KEY_ALERT_SUNRISE) {
			 date = new Date(nc.getNextSunRise());
		}
		if (key == PrefsReader.KEY_ALERT_SUNSET) {
			 date = new Date(nc.getNextSunSet());
		}

		if (key == PrefsReader.KEY_ALERT_FULLMOON) {
			 date = new Date(nc.getNextFullMoon());
		}
		if (key == PrefsReader.KEY_ALERT_NEWMOON) {
			 date = new Date(nc.getNextNewMoon());
		}
		if (key == PrefsReader.KEY_ALERT_FIRSTQUARTER) {
			 date = new Date(nc.getNextFirstQuarter());
		}
		if (key == PrefsReader.KEY_ALERT_LASTQUARTER) {
			 date = new Date(nc.getNextLastQuarter());
		}

		if (key == PrefsReader.KEY_ALERT_NORTHERNSOLSTICE) {
			 date = new Date(nc.getNextNSol());
		}
		if (key == PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE) {
			 date = new Date(nc.getNextSSol());
		}
		if (key == PrefsReader.KEY_ALERT_NORTHWARDEQUINOX) {
			 date = new Date(nc.getNextNEq());
		}
		if (key == PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX) {
			 date = new Date(nc.getNextSEq());
		}
		return DateFormat.getDateFormat(context).format(date)+" "+DateFormat.getTimeFormat(context).format(date);

	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View row = li.inflate(R.layout.alertrow_child, null);
		final String key = getKey(groupPosition, childPosition);
		((TextView) row.findViewById(android.R.id.title)).setText(getTitle(key));
		((TextView) row.findViewById(android.R.id.summary)).setText(getNext(key));
		final CheckBox cb = (CheckBox) row.findViewById(android.R.id.checkbox);
		cb.setChecked(prefs.getBoolean(key, false));
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.setChecked(!cb.isChecked());
			}
		});
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean(key, isChecked).commit();
			}
		});
		return row;
	}

	private View makeHeader(String text, final String key) {
		View row = li.inflate(R.layout.alertrow_header, null);
		((TextView) row.findViewById(android.R.id.title)).setText(text);
		final CheckBox cb = (CheckBox) row.findViewById(android.R.id.checkbox);
		cb.setChecked(prefs.getBoolean(key, false));
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cb.setChecked(!cb.isChecked());
			}
		});
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean(key, isChecked).commit();
			}
		});
		return row;
	}

	private View makeGroup(String text, boolean isExpanded) {
		View view = li.inflate(R.layout.alertrow_group, null);
		((TextView) view.findViewById(android.R.id.text1)).setText(text);
		if (isExpanded)
			((ImageView) view.findViewById(R.id.groupindicator)).setImageResource(R.drawable.collapse);
		else
			((ImageView) view.findViewById(R.id.groupindicator)).setImageResource(R.drawable.expand);
		return view;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		switch (groupPosition) {
		case 0: {
			return makeHeader(context.getString(R.string.audible), PrefsReader.KEY_ALERT_AUDIBLE);
		}
		case 1: {
			return makeHeader(context.getString(R.string.vibrate), PrefsReader.KEY_ALERT_VIBRATE);
		}
		case 2: {
			return makeGroup(context.getString(R.string.day), isExpanded);
		}
		case 3: {
			return makeGroup(context.getString(R.string.moon), isExpanded);
		}
		case 4: {
			return makeGroup(context.getString(R.string.year), isExpanded);
		}
		}
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		switch (groupPosition) {
		case 0:
		case 1:
			return 0;
		default:
			return 4;
		}
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		return Long.parseLong(String.valueOf(groupId) + String.valueOf(childId));
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return groupId;
	}

	@Override
	public int getGroupCount() {
		return 5;
	}

	@Override
	public Object getGroup(int groupPosition) {
		switch (groupPosition) {
		case 0:
			return "AUDIBLE";
		case 1:
			return "VIBRATE";
		case 2:
			return "DAY";
		case 3:
			return "MOON";
		case 4:
			return "YEAR";
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return prefs.getBoolean(getKey(groupPosition, childPosition), false);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return Long.parseLong(String.valueOf(groupPosition) + String.valueOf(childPosition));
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

}
