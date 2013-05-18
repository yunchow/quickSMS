package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import nick.chow.app.context.MenuItemSelector;
import nick.chow.app.context.OnPreferenceChangeListenerDecrator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author zhouyun
 * 
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {
	private PreferenceScreen preferenceScreen;
	private int prferenceCount;
	private Preference enableQSMSPreference;
	private Preference startAnimationPreference;
	private Preference stopAnimationPreference;
	private Preference startAnimationPreferenceValue;
	private Preference stopAnimationPreferenceValue;
	private Preference ringtongPreference;
	private Preference vibratePreference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFragmentSupport()) {
			setContentView(R.layout.settings);
			initActionBar();
		} else {
			addPreferencesFromResource(R.xml.pref_general);
			preferenceScreen = getPreferenceScreen();
			prferenceCount = preferenceScreen.getPreferenceCount();
			enableQSMSPreference = findPreference(Constants.ENABLE_QSMS);
			enableQSMSPreference.setOnPreferenceChangeListener(this);
			startAnimationPreference = findPreference(Constants.ENABLE_START_ANIMATION);
			stopAnimationPreference = findPreference(Constants.ENABLE_STOP_ANIMATION);
			startAnimationPreferenceValue = findPreference(Constants.START_ANIMATION_TYPE_VALUE);
			stopAnimationPreferenceValue = findPreference(Constants.STOP_ANIMATION_TYPE_VALUE);
			ringtongPreference = findPreference(Constants.SMS_RINGTONE);
			vibratePreference = findPreference(Constants.ENABLE_VIBRATE);
			startAnimationPreference.setOnPreferenceChangeListener(this);
			stopAnimationPreference.setOnPreferenceChangeListener(this);
			vibratePreference.setOnPreferenceChangeListener(this);
			initPreferenceState();
			OnPreferenceChangeListenerDecrator.bindPreferenceSummary(startAnimationPreferenceValue);
			OnPreferenceChangeListenerDecrator.bindPreferenceSummary(stopAnimationPreferenceValue);
			OnPreferenceChangeListenerDecrator.bindPreferenceSummary(ringtongPreference);
		}
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void initActionBar() {
		getActionBar().show();
	}
	
	protected boolean isFragmentSupport() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
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
		} else if (vibratePreference == preference && enabled) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(300);
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		new MenuItemSelector(this).onItemSelect(item);
		return super.onMenuItemSelected(featureId, item);
	}
	
}
