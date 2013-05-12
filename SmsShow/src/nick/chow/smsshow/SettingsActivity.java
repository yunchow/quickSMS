package nick.chow.smsshow;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author zhouyun
 * 
 */
public class SettingsActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}
}
