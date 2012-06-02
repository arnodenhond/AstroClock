package arnodenhond.astroclock.settings.location;

import java.util.List;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {

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
		 mv = (MapView) findViewById(R.id.mapview);
		mv.setBuiltInZoomControls(true);
		GeoPoint gp = new PrefsReader(this).getGeoPoint();
		 overlay = new Overlay(getResources().getDrawable(R.drawable.icon),gp);
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

	public void startLocationSettings(View v) {
		startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
	
	@Override
	protected void onPause() {
		new PrefsReader(this).setGeoPoint(overlay.getItem(0).getPoint());
		super.onPause();
	}
	
	public void acquireLocation(View v) {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(new Criteria(),true);
		if (providers.isEmpty()) {
			Toast.makeText(this, "Location provider not found or enabled.\nUse Location Settings to enable.", Toast.LENGTH_SHORT).show();
		} else {
			Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
			if (location==null) {
				Toast.makeText(this, "could not get location", Toast.LENGTH_SHORT).show();
				return;
			}
			int lat = (int) (location.getLatitude()*1E6);
			int lon = (int) (location.getLongitude()*1E6);
			GeoPoint gp = new GeoPoint(lat,lon);
			
			mv.getOverlays().remove(overlay);
			overlay = new Overlay(getResources().getDrawable(R.drawable.icon),gp);
			mv.getOverlays().add(overlay);
			mv.refreshDrawableState();
			mv.invalidate();
			
			new PrefsReader(this).setGeoPoint(gp);
			mv.getController().setCenter(gp);
		}
	}
	
}
