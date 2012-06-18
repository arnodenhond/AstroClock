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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

public class DownloadTask extends AsyncTask<Void, Integer, Void> {

	private View progresslayout;
	private View selector;
	private int theme;
	private boolean fail = false;
	private Context context;
	private ProgressBar pb;
	private Button button;

	public DownloadTask(View progresslayout, View selector, Button button, int theme, Context context) {
		this.progresslayout = progresslayout;
		this.selector = selector;
		this.theme = theme;
		this.context = context;
		this.button = button;
		this.pb = (ProgressBar) progresslayout.findViewById(R.id.downloadprogress);
		pb.setProgress(0);
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
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnectedOrConnecting())
				throw new IOException("no connection");
			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "cover.png").openConnection();
			context.deleteFile("background.png");
			context.deleteFile("cover.png");
			context.deleteFile("sunset.png");
			context.deleteFile("sunrise.png");
			context.deleteFile("year.png");
			context.deleteFile("moon.png");
			context.deleteFile("day.png");
			saveToFile(readResponse(huc.getInputStream()), "cover.png");
			publishProgress(1);

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "day.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "day.png");
			publishProgress(2);

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "moon.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "moon.png");
			publishProgress(3);

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "year.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "year.png");
			publishProgress(4);

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "sunrise.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "sunrise.png");
			publishProgress(5);

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "sunset.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "sunset.png");
			publishProgress(6);

			huc = (HttpURLConnection) new URL("http://android.arnodenhond.com/apps/astroclock/skins/t" + (theme + 1) + "background.png").openConnection();
			saveToFile(readResponse(huc.getInputStream()), "background.png");
			publishProgress(7);

		} catch (IOException e) {
			Log.d("AstroClock", e.toString());
			fail = true;
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Integer integer = values[0];
		pb.setProgress(integer.intValue());
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		progresslayout.setVisibility(View.GONE);
		selector.setVisibility(View.VISIBLE);
		if (fail) {
			((Activity) context).showDialog(Theme.DIALOG_NODOWNLOAD);
		} else {
			button.setText(R.string.themestored);
			button.setEnabled(false);
			new PrefsReader(context).setTheme(this.theme);
			Toast.makeText(context, R.string.themestored, Toast.LENGTH_SHORT).show();
		}
		context = null;
		super.onPostExecute(result);
	}

	private void saveToFile(byte[] baf, String name) throws IOException {
		FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
		fos.write(baf);
		fos.flush();
		fos.close();
		baf=null;
	}

	private byte[] readResponse(InputStream inputStream) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(inputStream,8192);
		ByteArrayBuffer baf = new ByteArrayBuffer(8192);
		int current = 0;
		byte[] buf = new byte[8192];
		while ((current = bis.read(buf)) != -1) {
			baf.append(buf,0,current);
		}
		bis.close();
		return baf.toByteArray();
	}

}
