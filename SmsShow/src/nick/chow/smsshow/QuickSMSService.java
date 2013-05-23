package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import nick.chow.app.context.Tools;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class QuickSMSService extends IntentService {
	private static final String TAG = "QuickSMSService";
	private SharedPreferences prefs;
	private Vibrator vibrator;
	private NotificationManager notificationManager;
	
	public QuickSMSService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (prefs.getBoolean(Constants.ENABLE_QSMS, true)) {
			String detail = intent.getStringExtra(Constants.NEW_MSG_CONTENT);
			setupNotification(detail);
			setupRemider();
			Intent aIntent = new Intent(this, SMSPopupActivity.class);
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			aIntent.putExtra(Constants.IS_TEST, intent.getBooleanExtra(Constants.IS_TEST, false));
			startActivity(aIntent);
			Log.i(TAG, "QuickSMSService start a new service");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setupNotification(String detail) {
		Notification notification = new Notification();
		notification.icon = R.drawable.state_notify_msg_orange_original;
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.tickerText = detail;
		String contentTitle = getString(R.string.notifyNewTitle);
		String contentText = detail;
		Intent aIntent = new Intent(this, SMSPopupActivity.class);
		aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
		notificationManager.notify(Constants.NOTIFY_NO_NEW_SMS, notification);
	}
	
	public void setupRemider() {
		if (!prefs.getBoolean(Constants.ENABLE_REMINDER, true)) {
			return;
		}
		if (prefs.getBoolean(Constants.ENABLE_VIBRATE, true)) {
			long[] pattern = new long[]{10, 500, 400, 500, 400, 500};
			try {
				vibrator.vibrate(pattern, -1);
				Log.i(TAG, "start to vibrate");
			} catch (Exception e) {
				Log.e(TAG, Tools.parse(e));
			}
		}
		if (prefs.getBoolean(Constants.ENABLE_VOICE, true)) {
			int volume = -1;
			AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);  
	        volume = audioManager.getStreamVolume(AudioManager.STREAM_RING); 
			if (volume > 0) {
				volume = (volume + 1) / 2;
				String defaultRing = "content://settings/system/notification_sound";
				String ringtone = prefs.getString(Constants.SMS_RINGTONE, defaultRing);
				MediaPlayer player = new MediaPlayer();
				try {
					player.setVolume(volume, volume);
					player.setDataSource(this, Uri.parse(ringtone));
					player.prepare();
					player.start();
					Log.i(TAG, "start to play ringtone");
				} catch (Exception e) {
					Log.e(TAG, Tools.parse(e));
				}
			}
		}
	}

}
