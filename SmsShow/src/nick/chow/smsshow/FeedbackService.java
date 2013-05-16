package nick.chow.smsshow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import nick.chow.app.context.Constants;
import nick.chow.app.context.Mail;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author zhouyun
 *
 */
public class FeedbackService extends IntentService {
	private static String TAG = "FeedbackService";
	private boolean sent = false;
	private int errorCount = 0;

	public FeedbackService() {
		super("FeedbackService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        while (!sent) {
        	try {
        		sent = doFeedback(intent);
        		errorCount = 0;
        		Log.i(TAG, "send feedback email scuessfully");
    		} catch (Exception e) {
    			doException(e);
    		}
        }
	}

	private void doException(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Log.e(TAG, sw.toString());
		Log.i(TAG, "errorCount = " + errorCount);
		try {
			TimeUnit.SECONDS.sleep(2 * (++errorCount));
		} catch (InterruptedException e1) {
			
		}
	}
	
	private boolean doFeedback(Intent intent) throws Exception {
		Mail m = new Mail("yunzhounj@gmail.com", "googlePassw0rd");
        String[] toArr = {"yunchow@qq.com"};
    	m.set_to(toArr); 
        m.set_from("nick@chow.com"); 
        m.set_subject(getString(R.string.subject)); 
        String from = intent.getStringExtra(Constants.FEEDBACK_FROM);
        String body = intent.getStringExtra(Constants.FEEDBACK_CONTENT);
        if (from.length() > 0) {
        	body += "\r\n" + getString(R.string.from) + from;
        }
        if (errorCount > 0) {
        	body += "\r\n errorCount = " + errorCount;
        }
        body += "\r\n " + buildDeviceInfo();
        m.setBody(body);
        return m.send();
	}
	
	private String buildDeviceInfo() {
		String model = Build.MODEL;
		String manufacture = Build.MANUFACTURER;
		String product = Build.PRODUCT;
		String brand = Build.BRAND;
		String releaseVersion = Build.VERSION.RELEASE;
		
		StringBuilder sb = new StringBuilder();
		sb.append(" model = " + model).append("\n");
		sb.append(" manufacture = " + manufacture).append("\n");
		sb.append(" product = " + product).append("\n");
		sb.append(" brand = " + brand).append("\n");
		sb.append(" releaseVersion = " + releaseVersion).append("\n");
		
		try {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String line1Number = telephonyManager.getLine1Number();
			sb.append(" line1Number = " + line1Number).append("\n");
		} catch (Exception e) {
			
		}
		
		return sb.toString();
	}

}
