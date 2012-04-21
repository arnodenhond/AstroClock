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
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class Map extends MapActivity {


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		MapView mv = (MapView) findViewById(R.id.mapview);
		mv.setBuiltInZoomControls(true);

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
	
	public void acquireLocation(View v) {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(new Criteria(),true);
		if (providers.isEmpty()) {
			Toast.makeText(this, "Location provider not found or enabled.\nUse Location Settings to enable.", Toast.LENGTH_SHORT).show();
		} else {
			locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
		}
	}
	
}
