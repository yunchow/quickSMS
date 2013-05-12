package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import nick.chow.app.context.MenuItemSelector;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * @author zhouyun
 *
 */
public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onTest(View view) {
		Intent intent = new Intent(getApplicationContext(), SMSPopupActivity.class);
		intent.putExtra(Constants.IS_TEST, true);
		startActivity(intent);
	}
	
	public void onSetting(View view) {
		Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		new MenuItemSelector(this).onItemSelect(item);
		return super.onMenuItemSelected(featureId, item);
	}

}
