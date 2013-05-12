package nick.chow.app.context;

import nick.chow.smsshow.R;
import nick.chow.smsshow.SettingsActivity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

/**
 * @author zhouyun
 *
 */
public class MenuItemSelector {
	Context context;
	
	public MenuItemSelector(Context context) {
		this.context = context;
	}

	public void onItemSelect(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent setting = new Intent(context, SettingsActivity.class);
			context.startActivity(setting);
			break;
		case R.id.action_feedback:
			Intent feedback = new Intent(context, SettingsActivity.class);
			context.startActivity(feedback);
			break;
		case R.id.action_about:
			Intent about = new Intent(context, SettingsActivity.class);
			context.startActivity(about);
			break;
		}
	}
}
