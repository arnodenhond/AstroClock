package arnodenhond.astroclock;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AstroClockAppWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_appwidget_provider);

    	
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double latitude=0.0d;
		double longitude=0.0d;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
		SharedPreferences prefs = context.getSharedPreferences("latlon", Activity.MODE_PRIVATE);
		if (latitude==0.0d && longitude==0.0d) {
			latitude = prefs.getFloat("latitude", 0.01f);
			longitude = prefs.getFloat("longitude", 0.01f);
			BitmapMaker bmmaker = new BitmapMaker(context, 146,latitude,longitude); 
	    	views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		} else {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putFloat("latitude", (float) latitude);
			edit.putFloat("longitude", (float) longitude);
			edit.commit();
			BitmapMaker bmmaker = new BitmapMaker(context, 146,latitude,longitude); 
	    	views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		}
    	
        views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(context, 0, new Intent("AstroClockWidgetStats"), Intent.FLAG_ACTIVITY_NEW_TASK));
        for (int i = 0; i < appWidgetIds.length; i++)
        	appWidgetManager.updateAppWidget(appWidgetIds[i], views);

    }
    
}

