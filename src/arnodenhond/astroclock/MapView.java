package arnodenhond.astroclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import arnodenhond.astroclocklite.R;

public class MapView extends View {

	EditText latet;
	EditText lonet;
	BitmapDrawable bd;
	Paint paint;
	public int x = -1;
	public int y = -1;

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bd = (BitmapDrawable) getResources().getDrawable(R.drawable.map);
		paint = new Paint(0);
	}

	public void setFields(EditText latet, EditText lonet) {
		this.latet = latet;
		this.lonet = lonet;
		try {
			setLat(Integer.parseInt(latet.getText().toString()));
		} catch (NumberFormatException nfe) {
		}
		try {
			setLon(Integer.parseInt(lonet.getText().toString()));
		} catch (NumberFormatException nfe) {
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// paint.setColor(Color.TRANSPARENT);
		// canvas.drawRect(0, 0, 240, 120, paint);
		// paint.setARGB(192, 0, 0, 0);
		canvas.drawBitmap(bd.getBitmap(), 0, 0, paint);
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(3f);
		if (x != -1 && y != -1) {
			canvas.drawLine((float) x, 0f, (float) x, 120f, paint);
			canvas.drawLine(0f, (float) y, 240f, (float) y, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = (int) event.getX();
		y = (int) event.getY();
		if (x < 0 || x > 240)
			return true;
		if (y < 0 || y > 120)
			return true;
		latet.setText(getLat() + "");
		lonet.setText(getLon() + "");
		invalidate();
		return true;
	}

	public void setLat(int lat) {
		lat *= -1;
		lat += 90;
		float f = 120f / 180f;
		f *= lat;
		y = (int) f;
	}

	public void setLon(int lon) {
		lon += 180;
		float f = 240f / 360f;
		f *= lon;
		x = (int) f;
	}

	public int getLat() {
		float f = (float) y / 120f;
		float flat = f * 180f;
		int lat = (int) flat;
		lat -= 90;
		return lat *= -1;
	}

	public int getLon() {
		float f = (float) x / 239f;
		float flon = f * 360f;
		int lon = (int) flon;
		lon -= 180;
		return lon;
	}

}