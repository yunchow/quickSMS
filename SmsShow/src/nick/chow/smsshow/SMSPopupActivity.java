package nick.chow.smsshow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 
 * @author zhouyun
 *
 */
public class SMSPopupActivity extends Activity {
	private final String tag = SMSPopupActivity.class.getName();
	
	private ListView smsListView;
	
	private Set<String> unreadSMSIds = new HashSet<String>();
	
	public static final Uri SMS_PROVIDER_URI = Uri.parse("content://sms/");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.i(tag, "###### SMSPopupActivity ######");
		setContentView(R.layout.activity_main);
		smsListView = (ListView) findViewById(R.id.smsListView);
	}
	
	public void markSMSRead() {
		if (unreadSMSIds.isEmpty()) {
			Log.i(tag, "no need to upate");
			return;
		}
		StringBuilder inClause = new StringBuilder("(");
		for (String id : unreadSMSIds) {
			inClause.append(id).append(",");
		}
		inClause.deleteCharAt(inClause.length() - 1);
		inClause.append(")");
		Log.i(tag, "inClause = " + inClause);
		
		ContentValues cv = new ContentValues();
		cv.put("read", "1");
		
		int upated = getContentResolver().update(SMS_PROVIDER_URI, cv, "_id in " + inClause, new String[]{});
		Log.i(tag, "upated = " + upated);
	}
	
	/**
	 * @return Cursor
	 */
	public Cursor queryAllUnReadSMS() {
		String[] projection = new String[]{"_id", "address", "date_sent", "body"};
		return getContentResolver().query(SMS_PROVIDER_URI, projection, "read=?", new String[]{"0"}, null);
	}
	
	/**
	 * query user name from contact book by number
	 * @param number
	 * @return
	 */
	public String getNameByNumber(String number) {
		Log.i(tag, "getNameByNumber number = " + number);
		if (number == null) {
			return "N/A";
		}
		if (number.indexOf("+86") != -1) {
			number = number.substring(number.indexOf("+86"));
		}
		Log.i(tag, "phone number is " + number);
		
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		Cursor cursor = this.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				projection, // Which columns to return.
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number
						+ "'", // WHERE clause.
				null, // WHERE clause value substitution
				null); // Sort order.
		
		Log.i(tag, "cursor = " + cursor);
		if (cursor != null && cursor.moveToFirst()) {
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME); 
			String nme = cursor.getString(nameFieldColumnIndex);
			Log.i(tag, "found! contact name = " + nme);
			return nme;
		}
		Log.i(tag, "no found name by " + number);
		return number;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(tag, "############# SMSPopupActivity onStart #######");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(tag, "##### SMSPopupActivity onResume ########");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
	    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); 
	    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	    
	    //Toast.makeText(getApplicationContext(), "开始查询", Toast.LENGTH_SHORT).show();
		Cursor allUnReadSMS = queryAllUnReadSMS();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		//Toast.makeText(getApplicationContext(), "查询结束，总条数：" + allUnReadSMS.getCount(), Toast.LENGTH_SHORT).show();
		
		if (allUnReadSMS != null) {
			while (allUnReadSMS.moveToNext()) {
				Log.i(tag, "current cursor : " + allUnReadSMS);
				unreadSMSIds.add(allUnReadSMS.getString(0));
				Map<String, String> each = new HashMap<String, String>();
				each.put("_id", allUnReadSMS.getString(0));
				each.put("body", allUnReadSMS.getString(3));
				
				String addressId = allUnReadSMS.getString(1);
				String sender = getNameByNumber(addressId);
				long date = allUnReadSMS.getLong(2);
				String time = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(date);
				each.put("note", getString(R.string.from) + sender + getString(R.string.at) + time);
				data.add(each);
			}
			allUnReadSMS.close();
		}

		SimpleAdapter cursorAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.sms_item_list,
				new String[]{"body", "note"}, new int[]{R.id.smsDetail, R.id.note});
		int layoutHeight = smsListView.getLayoutParams().height;
		int disHeight = smsListView.getHeight();
		//Toast.makeText(getApplicationContext(), "layoutHeight = " + layoutHeight + ", smsListView.getHeight() = "
		//+ smsListView.getHeight(), Toast.LENGTH_SHORT).show();
		
		if (disHeight == 0 && data.size() >= 3 && layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
			smsListView.getLayoutParams().height = 300;
		}
		if (disHeight >= 300 && layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
			smsListView.getLayoutParams().height = disHeight;
		}
		smsListView.setAdapter(cursorAdapter);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(tag, "####### SMSPopupActivity onPause ######");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(tag, "##### SMSPopupActivity onStop ####");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(tag, "####### SMSPopupActivity onDestroy #####");
	}
	
	public void close(View view) {
		markSMSRead();
		finish();
		Log.i(tag, "close MainActivity");
	}
	
	public void replySMS(View view) {
		Log.i(tag, "reply sms");
		Toast.makeText(getApplicationContext(), "reply sms", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
