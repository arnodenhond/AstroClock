package arnodenhond.astroclock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import arnodenhond.astroclocklite.R;

public class Map extends Activity {

	RelativeLayout cv;
	MapView mv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		cv = (RelativeLayout) li.inflate(R.layout.map, null);
		setContentView(cv);

		final EditText latet = (EditText) cv.findViewById(R.id.latet);
		final EditText lonet = (EditText) cv.findViewById(R.id.lonet);

		mv = new MapView(this, null);
		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		latet.setText(prefs.getString("latitude", "00"));
		lonet.setText(prefs.getString("longitude", "00"));
		mv.setFields(latet, lonet);

		latet.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					int lat = Integer.parseInt(latet.getText().toString());
					mv.setLat(lat);
					mv.invalidate();
				} catch (NumberFormatException nfe) {
				}
				return false;
			}
		});

		lonet.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					int lon = Integer.parseInt(lonet.getText().toString());
					mv.setLon(lon);
					mv.invalidate();
				} catch (NumberFormatException nfe) {
				}
				return false;
			}
		});

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(240, 120);
		lp.addRule(RelativeLayout.ABOVE, R.id.setmap);
		cv.addView(mv, 0, lp);
	}

	public void setmap(View v) {
		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString("latitude", ((EditText) cv.findViewById(R.id.latet)).getText().toString());
		edit.putString("longitude", ((EditText) cv.findViewById(R.id.lonet)).getText().toString());
		edit.commit();
		Intent intent = new Intent(this, AstroClock.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
