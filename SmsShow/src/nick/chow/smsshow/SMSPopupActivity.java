package nick.chow.smsshow;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nick.chow.app.component.SMSListView;
import nick.chow.app.context.AnimationDecrator;
import nick.chow.app.context.Constants;
import nick.chow.app.context.MenuItemSelector;
import nick.chow.app.context.Tools;
import nick.chow.app.manager.SMSManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author zhouyun
 *
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class SMSPopupActivity extends Activity {
	private final String tag = SMSPopupActivity.class.getSimpleName();
	
	private SMSListView smsListView;
	private TextView titleView;
	private View smsContainer;
	private boolean istest;
	private ViewGroup btnBar;
	private TextView smsDiver;
	
	private SMSManager smsService = SMSManager.getManager(this);
	private NotificationManager notificationManager;
	private Set<String> mids = new HashSet<String>();
	private SharedPreferences prefs;
	
	//private Button closeBtn;
	private Button deleBtn;
	private Button readBtn;
	private List<Map<String, String>> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.color.main_activity_bg);
		setContentView(R.layout.activity_main);
		init();
		this.setupButton();
		this.setupAnimation();
	}
	
	/**
	 * initialize all components and data
	 */
	protected void init() {
		smsListView = (SMSListView) findViewById(R.id.smsListView);
		titleView = (TextView) findViewById(R.id.title);
		smsContainer = findViewById(R.id.smsContainer);
		smsDiver = (TextView) findViewById(R.id.smsDivider);
		btnBar = (ViewGroup) findViewById(R.id.btnBar);
		//closeBtn = (Button) findViewById(R.id.close);
		deleBtn = (Button) findViewById(R.id.deleteAll);
		readBtn = (Button) findViewById(R.id.markRead);	
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		istest = getIntent().getBooleanExtra(Constants.IS_TEST, false);
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
		/*if (prefs.getBoolean(Constants.DISPLAY_CLOSE_BTN, false)) {
			closeBtn.setVisibility(Button.VISIBLE);
			display = true;
		} else {
			closeBtn.setVisibility(Button.GONE);
		}*/
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
	}
	
	/**
	 * prepare for title rendering
	 */
	public void setupTitle() {
	    SpannableString titleCount = new SpannableString(getString(R.string.smscountleft) 
	    		+ data.size() + getString(R.string.smscountright));
	    titleCount.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, titleCount.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
	    setupTitle2(titleCount);
	    titleView.setText(getString(R.string.title));
	    titleView.append(titleCount);
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void setupTitle2(SpannableString titleCount) {
	    titleCount.setSpan(new AbsoluteSizeSpan(14, true), 0, titleCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
	
	/**
	 * prepare for list view render
	 */
	public void setupListView() {
		SimpleAdapter cursorAdapter = new SimpleAdapter(this, data, R.layout.sms_item_list,
				new String[]{"body", "name"}, new int[]{R.id.smsDetail, R.id.contactor});
		/*int layoutHeight = smsListView.getLayoutParams().height;
		int disHeight = smsListView.getHeight();
		
		if (disHeight == 0 && data.size() >= 3 && layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
			smsListView.getLayoutParams().height = 300;
		}
		if (disHeight >= 300 && layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
			smsListView.getLayoutParams().height = disHeight;
		}*/
		smsListView.setActivity(this);
		smsListView.setAdapter(cursorAdapter);
	}
	
	/**
	 * prepare SMS data for list view
	 */
	public void setupData() {
		boolean notAll = prefs.getBoolean(Constants.ENABLE_PRIVATE_SMS, false);
		data = istest ? smsService.buildTestData() : smsService.querySMSDetail(mids, notAll);
	}
	
	@Override
	protected void onResume() {
		Tools.show(this, "## onResume ##");
		super.onResume();
		setupWindow();
		setupData();
		setupTitle();
	    setupListView();
	}

	private Animation loadExitAnimation() {
		String stopAnimVal = prefs.getString(Constants.STOP_ANIMATION_TYPE_VALUE, "");
		int rid = R.anim.scale_out;
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
			notificationManager.cancel(Constants.NOTIFY_NO_NEW_SMS);
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
			notificationManager.cancel(Constants.NOTIFY_NO_NEW_SMS);
			finish();
		}	
	}
	
	public void replySMS(View view) {
		Log.i(tag, "reply sms");
		Toast.makeText(getApplicationContext(), "reply sms", Toast.LENGTH_LONG).show();
	}
	
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
			notificationManager.cancel(Constants.NOTIFY_NO_NEW_SMS);
			finish();
		}
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
