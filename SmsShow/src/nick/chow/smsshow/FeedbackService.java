package nick.chow.smsshow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import nick.chow.app.context.Constants;
import nick.chow.app.context.SimpleMail;
import nick.chow.app.context.Tools;
import android.app.IntentService;
import android.content.Intent;
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
		SimpleMail m = new SimpleMail(this);
        String from = intent.getStringExtra(Constants.FEEDBACK_FROM);
        String body = intent.getStringExtra(Constants.FEEDBACK_CONTENT);
        if (from.length() > 0) {
        	body += "\r\n" + getString(R.string.from) + from;
        }
        if (errorCount > 0) {
        	body += "\r\n errorCount = " + errorCount;
        }
        body += "\r\n" + Tools.buildDeviceInfo();
        m.setBody(body);
        return m.send();
	}
}
