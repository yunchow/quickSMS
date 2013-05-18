package nick.chow.app.context;

import nick.chow.smsshow.AboutActivity;
import nick.chow.smsshow.FeedbackActivity;
import nick.chow.smsshow.QuickSMSService;
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
			Intent feedback = new Intent(context, FeedbackActivity.class);
			context.startActivity(feedback);
			break;
		case R.id.action_about:
			Intent about = new Intent(context, AboutActivity.class);
			context.startActivity(about);
			break;
		case R.id.previewSetting:
			Intent settingPreview = new Intent(context, QuickSMSService.class);
			settingPreview.putExtra(Constants.IS_TEST, true);
			context.startService(settingPreview);
			break;
		}
	}
}
