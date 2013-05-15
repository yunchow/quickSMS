package nick.chow.smsshow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import nick.chow.app.context.Mail;
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
		Mail m = new Mail("yunzhounj@gmail.com", "googlePassw0rd");
        String[] toArr = {"yunchow@qq.com"};
    	m.set_to(toArr); 
        m.set_from("nick@chow.com"); 
        m.set_subject("Q短信用户反馈"); 
        String from = intent.getStringExtra("from");
        m.setBody(intent.getStringExtra("content") + from + "(" + errorCount + ")");
        return m.send();
	}

}
