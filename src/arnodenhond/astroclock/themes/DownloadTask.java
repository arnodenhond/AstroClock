package arnodenhond.astroclock.themes;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class DownloadTask extends AsyncTask<Void, Void, Void> {

	private static View progresslayout;
	private int theme;
	private Context context;

	public DownloadTask(View progresslayout, int theme, Context context) {
		this.progresslayout = progresslayout;
		this.theme = theme;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		progresslayout.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			HttpURLConnection huc;
			ByteArrayBuffer baf;

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "background.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "background.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "cover.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "cover.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "day.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "day.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "moon.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "moon.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "year.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "year.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "sunrise.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "sunrise.png");

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme+1) + "sunset.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "sunset.png");

		} catch (IOException e) {
			Log.d("AstroClock",e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		progresslayout.setVisibility(View.GONE);
		final SharedPreferences themepref = context.getSharedPreferences("theme", context.MODE_PRIVATE);

		SharedPreferences.Editor edit = themepref.edit();
		edit.putInt("theme", this.theme);
		edit.commit();

		super.onPostExecute(result);
	}

	private void saveToFile(ByteArrayBuffer baf, String name) throws IOException {
		FileOutputStream fos = context.openFileOutput(name, Context.MODE_WORLD_READABLE);
		fos.write(baf.toByteArray());
		fos.close();
	}

	private ByteArrayBuffer readResponse(InputStream inputStream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(1024);
		int current = 0;
		while ((current = bis.read()) != -1) {
			baf.append((byte) current);
		}
		return baf;
	}

}
