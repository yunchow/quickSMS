package nick.chow.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
public class SMSService {
	public static final Uri SMS_PROVIDER_URI = Uri.parse("content://sms/");
	private final String tag = getClass().getSimpleName();
	private Context context;
	
	private ContactBookService contactService;
	
	protected SMSService(Context context) {
		this.context = context;
		contactService = ContactBookService.createService(context);
	}
	
	/**
	 * @param unreadSMSIds
	 * @return
	 */
	public List<Map<String, String>> queryUnReadSMS(Set<String> unreadSMSIds) {
		Cursor allUnReadSMS = queryAllUnReadSMS();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		try {
			while (allUnReadSMS != null && allUnReadSMS.moveToNext()) {
				Log.i(tag, "current cursor : " + allUnReadSMS);
				unreadSMSIds.add(allUnReadSMS.getString(0));
				Map<String, String> each = new HashMap<String, String>();
				each.put("_id", allUnReadSMS.getString(0));
				each.put("body", allUnReadSMS.getString(3));
				
				String addressId = allUnReadSMS.getString(1);
				String sender = contactService.getNameByNumber(addressId);
				long date = allUnReadSMS.getLong(2);
				String time = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(date);
				each.put("note", context.getString(R.string.from) + sender + context.getString(R.string.at) + time);
				data.add(each);
			}
		} finally {
			if (allUnReadSMS != null && !allUnReadSMS.isClosed()) {
				allUnReadSMS.close();
			}
		}
		return data;
	}

	/**
	 * @param unreadSMSIds  sms id
	 */
	public void markSMSReadFor(Set<String> unreadSMSIds) {
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
		
		int upated = context.getContentResolver().update(SMS_PROVIDER_URI, cv, "_id in " + inClause, new String[]{});
		Log.i(tag, "upated = " + upated);
	}
	
	/**
	 * @return Cursor
	 */
	public Cursor queryAllUnReadSMS() {
		String[] projection = new String[]{"_id", "address", "date_sent", "body"};
		return context.getContentResolver().query(SMS_PROVIDER_URI, projection, "read=?", new String[]{"0"}, null);
	}
	
	public static SMSService createService(Context context) {
		return new SMSService(context);
	}
}
