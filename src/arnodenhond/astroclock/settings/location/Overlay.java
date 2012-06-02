package arnodenhond.astroclock.settings.location;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Overlay extends ItemizedOverlay<OverlayItem> {

	public Overlay(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
		populate();
	}

	public Overlay(Drawable defaultMaker, GeoPoint geopoint) {
		super(boundCenter(defaultMaker));
		item = new OverlayItem(geopoint, "", "");
		populate();
	}

	public OverlayItem item;

	@Override
	protected OverlayItem createItem(int i) {
		return item;
	}

	@Override
	public int size() {
		return item == null ? 0 : 1;
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		item = new OverlayItem(p, "", "");
		populate();
		return super.onTap(p, mapView);
	}

}
