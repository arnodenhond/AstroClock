package arnodenhond.astroclock.settings.themes;

import java.util.Arrays;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import arnodenhond.astroclock.BitmapMaker;
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

public class Theme extends Activity implements AdListener {

	GoogleAnalyticsTracker tracker;
	public static final int DIALOG_NODOWNLOAD = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/Theme");

		setContentView(R.layout.themeselector);
		setupAd();
		final TextView textview = (TextView) findViewById(R.id.TextView);
		textview.setText(getResources().getStringArray(R.array.artists)[0]);
		final Button settheme = (Button) findViewById(R.id.settheme);
		final Gallery gallery = (Gallery) findViewById(R.id.Gallery);
		gallery.setAdapter(new ImageAdapter(this));
		final PrefsReader pr = new PrefsReader(this);
		gallery.setSelection(pr.getTheme());
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				// Intent intent = new Intent(Theme.this, AstroClock.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);

			}
		});
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
				textview.setText(getResources().getStringArray(R.array.artists)[pos]);
				PrefsReader pr = new PrefsReader(Theme.this);
				if (pos == pr.getTheme()) {
					settheme.setEnabled(false);
				} else {
					settheme.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		settheme.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View progresslayout = findViewById(R.id.downloadprogresslayout);
				int pos = gallery.getSelectedItemPosition();
				if (pos != 2) {
					new DownloadTask(progresslayout, gallery, pos, Theme.this).execute();
				} else {
					progresslayout.setVisibility(View.VISIBLE);
					deleteFile("background.png");
					deleteFile("cover.png");
					deleteFile("sunset.png");
					deleteFile("sunrise.png");
					deleteFile("year.png");
					deleteFile("moon.png");
					deleteFile("day.png");
					progresslayout.setVisibility(View.GONE);
					pr.setTheme(pos);
					finish();
				}
			}
		});

	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		private Integer[] mImageIds = { R.drawable.t1preview, R.drawable.t2preview, R.drawable.t3preview, R.drawable.t4preview, R.drawable.t5preview };

		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return mImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setImageResource(mImageIds[position]);
			i.setLayoutParams(new Gallery.LayoutParams(150, 150));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(mGalleryItemBackground);
			return i;
		}
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

	@Override
	public void onPause() {
		AppWidgetManager awm = AppWidgetManager.getInstance(this);

		RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
		PrefsReader settings = new PrefsReader(this);
//		int height = getResources().getDisplayMetrics().heightPixels;
//		int width = getResources().getDisplayMetrics().widthPixels;
		BitmapMaker bmmaker = new BitmapMaker(this, 480, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
		views.setImageViewBitmap(R.id.clock, bmmaker.makeBitmap());
		Intent menuintent = new Intent(this, Menu.class);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(this, 0, menuintent, Intent.FLAG_ACTIVITY_NEW_TASK));
		awm.updateAppWidget(new ComponentName(this, ACAppWidgetProvider.class), views);

		super.onPause();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NODOWNLOAD: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.nodownloadtitle);
			builder.setMessage(R.string.nodownloadbody);
			builder.setNeutralButton(R.string.nodownloadbutton, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					dismissDialog(DIALOG_NODOWNLOAD);
				}
			});
			return builder.create();
		}
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		tracker.trackEvent("noad", "noad", "noad", 1);
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
	}

	@Override
	public void onPresentScreen(Ad arg0) {
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		tracker.trackEvent("ad", "ad", "ad", 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}
