package nick.chow.app.context;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;

/**
 * @author zhouyun
 *
 */
public class OnPreferenceChangeListenerDecrator implements Preference.OnPreferenceChangeListener {
	public static OnPreferenceChangeListenerDecrator preferenceChangeListenerDecrator = new OnPreferenceChangeListenerDecrator();

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String stringValue = newValue.toString();
		if (preference instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) preference;
			int index = listPreference.findIndexOfValue(stringValue);
			preference.setSummary(index >= 0 ? listPreference.getEntries()[index]: null);
		}
		if (preference instanceof RingtonePreference) {
			preference.setSummary(stringValue);
		}
		return true;
	}
	
	public static void bindPreferenceSummary(Preference preference) {
		preference.setOnPreferenceChangeListener(preferenceChangeListenerDecrator);
		Object value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");
		preferenceChangeListenerDecrator.onPreferenceChange(preference, value);
	}

}
