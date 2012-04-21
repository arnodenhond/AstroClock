package arnodenhond.astroclock.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclocklite.R;

public class Help extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView sv = new ScrollView(this);
		TextView tv = new TextView(this);
		tv.setText(R.string.helptext);
		tv.setPadding(10, 15, 15, 15);
		sv.addView(tv);
		setContentView(sv);
		
	}
}