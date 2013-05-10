package nick.chow.smsshow;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nick.chow.app.manager.SMSManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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
	private TextView smsCounter;
	private View smsContainer;
	
	private SMSManager smsService = SMSManager.getManager(this);
	NotificationManager notificationManager;
	private Set<String> unreadSMSIds = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.i(tag, "###### SMSPopupActivity ######");
		setContentView(R.layout.activity_main);
		smsListView = (ListView) findViewById(R.id.smsListView);
		smsCounter = (TextView) findViewById(R.id.smsCounter);
		smsContainer = findViewById(R.id.smsContainer);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(tag, "##### SMSPopupActivity onResume ########");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	    
	    List<Map<String, String>> data = smsService.queryUnReadSMS(unreadSMSIds);
		smsCounter.setText("" + data.size());

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
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump_in);
		smsContainer.setAnimation(animation);
	}

	/**
	 * when click close button, this method triggered
	 * @param view
	 */
	public void close(View view) {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump_out);
		animation.setAnimationListener(animationOut);
		smsContainer.startAnimation(animation);
		Log.i(tag, "close MainActivity");
	}
	
	public void replySMS(View view) {
		Log.i(tag, "reply sms");
		Toast.makeText(getApplicationContext(), "reply sms", Toast.LENGTH_LONG).show();
	}
	
	private AnimationListener animationOut = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
			
		}
		
		public void onAnimationRepeat(Animation animation) {
			
		}
		
		public void onAnimationEnd(Animation animation) {
			smsService.markSMSReadFor(unreadSMSIds);
			//notificationManager.cancelAll();
			finish();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
