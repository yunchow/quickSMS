package nick.chow.smsshow;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class QuickSMSService extends Service implements Runnable {
	private final String tag = QuickSMSService.class.getSimpleName();
	private Intent intent;

	@Override
	public void onStart(Intent intent, int startId) {
		this.intent = intent;
		new Thread(this).start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
		Bundle extras = intent.getExtras();
		String sms = extras.getString("sms");
		String sender = extras.getString("sender");
		String time = extras.getString("sendTime");
		
		Log.i(tag, "QuickSMSService sleep 500 ms start");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i(tag, "QuickSMSService sleep wake up");
		
		Intent aIntent = new Intent(getApplicationContext(), SMSPopupActivity.class);
		aIntent.putExtra("sms", sms);
		aIntent.putExtra("sender", sender);
		aIntent.putExtra("sendTime", time);
		aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(aIntent);
		Log.i(tag, "QuickSMSService start a new service");
		Log.i(tag, "QuickSMSService sms = " + sms);
	}

}
