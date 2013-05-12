package nick.chow.smsshow;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nick.chow.app.context.AnimationDecrator;
import nick.chow.app.context.Constants;
import nick.chow.app.context.MenuItemSelector;
import nick.chow.app.manager.SMSManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
	NotificationManager notificationManager;
	private Set<String> mids = new HashSet<String>();
	private SharedPreferences prefs;
	
	private Button closeBtn;
	private Button deleBtn;
	private Button readBtn;
	List<Map<String, String>> data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		istest = getIntent().getBooleanExtra(Constants.IS_TEST, false);
		smsListView = (ListView) findViewById(R.id.smsListView);
		titleView = (TextView) findViewById(R.id.title);
		smsContainer = findViewById(R.id.smsContainer);
		smsDiver = (TextView) findViewById(R.id.smsDivider);
		btnBar = (ViewGroup) findViewById(R.id.btnBar);
		closeBtn = (Button) findViewById(R.id.close);
		deleBtn = (Button) findViewById(R.id.deleteAll);
		readBtn = (Button) findViewById(R.id.markRead);
		
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		this.setupButton();
		this.setupAnimation();
	}
	
	public void setupAnimation() {
		if (prefs.getBoolean(Constants.ENABLE_START_ANIMATION, true)) {
			// start animation
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump_in);
			smsContainer.startAnimation(animation);
		}
	}
	
	public void setupButton() {
		boolean display = false;
		if (prefs.getBoolean(Constants.DISPLAY_CLOSE_BTN, true)) {
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
	
	@Override
	protected void onResume() {
		super.onResume();
		setupWindow();
		setupData();
		setupTitle();
	    setupListView();
	}

	/**
	 * when click close button, this method triggered
	 * @param view
	 */
	public void close(View view) {
		if (prefs.getBoolean(Constants.ENABLE_STOP_ANIMATION, true)) {
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump_out);
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
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump_out);
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
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump_out);
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
