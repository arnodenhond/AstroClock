package arnodenhond.astroclock;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import arnodenhond.astroclock.alerts.BootReceiver;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.settings.alerts.Alerts;
import arnodenhond.astroclock.settings.location.Map;
import arnodenhond.astroclock.settings.themes.Theme;
import arnodenhond.astroclock.R;

public class AstroClock extends Activity {

	private static final String TAG = "AstroClock";
	private static final int DIALOG_WELCOME = 1;
	private static final int REQUEST_LOCATION_PERMISSION = 1;
	PendingIntent pi;

	private FusedLocationProviderClient fusedLocationClient;

	public static boolean supportsAPILevel11() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (supportsAPILevel11()) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
			requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

		PrefsReader pr = new PrefsReader(this);
		if (pr.isRefreshLatLon()) {
			requestLocationUpdate(pr);
		}
		if (pr.isFirstrun()) {
			requestLocationUpdate(pr);
			setAlerts(this);
			pr.setFirstrun(false);
		}
		if (pr.isFirstnewversion()) {
			setAlarms(this);
			showDialog(DIALOG_WELCOME);
			pr.setFirstnewversion(false);
		}
		setContentView(R.layout.activity);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_WELCOME: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.welcometitle);
			builder.setMessage(R.string.welcomebody);
			builder.setNeutralButton(R.string.welcomebutton, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(DIALOG_WELCOME);
				}
			});
			return builder.create();
		}
		}
		return super.onCreateDialog(id);
	}

	public static void setAlarms(Context context) {
		context.sendBroadcast(new Intent(context, BootReceiver.class));
	}

	public static void setAlerts(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PrefsReader.PREF_ALERTS, MODE_PRIVATE);
		prefs.edit().putBoolean(PrefsReader.KEY_ALERT_NORTHERNSOLSTICE, true).putBoolean(PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE, true).putBoolean(PrefsReader.KEY_ALERT_NORTHWARDEQUINOX, true).putBoolean(PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX, true).putBoolean(PrefsReader.KEY_ALERT_FULLMOON, true).commit();
	}

	private void requestLocationUpdate(final PrefsReader prefs) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
			ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// Log.w(TAG, "Location permission not granted. Requesting permissions.");
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
			Log.w(TAG, "Location permission not granted. Cannot update location at this time.");
			return;
		}

		fusedLocationClient.getLastLocation()
			.addOnSuccessListener(this, new OnSuccessListener<Location>() {
				@Override
				public void onSuccess(Location location) {
					if (location != null) {
						prefs.storeLatLon((float) location.getLatitude(), (float) location.getLongitude());
						Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
					} else {
						Log.w(TAG, "Last known location is null.");
					}
				}
			});
	}

    public static void updateLocationData(Context context, final PrefsReader prefs) {
        FusedLocationProviderClient staticFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted. Cannot update location from static method.");
            // In a widget or background service, direct permission requests are not ideal.
            // Ensure permissions are granted before calling this or handle the lack of location gracefully.
            return;
        }

        staticFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            prefs.storeLatLon((float) location.getLatitude(), (float) location.getLongitude());
                            Log.d(TAG, "Location updated from static method: " + location.getLatitude() + ", " + location.getLongitude());
                        } else {
                            Log.w(TAG, "Last known location is null from static method.");
                        }
                    }
                });
    }

	@Override
	protected void onResume() {
		super.onResume();
		PrefsReader pr = new PrefsReader(this);
		if (pr.isUseBackground()) {
			getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), AstroClock.loadFullImage(this, Uri.parse(pr.getBackgroundImage()))));
		} else {
			getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
		}

		final ImageView iv = (ImageView) findViewById(R.id.clock);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.loading);
		iv.post(new Runnable() {
			@Override
			public void run() {
				PrefsReader pr = new PrefsReader(AstroClock.this);
				final double latitude = pr.getLatitude();
				final double longitude = pr.getLongitude();
				final int theme = pr.getTheme();
				pb.setVisibility(View.VISIBLE);
				iv.setImageBitmap(null);
				iv.setImageBitmap(new BitmapMaker(AstroClock.this, 480, latitude, longitude, theme).makeBitmap());
				pb.setVisibility(View.GONE);
			}
		});

		Intent pintent = new Intent(this, AstroClock.class);
		pintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pi = PendingIntent.getActivity(this, 0, pintent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);

	}

	@Override
	protected void onPause() {
		getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
		ImageView iv = (ImageView) findViewById(R.id.clock);
		iv.setImageBitmap(null); // Setting to null is better than setImageDrawable(null) for Bitmaps
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		super.onPause();
	}

	public void clicked(View arg0) {
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.menu, menu);
		String[] titles = getResources().getStringArray(R.array.settingsoptions);
		menu.findItem(R.id.Theme).setTitle(titles[0]).setIntent(new Intent(AstroClock.this, Theme.class));
		menu.findItem(R.id.Alerts).setTitle(titles[1]).setIntent(new Intent(AstroClock.this, Alerts.class));
		menu.findItem(R.id.Location).setTitle(titles[2]).setIntent(new Intent(AstroClock.this, Map.class));
		menu.findItem(R.id.About).setTitle(titles[3]).setIntent(new Intent(AstroClock.this, About.class));
		return super.onCreateOptionsMenu(menu);
	}


	public static Bitmap loadFullImage(Context context, Uri photoUri) {
		Cursor photoCursor = null;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			photoCursor = context.getContentResolver().query(photoUri, projection, null, null, null);

			if (photoCursor != null && photoCursor.getCount() == 1) {
				photoCursor.moveToFirst();
				String photoFilePath = photoCursor.getString(photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)); // Added OrThrow

				Options opts = new Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(photoFilePath, opts);

				int width_tmp = opts.outWidth, height_tmp = opts.outHeight;
				int scale = 1;
				while (true) {
					if (width_tmp / 2 < metrics.widthPixels || height_tmp / 2 < metrics.heightPixels)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale++;
				}
				opts = new Options();
				opts.inSampleSize = scale;

				return BitmapFactory.decodeFile(photoFilePath, opts);
			}
		} catch (Exception e) { // Catch generic exception for safety
            Log.e(TAG, "Error loading full image", e);
        } finally {
			if (photoCursor != null) {
				photoCursor.close();
			}
		}

		return null;
	}

	/* Optional: Handle permission request result
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_LOCATION_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission was granted, try to get location again
				PrefsReader pr = new PrefsReader(this);
				requestLocationUpdate(pr);
			} else {
				// Permission denied
				Log.w(TAG, "Location permission denied by user.");
			}
		}
	}
	*/
}
