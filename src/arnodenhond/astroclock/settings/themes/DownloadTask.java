package arnodenhond.astroclock.settings.themes;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import arnodenhond.astroclock.settings.PrefsReader;

public class DownloadTask extends AsyncTask<Void, Void, Void> {

	private View progresslayout;
	private View selector;
	private int theme;
	private boolean fail = false;
	private Context context;

	public DownloadTask(View progresslayout, View selector, int theme, Context context) {
		this.progresslayout = progresslayout;
		this.selector = selector;
		this.theme = theme;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		progresslayout.setVisibility(View.VISIBLE);
		selector.setVisibility(View.INVISIBLE);
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			HttpURLConnection huc;

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "background.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "background.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "cover.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "cover.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "day.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "day.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "moon.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "moon.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "year.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "year.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "sunrise.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "sunrise.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "sunset.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "sunset.png");

		} catch (IOException e) {
			Log.d("AstroClock", e.toString());
			fail = true;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		progresslayout.setVisibility(View.GONE);
		selector.setVisibility(View.VISIBLE);
		if (fail) {
			((Activity) context).showDialog(Theme.DIALOG_NODOWNLOAD);
		} else {
			new PrefsReader(context).setTheme(this.theme);
			((Activity) context).finish();
		}
		super.onPostExecute(result);
	}

	private void saveToFile(byte[] baf, String name) throws IOException {
		FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
		fos.write(baf);
		fos.flush();
		fos.close();
	}

	private byte[] readResponse(InputStream inputStream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(1024);
		int current = 0;
		while ((current = bis.read()) != -1) {
			baf.append((byte) current);
		}
		return baf.toByteArray();
	}

}
