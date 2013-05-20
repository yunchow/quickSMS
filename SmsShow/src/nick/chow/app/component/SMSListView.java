package nick.chow.app.component;

import java.util.List;
import java.util.Map;

import nick.chow.app.manager.SMSManager;
import nick.chow.smsshow.R;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

/**
 * @author zhouyun
 *
 */
public class SMSListView extends ListView implements AdapterView.OnItemClickListener, 
		AdapterView.OnItemLongClickListener, DialogInterface.OnClickListener {
	private static String TAG = "SMSListView";
	private AlertDialog quickDialog;
	private AlertDialog holoDialog;
	private AlertDialog replyDialog;
	private String sender;
	private String number;
	private String _id;
	private String _body;
	private EditText editor;
	
	public SMSListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG, "SMSListView(Context context, AttributeSet attrs)");
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
		number = item.get("number");
		sender = item.get("sender");
		_body = item.get("_body");
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
		String content = editor.getText().toString();
		Toast.makeText(getContext(), "content = " + content, Toast.LENGTH_SHORT).show();
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
		SmsManager smsManager = SmsManager.getDefault();  
		List<String> divideContents = smsManager.divideMessage(content);    
		for (String text : divideContents) {    
		    smsManager.sendTextMessage(number, null, text, null, null);    
		}
		//Toast.makeText(getContext(), getResources().getString(R.string.replyScuess), Toast.LENGTH_SHORT).show();
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
			SMSManager.getManager(getContext()).deleteSMS(_id);
			Toast.makeText(getContext(), getResources().getString(R.string.deletescuess), Toast.LENGTH_SHORT).show();
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
		}
	}
	
}
