package arnodenhond.astroclock;

import java.util.Calendar;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TabHost;
import arnodenhond.astroclock.calcuator.SunTimes;
import arnodenhond.astroclock.calcuator.SunTimesException;
import arnodenhond.astroclock.calcuator.Time;
import arnodenhond.astroclock.settings.PrefsReader;
import arnodenhond.astroclocklite.R;

@SuppressWarnings("deprecation")
public class Help extends TabActivity {

    TabHost mTabHost;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("AboutApp").setContent(R.id.aboutapp).setIndicator("Help"));
        mTabHost.addTab(mTabHost.newTabSpec("AboutDev").setContent(R.id.aboutdev).setIndicator("Made-by"));
        
        calcDayNightLength();
        

//		ScrollView sv = new ScrollView(this);
//		TextView tv = new TextView(this);
//		tv.setText(R.string.helptext);
//		tv.setPadding(10, 15, 15, 15);
//		sv.addView(tv);
//		setContentView(sv);
//		tv.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(Help.this, AstroClock.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
//			}
//		});
	}
	
	private void calcDayNightLength() {
	
		PrefsReader pr = new PrefsReader(this);
		double latitude = pr.getLatitude();
		double longitude = pr.getLongitude();

		Calendar c = Calendar.getInstance();
		double up = 0;
		double down = 0;
		try {
			up = SunTimes.getSunriseTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
			down = SunTimes.getSunsetTimeUTC(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), longitude, latitude, SunTimes.ZENITH).getFractionalHours();
		} catch (SunTimesException e) {
		}
		Time uptime = new Time(down - up);
		Time downtime = new Time((24 - down) + up);
		
		ProgressBar pb = (ProgressBar) findViewById(R.id.daynightlength);
		int max = ((uptime.getHour()*60)+uptime.getMinute())+((downtime.getHour()*60)+downtime.getMinute());
		int progress = ((uptime.getHour()*60)+uptime.getMinute());
		pb.setMax(max);
		pb.setProgress(progress);
		
	}
}
