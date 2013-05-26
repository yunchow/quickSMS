package nick.chow.app.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import nick.chow.app.context.Constants;
import nick.chow.smsshow.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author zhouyun
 *
 */
public class SMSManager {
	public static final Uri SMS_PROVIDER_URI = Uri.parse(Constants.SMS_URI);
	private final String tag = getClass().getSimpleName();
	private Context context;
	
	private ContactBookManager contactService;
	
	protected SMSManager(Context context) {
		this.context = context;
		contactService = ContactBookManager.getManager(context);
	}
	
	/**
	 * @param smsIds
	 * @return
	 */
	public List<Map<String, String>> querySMSDetail(Set<String> smsIds, boolean notAll) {
		Cursor cursor = queryAllUnReadSMS();
		if (cursor == null || cursor.getCount() <= 0) {
			cursor = queryNewSMS();
		}
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				Log.i(tag, "current cursor : " + cursor);
				smsIds.add(cursor.getString(0));
				Map<String, String> each = new HashMap<String, String>();
				each.put("_id", cursor.getString(0));
				String body = cursor.getString(3);
				String addressId = cursor.getString(1);
				String sender = contactService.getNameByNumber(addressId);
				long date = cursor.getLong(2);
				String time = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(date);
				each.put("_body", body);
				int len = body.length();
				if (notAll) {
					if (len >= 10) {
						int end = len / 2 >= 50 ? 50 : len / 2;
						body = body.substring(0, end) + ".....";
					}
				}
				each.put("body", "["+ time +"]" + body);
				each.put("number", addressId);
				each.put("sender", sender);
				String name = sender;
				if (name == null || "".equals(name)) {
					name = addressId;
				}
				each.put("name", name);
				each.put("time", time);
				data.add(each);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return data;
	}
	
	/**
	 * @return
	 */
	public List<Map<String, String>> buildTestData() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int i = 0 ; i < 2; i++) {
			Map<String, String> each = new HashMap<String, String>();
			each.put("_id", "-1");
			each.put("number", "10086");
			each.put("sender", "Q¶ÌÐÅ");
			each.put("name", "Q¶ÌÐÅ");
			each.put("_body", context.getString(R.string.testContent));
			String time = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(System.currentTimeMillis());
			each.put("body", "[" + time + "]" + context.getString(R.string.testContent));
			each.put("time", time);
			data.add(each);
		}
		return data;
	}

	/**
	 * @param unreadSMSIds  sms id
	 */
	public void markSMSReadFor(Set<String> mids) {
		if (mids.isEmpty()) {
			Log.i(tag, "no need to upate");
			return;
		}
		StringBuilder inClause = new StringBuilder("(");
		for (String id : mids) {
			inClause.append(id).append(",");
		}
		inClause.deleteCharAt(inClause.length() - 1);
		inClause.append(")");
		Log.i(tag, "inClause = " + inClause);
		
		ContentValues cv = new ContentValues();
		cv.put("read", "1");
		
		int upated = context.getContentResolver().update(SMS_PROVIDER_URI, cv, "_id in " + inClause, new String[]{});
		Log.i(tag, "upated = " + upated);
	}
	
	/**
	 * @param mids
	 */
	public boolean deleteSMS(Set<String> mids) {
		if (mids.isEmpty()) {
			Log.i(tag, "no need to upate");
			return false;
		}
		return deleteSMS(mids.toArray(new String[mids.size()]));
	}
	
	/**
	 * @param mids
	 */
	public boolean deleteSMS(String... mids) {
		if (mids == null || mids.length == 0) {
			Log.i(tag, "no need to upate");
			return false;
		}
		StringBuilder inClause = new StringBuilder("(");
		for (String id : mids) {
			inClause.append(id).append(",");
		}
		inClause.deleteCharAt(inClause.length() - 1);
		inClause.append(")");
		Log.i(tag, "inClause = " + inClause);
		String where = "_id in " + inClause;
		
		int upated = context.getContentResolver().delete(SMS_PROVIDER_URI, where, new String[]{});
		Log.i(tag, "upated = " + upated);
		return upated > 0;
	}
	
	/**
	 * @return Cursor
	 */
	public Cursor queryAllUnReadSMS() {
		String[] projection = new String[]{"_id", "address", "date", "body"};
		return context.getContentResolver().query(SMS_PROVIDER_URI, projection, "read=?", new String[]{"0"}, null);
	}
	
	public Cursor queryNewSMS() {
		String[] projection = new String[]{"_id", "address", "date", "body"};
		return context.getContentResolver().query(SMS_PROVIDER_URI, projection, null, new String[]{}, "date desc limit 1");
	}
	
	/**
	 * @return
	 */
	public int countUnread() {
		int count = 0;
		Cursor cursor = queryAllUnReadSMS();
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
			cursor = null;
		}
		return count;
	}
	
	public static SMSManager getManager(Context context) {
		return new SMSManager(context);
	}
}
