package arnodenhond.astroclock.imwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
	}

	public void startTheme(View v) {
		startActivity(new Intent(Menu.this,Theme.class));
	}
	public void startMap(View v) {
		startActivity(new Intent(Menu.this,Map.class));
	}
	public void startStats(View v) {
		startActivity(new Intent(Menu.this,Stats.class));
	}
	public void startHelp(View v) {
		startActivity(new Intent(Menu.this,Help.class));
	}
	
	public void startAlarms(View v) {
		startActivity(new Intent(Menu.this,Alarms.class));
	}
	
}
