package nick.chow.smsshow;

/**
 * comment for compatibility android 2.1
 */
import nick.chow.app.context.Constants;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * @author zhouyun
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingFragment extends PreferenceFragment implements OnPreferenceChangeListener {
	private PreferenceScreen preferenceScreen;
	private int prferenceCount;
	private Preference enableQSMSPreference;
	private Preference startAnimationPreference;
	private Preference stopAnimationPreference;
	private Preference startAnimationPreferenceValue;
	private Preference stopAnimationPreferenceValue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
		PreferenceManager.setDefaultValues(this.getActivity(), R.xml.pref_general, false);
		preferenceScreen = getPreferenceScreen();
		prferenceCount = preferenceScreen.getPreferenceCount();
		enableQSMSPreference = findPreference(Constants.ENABLE_QSMS);
		enableQSMSPreference.setOnPreferenceChangeListener(this);
		startAnimationPreference = findPreference(Constants.ENABLE_START_ANIMATION);
		stopAnimationPreference = findPreference(Constants.ENABLE_STOP_ANIMATION);
		startAnimationPreferenceValue = findPreference(Constants.START_ANIMATION_TYPE_VALUE);
		stopAnimationPreferenceValue = findPreference(Constants.STOP_ANIMATION_TYPE_VALUE);
		startAnimationPreference.setOnPreferenceChangeListener(this);
		stopAnimationPreference.setOnPreferenceChangeListener(this);
		initPreferenceState();
		bindPreferenceSummaryToValue(startAnimationPreferenceValue);
		bindPreferenceSummaryToValue(stopAnimationPreferenceValue);
	}
	
	private void initPreferenceState() {
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		boolean enabled = prefs.getBoolean(Constants.ENABLE_QSMS, true);
		for (int i = 1; i < prferenceCount; i++) {
			Preference pref = preferenceScreen.getPreference(i);
			pref.setEnabled(enabled);
		}
		if (enabled) {
			enabled = prefs.getBoolean(Constants.ENABLE_START_ANIMATION, true);
			startAnimationPreferenceValue.setEnabled(enabled);
			enabled = prefs.getBoolean(Constants.ENABLE_STOP_ANIMATION, true);
			stopAnimationPreferenceValue.setEnabled(enabled);
		}
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (!(newValue instanceof Boolean)) {
			return true;
		}
		boolean enabled = (Boolean) newValue;
		if (enableQSMSPreference == preference) {
			for (int i = 1; i < prferenceCount; i++) {
				Preference pref = preferenceScreen.getPreference(i);
				pref.setEnabled(enabled);
			}
		} else if (startAnimationPreference == preference) {
			startAnimationPreferenceValue.setEnabled(enabled);
		} else if (stopAnimationPreference == preference) {
			stopAnimationPreferenceValue.setEnabled(enabled);
		}
		return true;
	}
	
	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference, PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(), ""));
	}
	
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();
			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index]: null);

			}
			return true;
		}
	};
}
