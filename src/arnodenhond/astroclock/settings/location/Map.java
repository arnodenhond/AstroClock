package arnodenhond.astroclock.settings.location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.RemoteViews;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.alerts.AlarmReceiver;
import arnodenhond.astroclock.alerts.BootReceiver;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.widget.ACAppWidgetProvider;
import arnodenhond.astroclocklite.R;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class Map extends MapActivity implements AdListener {

	GoogleAnalyticsTracker tracker;

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
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/Map");

		setContentView(R.layout.map);
		setupAd();
		mv = (MapView) findViewById(R.id.mapview);
		mv.setBuiltInZoomControls(true);
		FrameLayout.LayoutParams zoomParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		zoomParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		mv.getZoomButtonsController().getZoomControls().setLayoutParams(zoomParams);
		GeoPoint gp = new PrefsReader(this).getGeoPoint();
		overlay = new Overlay(getResources().getDrawable(R.drawable.mappin), gp);
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
		adrequest.setKeywords(new HashSet<String>(Arrays.asList(prefs.getKeywords().split(","))));
		adView.setAdListener(this);
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
			overlay = new Overlay(getResources().getDrawable(R.drawable.mappin), gp);
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
		PrefsReader settings = new PrefsReader(this);
		settings.setGeoPoint(overlay.getItem(0).getPoint());

		removeAlarms();
		sendBroadcast(new Intent(this, BootReceiver.class));

		AppWidgetManager awm = AppWidgetManager.getInstance(this);

		RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);

		int height = getResources().getDisplayMetrics().heightPixels;
		int width = getResources().getDisplayMetrics().widthPixels;
		BitmapMaker bmmaker = new BitmapMaker(this, 500, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
		views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		Intent menuintent = new Intent(this, Menu.class);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(this, 0, menuintent, Intent.FLAG_ACTIVITY_NEW_TASK));
		awm.updateAppWidget(new ComponentName(this, ACAppWidgetProvider.class), views);

		super.onPause();
	}

	private void removeAlarms() {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		Context context = this;

		Intent pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_MIDDAY);
		PendingIntent operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_MIDNIGHT);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_SUNRISE);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_SUNSET);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_FULLMOON);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_NEWMOON);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_FIRSTQUARTER);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_LASTQUARTER);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_NORTHERNSOLSTICE);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_SOUTHERNSOLSTICE);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_NORTHWARDEQUINOX);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

		pintent = new Intent(context, AlarmReceiver.class);
		pintent.setAction(PrefsReader.KEY_ALERT_SOUTHWARDEQUINOX);
		operation = PendingIntent.getBroadcast(context, 0, pintent, 0);
		am.cancel(operation);

	}

	@Override
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		tracker.trackEvent("noad", "noad", "noad", 3);
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
	}

	@Override
	public void onPresentScreen(Ad arg0) {
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		tracker.trackEvent("ad", "ad", "ad", 3);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}
