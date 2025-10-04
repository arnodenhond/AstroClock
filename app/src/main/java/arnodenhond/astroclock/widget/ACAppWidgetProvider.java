package arnodenhond.astroclock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.R;


public class ACAppWidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		PrefsReader settings = new PrefsReader(context);
		if (settings.isRefreshLatLon()) {
			AstroClock.updateLocationData(context,settings);
		}
		if (settings.isFirstnewversion()) {
			AstroClock.setAlarms(context);
			settings.setFirstnewversion(false);
		}
		if (settings.isFirstrun()) {
			AstroClock.setAlerts(context);
			AstroClock.updateLocationData(context,settings);
		}
		

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
		Bitmap bitmap = new BitmapMaker(context, 800, settings.getLatitude(), settings.getLongitude(), settings.getTheme()).makeBitmap();
		if (!AstroClock.supportsAPILevel11()) {
			bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, true);
		}
		views.setImageViewBitmap(R.id.clock, bitmap);
		Intent menuintent = new Intent(context, Menu.class);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(context, 0, menuintent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE));
		for (int i = 0; i < appWidgetIds.length; i++)
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);

	}

}
