package nick.chow.smsshow;

import nick.chow.app.context.Tools;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class QuickSMSService extends IntentService {
	private static final String tag = QuickSMSService.class.getSimpleName();
	
	public QuickSMSService() {
		super(tag);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(tag, "QuickSMSService sleep 500 ms start");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			Tools.show(this, e);
		}
		Log.i(tag, "QuickSMSService sleep wake up");
		
		Intent aIntent = new Intent(this, SMSPopupActivity.class);
		aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(aIntent);
		Log.i(tag, "QuickSMSService start a new service");
	}

}
