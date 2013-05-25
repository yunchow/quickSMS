package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import nick.chow.app.context.Tools;
import nick.chow.app.manager.SMSManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author zhouyun
 *
 */
public class QuickSMSService extends IntentService implements SensorEventListener {
	private static final String TAG = "QuickSMSService";
	private SharedPreferences prefs;
	private Vibrator vibrator;
	private NotificationManager notificationManager;
	private SensorManager sensorManager;
	private Sensor lightSensor;
	private boolean isTest;
	
	public QuickSMSService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		isTest = intent.getBooleanExtra(Constants.IS_TEST, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		}
		
		if (!prefs.getBoolean(Constants.ENABLE_QSMS, true)) {
			return;
		}
		
		setupNotification(intent);
		setupSMSContentObserver();
		setupRemider();
		openMainWindow();
		
		if (lightSensor != null) {
			sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
		}
	}

	public void setupSMSContentObserver() {
		getContentResolver().registerContentObserver(Uri.parse(Constants.SMS_URI), true, contentObserver);
	}
	
	private ContentObserver contentObserver = new ContentObserver(null) {
		
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			notificationManager.cancel(Constants.NOTIFY_NO_NEW_SMS);
			getContentResolver().unregisterContentObserver(contentObserver);
		};
		
	};
	
	public void openMainWindow() {
		Intent aIntent = new Intent(this, SMSPopupActivity.class);
		aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		aIntent.putExtra(Constants.IS_TEST, isTest);
		startActivity(aIntent);
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
		Log.i(TAG, "QuickSMSService start a new service");
	}
	
	@SuppressWarnings("deprecation")
	public void setupNotification(Intent intent) {
		String detail = isTest ? getString(R.string.testContent) : intent.getStringExtra(Constants.NEW_MSG_CONTENT);
		
		Notification notification = new Notification();
		notification.icon = R.drawable.state_notify_msg_orange_original;
		//notification.defaults = Notification.FLAG_SHOW_LIGHTS;
		notification.when = System.currentTimeMillis();
		notification.ledARGB = 0x00FF00;
		notification.ledOnMS = 100;
		notification.ledOffMS = 100;
		notification.tickerText = detail;
		
		int count = SMSManager.getManager(this).countUnread();
		String contentTitle = count + getString(R.string.notifyNewTitle);
		String contentText = detail;
		
		Intent aIntent = new Intent(this, SMSPopupActivity.class);
		aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		aIntent.putExtra(Constants.IS_TEST, isTest);
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		float maximumRange = event.sensor.getMaximumRange();
		float currentValue = event.values[0];
		if (maximumRange / currentValue >= 1) {
			openMainWindow();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}
