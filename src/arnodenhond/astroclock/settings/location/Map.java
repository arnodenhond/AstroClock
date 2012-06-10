package arnodenhond.astroclock.settings.location;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.widget.WidgetProvider;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class Map extends MapActivity {

	private static final int DIALOG_LOCATION = 1;
	Overlay overlay;
	MapView mv;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		setupAd();
		mv = (MapView) findViewById(R.id.mapview);
		mv.setBuiltInZoomControls(true);
		FrameLayout.LayoutParams zoomParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		zoomParams.gravity=Gravity.BOTTOM|Gravity.RIGHT;
		mv.getZoomButtonsController().getZoomControls().setLayoutParams(zoomParams);
		GeoPoint gp = new PrefsReader(this).getGeoPoint();
		overlay = new Overlay(getResources().getDrawable(R.drawable.icon), gp);
		mv.getController().setCenter(gp);
		mv.getOverlays().add(overlay);
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
		adrequest.addTestDevice("10007c61aeb3");
		adView.loadAd(adrequest);
	}

	public void acquireLocation(View v) {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(new Criteria(), true);
		if (providers.isEmpty()) {
			showDialog(DIALOG_LOCATION);
		} else {
			Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
			if (location == null) {
				showDialog(DIALOG_LOCATION);
				return;
			}
			int lat = (int) (location.getLatitude() * 1E6);
			int lon = (int) (location.getLongitude() * 1E6);
			GeoPoint gp = new GeoPoint(lat, lon);

			mv.getOverlays().remove(overlay);
			overlay = new Overlay(getResources().getDrawable(R.drawable.icon), gp);
			mv.getOverlays().add(overlay);
			mv.refreshDrawableState();
			mv.invalidate();

			new PrefsReader(this).setGeoPoint(gp);
			mv.getController().setCenter(gp);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LOCATION: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.nolocationtitle);
			builder.setMessage(R.string.nolocationbody);
			builder.setNeutralButton(R.string.nolocationbutton, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					dismissDialog(DIALOG_LOCATION);
				}
			});
			return builder.create();
		}
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onPause() {
		new PrefsReader(this).setGeoPoint(overlay.getItem(0).getPoint());

		AppWidgetManager awm = AppWidgetManager.getInstance(this);

		RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
		PrefsReader settings = new PrefsReader(this);
		int height = getResources().getDisplayMetrics().heightPixels;
		int width = getResources().getDisplayMetrics().widthPixels;
		BitmapMaker bmmaker = new BitmapMaker(this, 500, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
		views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		Intent menuintent = new Intent(this, Menu.class);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(this, 0, menuintent, Intent.FLAG_ACTIVITY_NEW_TASK));
		awm.updateAppWidget(new ComponentName(this, WidgetProvider.class), views);

		super.onPause();
	}

}
