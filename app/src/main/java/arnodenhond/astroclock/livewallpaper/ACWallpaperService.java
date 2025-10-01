package arnodenhond.astroclock.livewallpaper;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import arnodenhond.astroclock.AstroClock;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.settings.Menu;
import arnodenhond.astroclock.settings.PrefsReader;

public class ACWallpaperService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
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
		return new ACEngine();
	}

	private class ACEngine extends Engine {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};
		private Paint paint = new Paint();
		private int width;
		private int height;
		private boolean visible = true;

		public ACEngine() {
			handler.post(drawRunner);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
			if (WallpaperManager.COMMAND_TAP.equals(action)) {
				Intent menuintent = new Intent(ACWallpaperService.this, Menu.class);
				menuintent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				menuintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(menuintent);
			}
			return super.onCommand(action, x, y, z, extras, resultRequested);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			this.width = width;
			this.height = height;
			super.onSurfaceChanged(holder, format, width, height);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();

				if (canvas != null) {
					PrefsReader settings = new PrefsReader(ACWallpaperService.this);
					BitmapMaker bmmaker = new BitmapMaker(ACWallpaperService.this, 480, settings.getLatitude(), settings.getLongitude(), settings.getTheme());
					if (settings.isUseBackground()) {
						Bitmap bmp = AstroClock.loadFullImage(ACWallpaperService.this, Uri.parse(settings.getBackgroundImage()));
						if (bmp!=null) {
							canvas.drawBitmap(Bitmap.createScaledBitmap(bmp, width, height, true), 0,0, paint);
							bmp.recycle();
						}
					} else {
						paint.setARGB(255, 0, 0, 0);
						canvas.drawRect(0, 0, width, height, paint);
					}
					if (width < height) {
						// portrait
						int diff = height - width;
						canvas.drawBitmap(Bitmap.createScaledBitmap(bmmaker.makeBitmap(), width, width, true), 0, diff / 2, paint);
					} else {
						// landscape
						int diff = width - height;
						canvas.drawBitmap(Bitmap.createScaledBitmap(bmmaker.makeBitmap(), height, height, true), diff / 2, 0, paint);
					}
				}
			} finally {
				try {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				} catch (IllegalArgumentException iae) {
					Log.d("AstroClock livewallpaper", iae.toString());
				}
			}
			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 5000);
			}
		}
	}
}
