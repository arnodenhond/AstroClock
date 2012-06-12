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

public class ACAppWidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
		PrefsReader settings = new PrefsReader(context);
		int height = context.getResources().getDisplayMetrics().heightPixels;
		int width = context.getResources().getDisplayMetrics().widthPixels;
		BitmapMaker bmmaker = new BitmapMaker(context, 500, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
		views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		Intent menuintent = new Intent(context, Menu.class);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(context, 0, menuintent, PendingIntent.FLAG_CANCEL_CURRENT));
		for (int i = 0; i < appWidgetIds.length; i++)
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);

	}

}
