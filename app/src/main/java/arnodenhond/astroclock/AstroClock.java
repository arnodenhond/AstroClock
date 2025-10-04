package arnodenhond.astroclock;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
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
	public static final String ACTION_LOCATION_UPDATED = "arnodenhond.astroclock.LOCATION_UPDATED";

	PendingIntent pi;
	private FusedLocationProviderClient fusedLocationClient;

	private final BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_LOCATION_UPDATED.equals(intent.getAction())) {
                Log.d(TAG, "Received location update broadcast. Redrawing clock.");
                updateClockDisplay();
            }
        }
    };

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
		setContentView(R.layout.activity);

		Log.d(TAG, "onCreate: Checking location permissions.");
		checkLocationPermissionAndProceed();
	}

	private void checkLocationPermissionAndProceed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions not granted. Requesting...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "Permissions already granted. Proceeding with app logic.");
            proceedWithAppLogic();
        }
    }

	private void proceedWithAppLogic() {
		Log.d(TAG, "proceedWithAppLogic started.");
		PrefsReader pr = new PrefsReader(this);

		boolean shouldRequestLocation = pr.isRefreshLatLon() || pr.isFirstrun();
		Log.d(TAG, "isRefreshLatLon=" + pr.isRefreshLatLon() + ", isFirstrun=" + pr.isFirstrun() + ", shouldRequestLocation=" + shouldRequestLocation);

		if (shouldRequestLocation) {
			requestLocationUpdate(pr);
		}

		if (pr.isFirstrun()) {
			Log.d(TAG, "First run setup: setting alerts and marking first run as false.");
			setAlerts(this);
			pr.setFirstrun(false);
		}
		if (pr.isFirstnewversion()) {
			Log.d(TAG, "New version setup: setting alarms and showing welcome dialog.");
			setAlarms(this);
			//noinspection deprecation
			showDialog(DIALOG_WELCOME);
			pr.setFirstnewversion(false);
		}
	}

	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Permission GRANTED.");
                proceedWithAppLogic();
            } else {
                Log.w(TAG, "onRequestPermissionsResult: Permission DENIED.");
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_LONG).show();
				proceedWithAppLogic();
            }
        }
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
					//noinspection deprecation
					dismissDialog(DIALOG_WELCOME);
				}
			});
			return builder.create();
		}
		}
		//noinspection deprecation
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
		Log.d(TAG, "requestLocationUpdate called.");
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Log.w(TAG, "requestLocationUpdate: Permissions not granted. Bailing out.");
			return;
		}

		fusedLocationClient.getLastLocation()
			.addOnSuccessListener(this, new OnSuccessListener<Location>() {
				@Override
				public void onSuccess(Location location) {
					if (location != null) {
						Log.i(TAG, "Acquired location: " + location);
						prefs.storeLatLon((float) location.getLatitude(), (float) location.getLongitude());
                        updateClockDisplay(); // Redraw the clock with the new location
					} else {
						Log.w(TAG, "fusedLocationClient.getLastLocation() was successful, but returned a null location.");
					}
				}
			})
			.addOnFailureListener(this, new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					Log.e(TAG, "fusedLocationClient.getLastLocation() failed.", e);
				}
			});
	}

    public static void updateLocationData(Context context, final PrefsReader prefs) {
        FusedLocationProviderClient staticFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted. Cannot update location from static method.");
            return;
        }

        staticFusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        prefs.storeLatLon((float) location.getLatitude(), (float) location.getLongitude());
                        Log.d(TAG, "Location updated from static method: " + location.getLatitude() + ", " + location.getLongitude());
						// Broadcast that location has changed so active UI can update
						context.sendBroadcast(new Intent(ACTION_LOCATION_UPDATED));
                    } else {
                        Log.w(TAG, "Last known location is null from static method.");
                    }
                })
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.e(TAG, "staticFusedLocationClient.getLastLocation() failed.", e);
					}
				});
    }

	private void updateClockDisplay() {
        final ImageView iv = (ImageView) findViewById(R.id.clock);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.loading);
        if (iv == null || pb == null) return; // Guard against views not being ready

        iv.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Updating clock display.");
                PrefsReader pr = new PrefsReader(AstroClock.this);
                final double latitude = pr.getLatitude();
                final double longitude = pr.getLongitude();
                final int theme = pr.getTheme();
                pb.setVisibility(View.VISIBLE);
                iv.setImageBitmap(null);
                iv.setImageBitmap(new BitmapMaker(AstroClock.this, 800, latitude, longitude, theme).makeBitmap());
                pb.setVisibility(View.GONE);
            }
        });
    }

	@Override
	protected void onResume() {
		super.onResume();

		//registerReceiver(locationUpdateReceiver, new IntentFilter(ACTION_LOCATION_UPDATED), RECEIVER_NOT_EXPORTED);

		PrefsReader pr = new PrefsReader(this);
		if (pr.isUseBackground()) {
			getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), AstroClock.loadFullImage(this, Uri.parse(pr.getBackgroundImage()))));
		} else {
			getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
		}

		updateClockDisplay();

		Intent pintent = new Intent(this, AstroClock.class);
		pintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pi = PendingIntent.getActivity(this, 0, pintent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);

	}

	@Override
	protected void onPause() {
		super.onPause();
		//unregisterReceiver(locationUpdateReceiver);

		getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
		ImageView iv = (ImageView) findViewById(R.id.clock);
		iv.setImageBitmap(null); // Setting to null is better than setImageDrawable(null) for Bitmaps
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.cancel(pi);
	}

	public void clicked(View arg0) {
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		return super.onCreateOptionsMenu(menu);
//		MenuInflater mi = new MenuInflater(this);
//		mi.inflate(R.menu.menu, menu);
//		String[] titles = getResources().getStringArray(R.array.settingsoptions);
//		menu.findItem(R.id.Theme).setTitle(titles[0]).setIntent(new Intent(AstroClock.this, Theme.class));
//		menu.findItem(R.id.Alerts).setTitle(titles[1]).setIntent(new Intent(AstroClock.this, Alerts.class));
//		menu.findItem(R.id.Location).setTitle(titles[2]).setIntent(new Intent(AstroClock.this, Map.class));
//		menu.findItem(R.id.About).setTitle(titles[3]).setIntent(new Intent(AstroClock.this, About.class));
//		return super.onCreateOptionsMenu(menu);
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

}
