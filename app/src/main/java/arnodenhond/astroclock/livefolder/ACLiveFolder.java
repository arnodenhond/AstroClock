package arnodenhond.astroclock.livefolder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.LiveFolders;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.R;

public class ACLiveFolder extends Activity {

	public static final String CONTENT_URI = "content://astroclock";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PrefsReader settings = new PrefsReader(this);
		if (settings.isRefreshLatLon()) {
			AstroClock.updateLocationData(this,settings); // Changed from getLocation
		}
		if (settings.isFirstnewversion()) {
			AstroClock.setAlarms(this);
			settings.setFirstnewversion(false);
		}
		if (settings.isFirstrun()) {
			AstroClock.updateLocationData(this,settings); // Changed from getLocation
			AstroClock.setAlerts(this);
		}
		final Intent intent = new Intent();

		intent.setData(Uri.parse(CONTENT_URI));
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_NAME, getResources().getStringArray(R.array.settingsoptions)[1]);
		// android 2.2 does not resize icon
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_ICON, Intent.ShortcutIconResource.fromContext(this, R.drawable.iconsmall));
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_DISPLAY_MODE, LiveFolders.DISPLAY_MODE_LIST);
		intent.putExtra(LiveFolders.EXTRA_LIVE_FOLDER_BASE_INTENT, new Intent(Intent.ACTION_VIEW, Uri.parse(CONTENT_URI)));
		setResult(RESULT_OK, intent);
		finish();
	}
}
