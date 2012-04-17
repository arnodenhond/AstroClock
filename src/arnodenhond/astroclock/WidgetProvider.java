package arnodenhond.astroclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

public class WidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_appwidget_provider);
		PrefsReader settings = new PrefsReader(context);
		BitmapMaker bmmaker = new BitmapMaker(context, 146, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
		views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(context, 0, new Intent("AstroClockWidgetStats"), Intent.FLAG_ACTIVITY_NEW_TASK));
		for (int i = 0; i < appWidgetIds.length; i++)
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);

	}

}
