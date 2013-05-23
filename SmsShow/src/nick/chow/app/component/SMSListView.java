package nick.chow.app.component;

import java.util.List;
import java.util.Map;

import nick.chow.app.context.Constants;
import nick.chow.app.manager.SMSManager;
import nick.chow.smsshow.R;
import nick.chow.smsshow.SMSPopupActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author zhouyun
 *
 */
public class SMSListView extends ListView implements AdapterView.OnItemClickListener, 
		AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener {
	private static String TAG = "SMSListView";
	private static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private NotificationManager notificationManager;
	private Activity activity;
	private AlertDialog quickDialog;
	private AlertDialog holoDialog;
	private AlertDialog replyDialog;
	private String sender;
	private String number;
	private String _id;
	private String _body;
	private String time;
	private EditText editor;
	private String content;
	
	public SMSListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG, "SMSListView(Context context, AttributeSet attrs)");
		notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		quickDialog = createDialog(position, R.array.quickDialogMenu);
		quickDialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		holoDialog = createDialog(position, R.array.holoDialogMenu);;
		holoDialog.show();
		return true;
	}
	
	private AlertDialog createDialog(int position, int res) {
		@SuppressWarnings("unchecked")
		Map<String, String> item = (Map<String, String>) getItemAtPosition(position);
		_id = item.get("_id");
		number = item.get("number");
		sender = item.get("sender");
		_body = item.get("_body");
		time = item.get("time");
		AlertDialog.Builder builder = createDiallogBuilder();
		String title = number;
		if (sender != null && !"".equals(sender)) {
			title = sender + "("+ number +")";
		}
		builder.setTitle(title);
		builder.setItems(getResources().getStringArray(res), this);
		builder.setNegativeButton(R.string.cancelBtn, null);
		return builder.create();
	}
	
	private AlertDialog.Builder createDiallogBuilder() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			return createDiallogBuilderHolo();
		}
		return new Builder(getContext()); 
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private AlertDialog.Builder createDiallogBuilderHolo() {
		return new Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT); 
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == quickDialog) {
			onQuickMenuClick(which);
		} else if (dialog == holoDialog) {
			onHoloMenuClick(which);
		} else if (dialog == replyDialog) {
			doReplyMessage();
		}
	}
	
	private void doReplyMessage() {
		editor = (EditText) replyDialog.findViewById(R.id.replyContent);
		content = editor.getText().toString();
		if (content == null || content.length() < 1) {
			AlertDialog.Builder builder = createDiallogBuilder();;
			builder.setTitle(R.string.commTitile);
			builder.setMessage(getResources().getString(R.string.contentShortAlert));
			builder.setPositiveButton(getResources().getString(R.string.known), null);
			builder.show();
		} else {
			doReplyDetail(content);
		}
	}
	
	private void doReplyDetail(String content) {
		SMSReceiver sr = new SMSReceiver();
		getContext().registerReceiver(sr, new IntentFilter(SENT_SMS_ACTION));
		SmsManager smsManager = SmsManager.getDefault();  
		List<String> divideContents = smsManager.divideMessage(content);    
		Intent send = new Intent(SENT_SMS_ACTION);
		PendingIntent sendPendingIntent = PendingIntent.getBroadcast(getContext(), 0, send, 0);
		for (String text : divideContents) {    
		    smsManager.sendTextMessage(number, null, text, sendPendingIntent, null);    
		}
	}
	
	/**
	 * @author zhouyun
	 *
	 */
	public class SMSReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(getContext(),
						getResources().getString(R.string.replyScuess), Toast.LENGTH_SHORT).show();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				notifyFail(getResources().getString(R.string.replyFail));
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				notifyFail(getResources().getString(R.string.noSignal));
				break;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void notifyFail(String errorDetail) {
		NotificationManager notificationManager = (NotificationManager) getContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.defaults = Notification.DEFAULT_ALL;
		notification.when = System.currentTimeMillis();
		notification.tickerText = errorDetail + " " + content;
		notification.icon = R.drawable.indicator_input_error;
		
		Uri uri = Uri.parse("smsto:" + number);            
		Intent sms = new Intent(Intent.ACTION_SENDTO, uri);            
		sms.putExtra("sms_body", _body);
		
		PendingIntent contentIntent = PendingIntent.getActivity(getContext(), R.string.app_name, 
				sms, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(getContext(), errorDetail, content, contentIntent);
		notificationManager.notify(Constants.NOTIFY_NO_SEND_FAIL, notification);
	}

	private void onHoloMenuClick(int which) {
		switch (which) {
		case 0:
			prepareReplyDialog();
			break;
		case 1:
			Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + number));
			getContext().startActivity(phoneIntent);
		case 2:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("vnd.android-dir/mms-sms");
			getContext().startActivity(intent);
			break;
		case 3:
			boolean updated = SMSManager.getManager(getContext()).deleteSMS(_id);
			String updatedString = updated ? getResources().getString(R.string.deletescuess)
										   : getResources().getString(R.string.deletesfail);
			Toast.makeText(getContext(), updatedString, Toast.LENGTH_SHORT).show();
			if (updated && this.getCount() > 1) {
				Intent aintent = new Intent(getContext(), SMSPopupActivity.class);
				aintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().startActivity(aintent);
			} else if (updated && this.getCount() <= 1) {
				notificationManager.cancel(Constants.NOTIFY_NO_NEW_SMS);
				getActivity().finish();
			}
			break;
		case 4:
			Uri uri = Uri.parse("smsto:");            
			Intent sms = new Intent(Intent.ACTION_SENDTO, uri);            
			sms.putExtra("sms_body", _body);            
			getContext().startActivity(sms); 
			break;
		case 5:
			addContactPeople();
			break;
		}
	}
	
	/**
	 * quick reply short message
	 */
	private void prepareReplyDialog() {
		AlertDialog.Builder builder = createDiallogBuilder();
		String title = number;
		if (sender != null && !"".equals(sender)) {
			title = sender;
		}
		builder.setTitle(getResources().getString(R.string.replyTitle) + title);
		builder.setView(LayoutInflater.from(getContext()).inflate(R.layout.reply, null));
		builder.setPositiveButton(getResources().getString(R.string.replyBtn), this);
		builder.setNegativeButton(getResources().getString(R.string.cancelBtn), null);
		replyDialog = builder.create();
		replyDialog.show();
		editor = (EditText) replyDialog.findViewById(R.id.replyContent);
		editor.setFocusable(true);
		editor.setFocusableInTouchMode(true);
		editor.requestFocus();
		editor.requestFocusFromTouch();
	}
	
	@SuppressWarnings("deprecation")
	private void addContactPeople() {
		Intent contactIntent = new Intent(android.provider.Contacts.Intents.Insert.ACTION);
		contactIntent.setType(android.provider.Contacts.People.CONTENT_TYPE);
		contactIntent.putExtra(android.provider.Contacts.Intents.Insert.PHONE, number);
		getContext().startActivity(contactIntent);
	}

	private void onQuickMenuClick(int which) {
		switch (which) {
		case 0:
			prepareReplyDialog();
			break;
		case 1:
			Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + number));
			getContext().startActivity(phoneIntent);
			break;
		case 2:
			viewDetail();
			break;
		}
	}
	
	private void viewDetail() {
		AlertDialog.Builder builder = createDiallogBuilder();;
		builder.setTitle(R.string.viewDetail);
		builder.setView(LayoutInflater.from(getContext()).inflate(R.layout.sms_detail, null));
		builder.setPositiveButton(getResources().getString(R.string.close), null);
		AlertDialog dia = builder.create();
		dia.show();
		String title = number;
		if (sender != null && !"".equals(sender)) {
			title = sender + "("+ number +")";
		}
		((TextView) dia.findViewById(R.id.detailSender)).setText(getResources().getString(R.string.detailSender) + title);
		((TextView) dia.findViewById(R.id.detailSendTime)).setText(getResources().getString(R.string.detailSendTime) + time);
		((TextView) dia.findViewById(R.id.detailContent)).setText(getResources().getString(R.string.detailContent) + _body);
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
}
