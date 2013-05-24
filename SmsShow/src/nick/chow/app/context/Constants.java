package nick.chow.app.context;

/**
 * @author zhouyun
 * 
 */
public interface Constants {

	Boolean RELEASE = true;
	
	String IS_TEST = "istest";
	String ENABLE_QSMS = "enableQSMS";
	String ENABLE_START_ANIMATION = "enableStartAnimation";
	String ENABLE_STOP_ANIMATION = "enableStopAnimation";
	
	int NOTIFY_NO_NEW_SMS = 998;
	int NOTIFY_NO_SEND_FAIL = 999;
	
	String NEW_MSG_CONTENT = "newMsgContent";
	String SMS_INBOX_URI = "content://sms/inbox";
	String ENABLE_PRIVATE_SMS = "enablePrivateSMS";
	
	String START_ANIMATION_TYPE_VALUE = "startAnimationTypeValue";
	String STOP_ANIMATION_TYPE_VALUE = "stopAnimationTypeValue";
	
	String ENABLE_VIBRATE = "enableVibrate";
	String ENABLE_VOICE = "enableVoice";
	String SMS_RINGTONE = "smsringtone";
	String ENABLE_REMINDER = "enableReminder";

	String DISPLAY_CLOSE_BTN = "displayCloseBtn";
	String DISPLAY_READ_BTN = "displayReadBtn";
	String DISPLAY_DELETE_BTN = "displayDeleteBtn";
	
	String FEEDBACK_FROM = "from";
	String FEEDBACK_CONTENT = "content";
	
	String EXCEPTION = "exception";
	
}
