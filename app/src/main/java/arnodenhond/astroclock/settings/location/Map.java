package arnodenhond.astroclock.settings.location;

// Android Core & Support
import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

// AndroidX
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

// Google Play Services - Location
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

// Google Play Services - Maps
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// Project specific
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.R;
import arnodenhond.astroclock.alerts.AlarmReceiver;
import arnodenhond.astroclock.alerts.BootReceiver;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.widget.ACAppWidgetProvider;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int DIALOG_LOCATION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap googleMap;
    private Marker currentMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private PrefsReader prefsReader;

    // private com.google.android.apps.analytics.GoogleAnalyticsTracker tracker; // Legacy
    // private com.google.ads.AdView adView; // Legacy - Note: ID is still in map.xml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        prefsReader = new PrefsReader(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment not found!");
            Toast.makeText(this, "Error loading map.", Toast.LENGTH_LONG).show();
            finish();
        }

        // tracker = com.google.android.apps.analytics.GoogleAnalyticsTracker.getInstance(); // Legacy
        // tracker.startNewSession("UA-xxxx-1", this); // Replace with your tracking ID
        // tracker.trackPageView("/Map"); // Legacy

        // com.google.ads.AdView adViewLegacy = (com.google.ads.AdView) findViewById(R.id.adView); // Legacy
        // if (adViewLegacy != null) {
        // com.google.ads.AdRequest adRequest = new com.google.ads.AdRequest(); // Legacy
        // adViewLegacy.loadAd(adRequest); // Legacy
        // }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng initialLatLng = new LatLng(prefsReader.getLatitude(), prefsReader.getLongitude());
        updateMapLocation(initialLatLng, true, 10f); // Zoom level 10f

        // Optional: Add map click listener if needed
        // this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
        // @Override
        // public void onMapClick(LatLng latLng) {
        // updateMapLocation(latLng, false, googleMap.getCameraPosition().zoom);
        // prefsReader.storeLatLon((float) latLng.latitude, (float) latLng.longitude);
        // }
        // });
    }

    private void updateMapLocation(LatLng latLng, boolean moveCamera, float zoomLevel) {
        if (googleMap == null) return;

        if (currentMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mappin));
            currentMarker = googleMap.addMarker(markerOptions);
        } else {
            currentMarker.setPosition(latLng);
        }

        if (moveCamera) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public void acquireLocation(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            updateMapLocation(newLatLng, true, googleMap != null ? googleMap.getCameraPosition().zoom : 10f);
                            prefsReader.storeLatLon((float) location.getLatitude(), (float) location.getLongitude());
                        } else {
                            Log.w(TAG, "Last known location is null.");
                            //noinspection deprecation
                            showDialog(DIALOG_LOCATION); // Show dialog if location is null
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error getting location", e);
                    //noinspection deprecation
                    showDialog(DIALOG_LOCATION);
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                acquireLocation(null); // Call acquireLocation again if permission granted
            } else {
                Toast.makeText(this, R.string.nolocationbody, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        if (item.getItemId() == R.id.refreshlocation) { // Ensure it's the correct menu item
            item.setChecked(!item.isChecked());
            prefsReader.setRefreshLatLon(item.isChecked());
            if (!AstroClock.supportsAPILevel11()) { // This check might be less relevant now
                Toast.makeText(this, item.isChecked() ? R.string.on : R.string.off, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.location, menu);

        MenuItem item = menu.findItem(R.id.refreshlocation);
        item.setCheckable(true);
        item.setChecked(prefsReader.isRefreshLatLon());

        return super.onCreateOptionsMenu(menu);
    }

    @NonNull
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_LOCATION) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.nolocationtitle);
            builder.setMessage(R.string.nolocationbody);
            builder.setNeutralButton(R.string.nolocationbutton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    //noinspection deprecation
                    dismissDialog(DIALOG_LOCATION);
                }
            });
            return builder.create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentMarker != null) {
            LatLng position = currentMarker.getPosition();
            prefsReader.storeLatLon((float) position.latitude, (float) position.longitude);
        }

        removeAlarms();
        sendBroadcast(new Intent(this, BootReceiver.class));

        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
        Bitmap bitmap = new BitmapMaker(this, 480, prefsReader.getLatitude(), prefsReader.getLongitude(), prefsReader.getTheme()).makeBitmap();
        if (!AstroClock.supportsAPILevel11()) {
            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, true);
        }
        views.setImageViewBitmap(R.id.clock, bitmap);
        Intent menuintent = new Intent(this, Menu.class);
        menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, menuintent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.clock, pendingIntent);
        awm.updateAppWidget(new ComponentName(this, ACAppWidgetProvider.class), views);
    }

    private void removeAlarms() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Context context = this;

        String[] actions = {
                PrefsReader.KEY_ALERT_MIDDAY, PrefsReader.KEY_ALERT_MIDNIGHT,
                PrefsReader.KEY_ALERT_SUNRISE, PrefsReader.KEY_ALERT_SUNSET,
                PrefsReader.KEY_ALERT_FULLMOON, PrefsReader.KEY_ALERT_NEWMOON,
                PrefsReader.KEY_ALERT_FIRSTQUARTER, PrefsReader.KEY_ALERT_LASTQUARTER,
                PrefsReader.KEY_ALERT_NORTHERNSOLSTICE, PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE,
                PrefsReader.KEY_ALERT_NORTHWARDEQUINOX, PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX
        };

        for (String action : actions) {
            Intent pintent = new Intent(context, AlarmReceiver.class);
            pintent.setAction(action);
            PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
            if (operation != null) {
                am.cancel(operation);
                operation.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if (tracker != null) { // Legacy
        // tracker.stopSession(); // Legacy
        // }
        // com.google.ads.AdView adViewLegacy = (com.google.ads.AdView) findViewById(R.id.adView); // Legacy
        // if (adViewLegacy != null) { // Legacy
        // adViewLegacy.destroy(); // Legacy
        // }
    }
}
