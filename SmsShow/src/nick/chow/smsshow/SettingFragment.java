package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * @author zhouyun
 *
 */
public class SettingFragment extends PreferenceFragment implements OnPreferenceChangeListener {
	private PreferenceScreen preferenceScreen;
	private int prferenceCount;
	private Preference enableQSMSPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
		preferenceScreen = getPreferenceScreen();
		prferenceCount = preferenceScreen.getPreferenceCount();
		enableQSMSPreference = findPreference(Constants.ENABLE_QSMS);
		enableQSMSPreference.setOnPreferenceChangeListener(this);
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (newValue instanceof Boolean) {
			boolean enable = (Boolean) newValue;
			for (int i = 1; i < prferenceCount; i++) {
				Preference pref = preferenceScreen.getPreference(i);
				pref.setEnabled(enable);
			}
		}
		return true;
	}
	
}
