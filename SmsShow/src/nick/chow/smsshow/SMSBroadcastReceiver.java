package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 
 * @author zhouyun
 *
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
	private final String tag = SMSBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent ) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (prefs.getBoolean(Constants.ENABLE_QSMS, true)) {
			saveShortMessage(context, intent.getExtras());
			abortBroadcast();
			Log.i(tag, "abort broadcast");
			Intent service = new Intent(context, QuickSMSService.class);
			context.startService(service);
		}
	}
	
	protected void saveShortMessage(Context context, Bundle bundle) {  
		Log.i(tag, "Receive a new message");
		Object[] pdus = (Object[]) bundle.get("pdus");
		StringBuilder detail = new StringBuilder();
		String sender = null;
		long time = 0l;
		for (Object pdu : pdus) {
			SmsMessage message  = SmsMessage.createFromPdu((byte[])pdu);
			detail.append(message.getDisplayMessageBody());
			sender = message.getOriginatingAddress();
			time = message.getTimestampMillis();
		}
		Log.i(tag, "SMSBroadcastReceiver : message is " + detail);
		
	    ContentValues values = new ContentValues();
	    values.put("address", sender);
	    values.put("date", time);
	    values.put("read", 0); // 1 read 0 not read
	    values.put("status", -1); // must be ?
	    values.put("type", 1);
	    values.put("body", detail.toString());
	    context.getContentResolver().insert(Uri.parse(Constants.SMS_URI),  values);
	}  
	
}
