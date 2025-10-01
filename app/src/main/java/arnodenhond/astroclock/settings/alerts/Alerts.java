package arnodenhond.astroclock.settings.alerts;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.R;


public class Alerts extends ExpandableListActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.alerts);

		AlertsAdapter adapter = new AlertsAdapter(this, null, 0, null, null, null, 0, null, null);
		setListAdapter(adapter);
		getExpandableListView().setGroupIndicator(null);

	}

}
