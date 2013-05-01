package nick.chow.smsshow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
		Log.i(tag, "Receive a new message");
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");
		StringBuilder detail = new StringBuilder();
		String sender = null;
		long time = 0l;
		DateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
		for (Object pdu : pdus) {
			SmsMessage message  = SmsMessage.createFromPdu((byte[])pdu);
			detail.append(message.getDisplayMessageBody());
			sender = message.getOriginatingAddress();
			time = message.getTimestampMillis();
		}
		Log.i(tag, "SMSBroadcastReceiver : message is " + detail);
		/*Intent aIntent = new Intent(context, SMSPopupActivity.class);
		aIntent.putExtra("sms", detail.toString());
		aIntent.putExtra("sender", sender);
		aIntent.putExtra("sendTime", sdf.format(time));
		aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(aIntent);*/
		
		Intent service = new Intent(context, QuickSMSService.class);
		service.putExtra("sms", detail.toString());
		service.putExtra("sender", sender);
		service.putExtra("sendTime", sdf.format(time));
		context.startService(service);
	}
	
}
