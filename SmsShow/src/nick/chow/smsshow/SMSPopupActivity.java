package nick.chow.smsshow;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nick.chow.app.context.AnimationDecrator;
import nick.chow.app.context.Constants;
import nick.chow.app.context.MenuItemSelector;
import nick.chow.app.context.Tools;
import nick.chow.app.manager.SMSManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author zhouyun
 *
 */
public class SMSPopupActivity extends Activity {
	private final String tag = SMSPopupActivity.class.getSimpleName();
	
	private ListView smsListView;
	private TextView titleView;
	private View smsContainer;
	private boolean istest;
	private ViewGroup btnBar;
	private TextView smsDiver;
	
	private SMSManager smsService = SMSManager.getManager(this);
	//private NotificationManager notificationManager;
	private Vibrator vibrator;
	private Set<String> mids = new HashSet<String>();
	private SharedPreferences prefs;
	
	private Button closeBtn;
	private Button deleBtn;
	private Button readBtn;
	private List<Map<String, String>> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		this.setupButton();
		this.setupAnimation();
	}
	
	/**
	 * initialize all components and data
	 */
	protected void init() {
		smsListView = (ListView) findViewById(R.id.smsListView);
		titleView = (TextView) findViewById(R.id.title);
		smsContainer = findViewById(R.id.smsContainer);
		smsDiver = (TextView) findViewById(R.id.smsDivider);
		btnBar = (ViewGroup) findViewById(R.id.btnBar);
		closeBtn = (Button) findViewById(R.id.close);
		deleBtn = (Button) findViewById(R.id.deleteAll);
		readBtn = (Button) findViewById(R.id.markRead);	
		//notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		istest = getIntent().getBooleanExtra(Constants.IS_TEST, false);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	/**
	 * set animation for startup if need
	 */
	public void setupAnimation() {
		if (prefs.getBoolean(Constants.ENABLE_START_ANIMATION, true)) {
			// start animation
			int rid = R.anim.fade_in;
			String startAnimVal = prefs.getString(Constants.START_ANIMATION_TYPE_VALUE, "");
			if ("fadeIn".equals(startAnimVal)) {
				rid = R.anim.fade_in;
			} else if ("scaleIn".equals(startAnimVal)) {
				rid = R.anim.scale_in;
			} else if ("scaleRotateIn".equals(startAnimVal)) {
				rid = R.anim.hyperspace_jump_in;
			}
			Animation animation = AnimationUtils.loadAnimation(this, rid);
			smsContainer.startAnimation(animation);
		}
	}
	
	public void setupButton() {
		boolean display = false;
		if (prefs.getBoolean(Constants.DISPLAY_CLOSE_BTN, false)) {
			closeBtn.setVisibility(Button.VISIBLE);
			display = true;
		} else {
			closeBtn.setVisibility(Button.GONE);
		}
		if (prefs.getBoolean(Constants.DISPLAY_DELETE_BTN, true)) {
			deleBtn.setVisibility(Button.VISIBLE);
			display = true;
		} else {
			deleBtn.setVisibility(Button.GONE);
		}
		if (prefs.getBoolean(Constants.DISPLAY_READ_BTN, true)) {
			readBtn.setVisibility(Button.VISIBLE);
			display = true;
		} else {
			readBtn.setVisibility(Button.GONE);
		}
		if (!display) {
			smsDiver.setVisibility(View.GONE);
			btnBar.setVisibility(View.GONE);
		}
	}
	
	public void setupWindow() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	/**
	 * prepare for title rendering
	 */
	public void setupTitle() {
	    SpannableString titleCount = new SpannableString(getString(R.string.smscountleft) 
	    		+ data.size() + getString(R.string.smscountright));
	    titleCount.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, titleCount.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
	    titleCount.setSpan(new AbsoluteSizeSpan(14, true), 0, titleCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    titleView.setText(getString(R.string.title));
	    titleView.append(titleCount);
	}
	
	/**
	 * prepare for list view render
	 */
	public void setupListView() {
		SimpleAdapter cursorAdapter = new SimpleAdapter(this, data, R.layout.sms_item_list,
				new String[]{"body", "note"}, new int[]{R.id.smsDetail, R.id.note});
		int layoutHeight = smsListView.getLayoutParams().height;
		int disHeight = smsListView.getHeight();
		
		if (disHeight == 0 && data.size() >= 3 && layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
			smsListView.getLayoutParams().height = 300;
		}
		if (disHeight >= 300 && layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
			smsListView.getLayoutParams().height = disHeight;
		}
		smsListView.setAdapter(cursorAdapter);
	}
	
	/**
	 * prepare SMS data for list view
	 */
	public void setupData() {
		data = istest ? smsService.buildTestData() : smsService.querySMSDetail(mids);
	}
	
	public void setupRemider() {
		if (prefs.getBoolean(Constants.ENABLE_VIBRATE, false)) {
			long[] pattern = new long[]{10, 500, 400, 500, 400, 500};
			try {
				vibrator.vibrate(pattern, -1);
			} catch (Exception e) {
				Log.e(tag, e.toString());
				Tools.show(this, e);
			}
		}
		if (prefs.getBoolean(Constants.ENABLE_VOICE, false)) {
			int volume = -1;
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);  
	        volume = audioManager.getStreamVolume(AudioManager.STREAM_RING); 
			if (volume > 0) {
				volume = (volume + 1) / 2;
				String defaultRing = "content://settings/system/notification_sound";
				String ringtone = prefs.getString(Constants.SMS_RINGTONE, defaultRing);
				MediaPlayer player = new MediaPlayer();
				try {
					player.setVolume(volume, volume);
					player.setDataSource(this, Uri.parse(ringtone));
					player.prepare();
					player.start();
				} catch (Exception e) {
					Log.e(tag, e.toString());
					Tools.show(this, e);
				}
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setupWindow();
		setupData();
		setupRemider();
		setupTitle();
	    setupListView();
	}

	private Animation loadExitAnimation() {
		String stopAnimVal = prefs.getString(Constants.STOP_ANIMATION_TYPE_VALUE, "");
		int rid = R.anim.fade_out;
		if ("fadeOut".equals(stopAnimVal)) {
			rid = R.anim.fade_out;
		} else if ("scaleOut".equals(stopAnimVal)) {
			rid = R.anim.scale_out;
		} else if ("scaleRotateOut".equals(stopAnimVal)) {
			rid = R.anim.hyperspace_jump_out;
		}
		Animation animation = AnimationUtils.loadAnimation(this, rid);
		return animation;
	}
	
	/**
	 * when click close button, this method triggered
	 * @param view
	 */
	public void close(View view) {
		if (prefs.getBoolean(Constants.ENABLE_STOP_ANIMATION, true)) {
			Animation animation = loadExitAnimation();
			animation.setAnimationListener(animationCloseOut);
			smsContainer.startAnimation(animation);
		} else {
			finish();
		}		
	}
	
	/**
	 * when click read button, this method triggered
	 * @param view
	 */
	public void markRead(View view) {
		if (prefs.getBoolean(Constants.ENABLE_STOP_ANIMATION, true)) {
			Animation animation = loadExitAnimation();
			animation.setAnimationListener(animationReadOut);
			smsContainer.startAnimation(animation);
		} else {
			smsService.markSMSReadFor(mids);
			finish();
		}
		
	}
	
	/**
	 * when click delete button, this method triggered
	 * @param view
	 */
	public void deleteAll(View view) {
		if (prefs.getBoolean(Constants.ENABLE_STOP_ANIMATION, true)) {
			Animation animation = loadExitAnimation();
			animation.setAnimationListener(animationDeleteOut);
			smsContainer.startAnimation(animation);
		} else {
			smsService.deleteSMS(mids);
			finish();
		}	
	}
	
	public void replySMS(View view) {
		Log.i(tag, "reply sms");
		Toast.makeText(getApplicationContext(), "reply sms", Toast.LENGTH_LONG).show();
	}
	
	private AnimationListener animationCloseOut = new MyAnimationDecrator();
	
	private AnimationListener animationDeleteOut = new MyAnimationDecrator() {
		
		public void onAnimationEnd(Animation animation) {
			smsService.deleteSMS(mids);
			super.onAnimationEnd(animation);
		}
	};
	
	private AnimationListener animationReadOut = new MyAnimationDecrator() {
		
		public void onAnimationEnd(Animation animation) {
			smsService.markSMSReadFor(mids);
			super.onAnimationEnd(animation);
		}
	};
	
	public class MyAnimationDecrator extends AnimationDecrator {

		@Override
		public void onAnimationEnd(Animation animation) {
			//cancelSMSNotification();
			killSMS();
			finish();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void killSMS() {
		try {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			am.restartPackage("com.android.mms");//killBackgroundProcesses("com.android.mms");
		} catch (Exception e) {
			Tools.show(this, e);
		}
	}
	
	protected void root() {
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			Tools.show(this, e);
		}
	}
	
	protected boolean hasRoot() {
		char[] arrayOfChar = new char[1024];
		try {
			int j = new InputStreamReader(Runtime.getRuntime().exec("su -c ls")
					.getErrorStream()).read(arrayOfChar);
			if (j == -1) {
				return true;
			}
		} catch (IOException e) {
			
		}
		return false;
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
