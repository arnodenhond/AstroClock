package arnodenhond.astroclock.imwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MapView extends View {

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bd = (BitmapDrawable) getResources().getDrawable(R.drawable.map);
		paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setAntiAlias(true);
	}

	Button button;

	public void setButton(Button button) {
		this.button = button;
	}

	BitmapDrawable bd;
	Paint paint;
	public int x = -1;
	public int y = -1;

	@Override
	protected void onDraw(Canvas canvas) {
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, 240, 240, paint);
		paint.setARGB(192, 0, 0, 0);
		canvas.drawBitmap(bd.getBitmap(), 0, 0, paint);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(3f);
		
		if (x!=-1) {
			canvas.drawLine((float)x, 0, (float)x, 240, paint);
		}
		if (y!=-1) {
			canvas.drawLine(0,(float)y, 240, (float)y, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = (int) event.getX();
		y = (int) event.getY();
		if (y<0)
			return true;
		button.setText("Set "+makeLatLon());
		invalidate();
		return true;
	}

	private String makeLatLon() {
		int lat = getLat();
		int lon = getLon();
		return Math.abs(lat) + (lat < 0 ? "N" : "S") + ", " + Math.abs(lon) + (lon > 0 ? "E" : "W");
	}

	public int getLat() {
		//-75 .. 60 = 135
		int total = getHeight();
		int range = 180;
		int mod = -90;
		
		float f = ((float)y / (float)total);
		float flat = f*(float)range;
		int lat = (int) flat;
		lat += mod;
		return lat;
	}

	public int getLon() {
		float f = (float) x / 239f;
		float flon = f * 360;
		int lon  = (int) flon;
		lon -= 180;
		return lon;
	}

}