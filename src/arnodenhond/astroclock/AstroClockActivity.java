package arnodenhond.astroclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class AstroClockActivity extends Activity {

	private static final int HELP = Menu.FIRST + 1;
	private static final int STATISTICS = Menu.FIRST + 2;
	private static final int COORDS = Menu.FIRST + 3;
	private static final int INSTALL = Menu.FIRST + 4;
	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity);

		iv = (ImageView) findViewById(R.id.clock);

		SharedPreferences firstprefs = getSharedPreferences("firstprefs", Activity.MODE_PRIVATE);
		if (firstprefs.getBoolean("firststart", true)) {
			SharedPreferences.Editor editor = firstprefs.edit();
			editor.putBoolean("firststart", false);
			editor.commit();
			showDialog(INSTALL);
		}

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double latitude = 0.0d;
		double longitude = 0.0d;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
		SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		if (latitude == 0.0d && longitude == 0.0d) {
			latitude = prefs.getFloat("latitude", 0.01f);
			longitude = prefs.getFloat("longitude", 0.01f);
			if (latitude == 0.01f && longitude == 0.01f) {
				showDialog(COORDS);
			}
			BitmapMaker bmmaker = new BitmapMaker(this, 320, latitude, longitude);
			iv.setImageBitmap(bmmaker.makeBitmap());
		} else {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putFloat("latitude", (float) latitude);
			edit.putFloat("longitude", (float) longitude);
			edit.commit();
			BitmapMaker bmmaker = new BitmapMaker(this, 320, latitude, longitude);
			iv.setImageBitmap(bmmaker.makeBitmap());
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, STATISTICS, Menu.NONE, R.string.stats).setIcon(android.R.drawable.ic_menu_sort_by_size);
		menu.add(Menu.NONE, HELP, Menu.NONE, R.string.help).setIcon(android.R.drawable.ic_menu_help);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case STATISTICS:
			startActivity(new Intent("AstroClockWidgetStats"));
			break;
		case HELP:
			showDialog(HELP);
			break;
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int i) {
		switch (i) {
		case HELP:
			TextView textView = new TextView(this);
			textView.setText(R.string.helptext);
			return new AlertDialog.Builder(AstroClockActivity.this).setView(textView).setPositiveButton(R.string.moreapps, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:\"Arno den Hond\"")));
				}
			}).create();
		case COORDS:
			LayoutInflater li = getLayoutInflater();
			View view = li.inflate(R.layout.coordinput, null);
			final EditText eastwest = (EditText) view.findViewById(R.id.degeastwest);
			final EditText northsouth = (EditText) view.findViewById(R.id.degnorthsouth);
			final RadioButton north = (RadioButton) view.findViewById(R.id.north);
			final RadioButton east = (RadioButton) view.findViewById(R.id.east);
			return new AlertDialog.Builder(AstroClockActivity.this).setTitle(R.string.coords).setView(view).setPositiveButton(R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences prefs = getSharedPreferences("latlon", Activity.MODE_PRIVATE);
					SharedPreferences.Editor edit = prefs.edit();
					float latitude = Float.parseFloat(northsouth.getText().toString());
					if (!north.isChecked())
						latitude *= -1f;
					float longitude = Float.parseFloat(eastwest.getText().toString());
					if (!east.isChecked())
						longitude *= -1f;
					edit.putFloat("latitude", (float) latitude);
					edit.putFloat("longitude", (float) longitude);
					edit.commit();
					BitmapMaker bmmaker = new BitmapMaker(AstroClockActivity.this, 320, latitude, longitude);
					iv.setImageBitmap(bmmaker.makeBitmap());
				}
			}).create();
		case INSTALL:
			return new AlertDialog.Builder(AstroClockActivity.this).setMessage(R.string.install).setPositiveButton(R.string.ok, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create();

		}
		return null;
	}

}
