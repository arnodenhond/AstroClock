package arnodenhond.astroclock.settings.themes;

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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclock.widget.ACAppWidgetProvider;
import arnodenhond.astroclocklite.R;

import com.google.ads.AdView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class Theme extends Activity {

	GoogleAnalyticsTracker tracker;
	public static final int DIALOG_NODOWNLOAD = 1;
	AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-5436860-15", 20, this);
		tracker.trackPageView("/Theme");
		setContentView(R.layout.themeselector);
		adView = AstroClock.setupAd(this);
		final TextView textview = (TextView) findViewById(R.id.TextView);
		textview.setText(getResources().getStringArray(R.array.artists)[0]);
		final Button settheme = (Button) findViewById(R.id.settheme);
		final Gallery gallery = (Gallery) findViewById(R.id.Gallery);
		gallery.setAdapter(new ImageAdapter(this));
		final PrefsReader pr = new PrefsReader(this);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id) {
				textview.setText(getResources().getStringArray(R.array.artists)[pos]);
				PrefsReader pr = new PrefsReader(Theme.this);
				if (pos == pr.getTheme()) {
					settheme.setText(R.string.themestored);
					settheme.setEnabled(false);
				} else {
					settheme.setText(R.string.settheme);
					settheme.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		gallery.setSelection(pr.getTheme());
		settheme.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View progresslayout = findViewById(R.id.downloadprogresslayout);
				int pos = gallery.getSelectedItemPosition();
				if (pos != 2) {
					new DownloadTask(progresslayout, findViewById(R.id.selector), settheme, pos, Theme.this).execute();
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
					Toast.makeText(Theme.this, R.string.themestored, Toast.LENGTH_SHORT).show();
					settheme.setText(R.string.themestored);
					settheme.setEnabled(false);
				}
			}
		});
		final Button selectImage = (Button) findViewById(R.id.selectbackgroundbutton);
		CheckBox useBackground = (CheckBox) findViewById(R.id.usebackgroundsetting);
		useBackground.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				pr.setUseBackground(isChecked);
				selectImage.setEnabled(isChecked);
				if (isChecked) {
					getWindow().setBackgroundDrawable(new BitmapDrawable(AstroClock.loadFullImage(Theme.this, Uri.parse(pr.getBackgroundImage()))));
				} else {
					getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
				}
			}
		});
		useBackground.setChecked(pr.isUseBackground());
		selectImage.setEnabled(pr.isUseBackground());
		if (pr.isUseBackground()) {
			getWindow().setBackgroundDrawable(new BitmapDrawable(AstroClock.loadFullImage(Theme.this, Uri.parse(pr.getBackgroundImage()))));
		} else {
			getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
		}
		selectImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent pickImage = new Intent(Intent.ACTION_PICK);
				pickImage.setType("image/*");
				startActivityForResult(pickImage, 0);
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode!=Activity.RESULT_OK)
			return;
		Uri uri = data.getData();
		new PrefsReader(this).setBackgroundImage(uri.toString());
		getWindow().setBackgroundDrawable(new BitmapDrawable(AstroClock.loadFullImage(this, uri)));
		super.onActivityResult(requestCode, resultCode, data);
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
	
	@Override
	public void onPause() {
		PrefsReader settings = new PrefsReader(this);
		getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_dark);
		AppWidgetManager awm = AppWidgetManager.getInstance(this);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget);
		Bitmap bitmap = new BitmapMaker(this, 480, settings.getLatitude(), settings.getLongitude(), settings.getTheme()).makeBitmap();
		if (!AstroClock.supportsAPILevel11()) {
			bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, true);
		}
		views.setImageViewBitmap(R.id.clock, bitmap);
		Intent menuintent = new Intent(this, Menu.class);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		views.setOnClickPendingIntent(R.id.clock, PendingIntent.getActivity(this, 0, menuintent, PendingIntent.FLAG_UPDATE_CURRENT));
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
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
		adView.destroy();
	}
}
