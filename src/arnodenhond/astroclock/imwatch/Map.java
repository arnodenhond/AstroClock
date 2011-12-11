package arnodenhond.astroclock.imwatch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Map extends Activity {
	
	MapView mv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		RelativeLayout cv = (RelativeLayout) li.inflate(R.layout.map, null);

		Button b = (Button) cv.findViewById(R.id.setmap);

		mv = new MapView(this, null);
		mv.setButton(b);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(240, 120);
		lp.addRule(RelativeLayout.ABOVE,R.id.setmap);
		cv.addView(mv, 0,lp);
		setContentView(cv);
	}

	public void setmap(View v) {
		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putFloat("latitude", (float) mv.getLat()*-1);
		edit.putFloat("longitude", (float) mv.getLon());
		edit.commit();
		Intent intent = new Intent(this, AstroClock.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
