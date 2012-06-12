package arnodenhond.astroclock.settings.alerts;

import java.util.Arrays;
import java.util.HashSet;

import android.app.ExpandableListActivity;
import android.location.Location;
import android.os.Bundle;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class Alerts extends ExpandableListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alerts);
		setupAd();
		
		AlertsAdapter adapter = new AlertsAdapter(this,null,0,null,null, null, 0, null, null);
		setListAdapter(adapter);
		getExpandableListView().setGroupIndicator(null);
		
	}

	private void setupAd() {
		PrefsReader prefs = new PrefsReader(this);
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adrequest = new AdRequest();
		adrequest.addKeyword(prefs.getKeywords());
		Location location = new Location("AstroClock");
		location.setLatitude(prefs.getLatitude());
		location.setLongitude(prefs.getLongitude());
		adrequest.setLocation(location);
		adrequest.setKeywords(new HashSet<String>(Arrays.asList(prefs.getKeywords().split(","))));
		adView.loadAd(adrequest);
	}

	
}

/*
  		//SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, groups, android.R.layout.simple_expandable_list_item_1, new String[] {"name"}, new int[] {android.R.id.text1}, childs, R.layout.alertrow, new String[] {"title","summary"}, new int[]{android.R.id.title,android.R.id.summary});

ArrayList<Map<String,String>> groups = new ArrayList<Map<String,String>>();
Map<String,String> group;
group = new HashMap<String,String>();
group.put("name", "dag");
groups.add(group);
group = new HashMap<String,String>();
group.put("name", "maan");
groups.add(group);
group = new HashMap<String,String>();
group.put("name", "jaar");
groups.add(group);

ArrayList<ArrayList<Map<String,String>>> childs = new ArrayList<ArrayList<Map<String,String>>>();
ArrayList<Map<String,String>> child;
child = new ArrayList<Map<String,String>>();
Map<String,String> map;
map = new HashMap<String,String>();
map.put("title", "titel");
map.put("summary", "summarie");
child.add(map);
map = new HashMap<String,String>();
map.put("title", "titel");
map.put("summary", "summarie");
child.add(map);
childs.add(child);

child = new ArrayList<Map<String,String>>();
map = new HashMap<String,String>();
map.put("title", "titel");
map.put("summary", "summarie");
child.add(map);
map = new HashMap<String,String>();
map.put("title", "titel");
map.put("summary", "summarie");
child.add(map);
childs.add(child);

child = new ArrayList<Map<String,String>>();
map = new HashMap<String,String>();
map.put("title", "titel");
map.put("summary", "summarie");
child.add(map);
map = new HashMap<String,String>();
map.put("title", "titel");
map.put("summary", "summarie");
child.add(map);
childs.add(child);
*/