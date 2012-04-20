package arnodenhond.astroclock.livewallpaper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import arnodenhond.astroclock.BitmapMaker;
import arnodenhond.astroclock.settings.PrefsReader;

public class ACWallpaperService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new MyWallpaperEngine();
	}

	private class MyWallpaperEngine extends Engine {
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

		public MyWallpaperEngine() {
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
					BitmapMaker bmmaker = new BitmapMaker(ACWallpaperService.this, (width < height ? width : height), settings.getLatitude(), settings.getLongitude(), settings.getTheme());
					paint.setARGB(255, 0, 0, 0);
					canvas.drawRect(0, 0, width, height, paint);
					if (width < height) {
						// portrait
						int diff = height - width;
						canvas.drawBitmap(bmmaker.makeBitmap(), 0, diff / 2, paint);
					} else {
						// landscape
						int diff = width - height;
						canvas.drawBitmap(bmmaker.makeBitmap(), diff / 2, 0, paint);
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
				handler.postDelayed(drawRunner, 60000);
			}
		}
	}
}
