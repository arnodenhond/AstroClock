package arnodenhond.astroclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Welcome extends Activity implements OnCheckedChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);

		RadioGroup rg = (RadioGroup) findViewById(R.id.rotation);
		rg.setOnCheckedChangeListener(this);
		SharedPreferences prefs = getSharedPreferences("settings", Activity.MODE_PRIVATE);
		boolean pointers = prefs.getBoolean("pointers", true);
		if (pointers) {
			RadioButton rb = (RadioButton) findViewById(R.id.pointers);
			rb.setChecked(true);
		} else {
			RadioButton rb = (RadioButton) findViewById(R.id.rings);
			rb.setChecked(true);
		}

		Intent resultValue = new Intent();
		final int id = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
		setResult(RESULT_OK, resultValue);

		Button button = (Button) findViewById(R.id.OkButton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	RemoteViews views;
	double latitude;
	double longitude;
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		SharedPreferences prefs = getSharedPreferences("settings", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean("pointers", checkedId == R.id.pointers);
		edit.commit();

		AppWidgetManager awm = AppWidgetManager.getInstance(this);

		views = new RemoteViews(getPackageName(), R.layout.layout_appwidget_provider);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		latitude = 0.0d;
		longitude = 0.0d;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
		prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		if (latitude == 0.0d && longitude == 0.0d) {
			latitude = prefs.getFloat("latitude", 0.01f);
			longitude = prefs.getFloat("longitude", 0.01f);
			if (latitude == 0.01f && longitude == 0.01f) {
				showDialog(0);
			} else { 
				BitmapMaker bmmaker = new BitmapMaker(this, 146, latitude, longitude);
				views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
			}
			
		} else {
			edit = prefs.edit();
			edit.putFloat("latitude", (float) latitude);
			edit.putFloat("longitude", (float) longitude);
			edit.commit();
			BitmapMaker bmmaker = new BitmapMaker(this, 146, latitude, longitude);
			views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		}

		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(this, 0, new Intent("AstroClockWidgetStats"), Intent.FLAG_ACTIVITY_NEW_TASK));

		awm.updateAppWidget(new ComponentName(this, AstroClockAppWidgetProvider.class), views);
	}

	@Override
	protected Dialog onCreateDialog(int i) {
		LayoutInflater li = getLayoutInflater();
		View view = li.inflate(R.layout.coordinput, null);
		final EditText eastwest = (EditText) view.findViewById(R.id.degeastwest);
		final EditText northsouth = (EditText) view.findViewById(R.id.degnorthsouth);
		final RadioButton north = (RadioButton) view.findViewById(R.id.north);
		final RadioButton east = (RadioButton) view.findViewById(R.id.east);
		return new AlertDialog.Builder(Welcome.this).setTitle(R.string.coords).setView(view).setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				float lat = Float.parseFloat(northsouth.getText().toString());
				if (!north.isChecked())
					lat *= -1f;
				float lon = Float.parseFloat(eastwest.getText().toString());
				if (!east.isChecked())
					lon *= -1f;
				edit.putFloat("latitude", (float) lat);
				edit.putFloat("longitude", (float) lon);
				edit.commit();

				latitude = lat;
				longitude=lon;

				BitmapMaker bmmaker = new BitmapMaker(Welcome.this, 146, latitude, longitude);
				views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
				
				views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(Welcome.this, 0, new Intent("AstroClockWidgetStats"), Intent.FLAG_ACTIVITY_NEW_TASK));
				AppWidgetManager awm = AppWidgetManager.getInstance(Welcome.this);
				awm.updateAppWidget(new ComponentName(Welcome.this, AstroClockAppWidgetProvider.class), views);

			}
		}).create();
	}

}
