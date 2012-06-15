package arnodenhond.astroclock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ACAppWidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", context);
		tracker.trackEvent("Widget", "Update", "Receiver", 0);
		tracker.trackPageView("/WidgetUpdateReceiver");
		tracker.dispatch();

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
		PrefsReader settings = new PrefsReader(context);
//		int height = context.getResources().getDisplayMetrics().heightPixels;
//		int width = context.getResources().getDisplayMetrics().widthPixels;
		BitmapMaker bmmaker = new BitmapMaker(context, 480, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
		views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		Intent menuintent = new Intent(context, Menu.class);
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(context, 0, menuintent, PendingIntent.FLAG_CANCEL_CURRENT));
		for (int i = 0; i < appWidgetIds.length; i++)
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);

	}

}
