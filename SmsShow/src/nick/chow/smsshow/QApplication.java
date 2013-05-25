package nick.chow.smsshow;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeUnit;

import nick.chow.app.context.SimpleMail;
import nick.chow.app.context.Tools;
import android.app.Application;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

/**
 * @author zhouyun
 *
 */
public class QApplication extends Application {
	private static final String TAG = "QApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				if (Log.isLoggable(TAG, Log.ERROR)) {
					Log.e(TAG, Tools.parse(ex));
				}
				new Thread() {
					public void run() {
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "³ÌÐò³ö´íÁË£¡", Toast.LENGTH_LONG).show();
						Looper.loop();
					};
				}.start();
				final SimpleMail mail = new SimpleMail(QApplication.this);
				mail.setBody(Tools.buildDeviceInfo() + "\r\n" + Tools.parse(ex));
				new Thread() {
					public void run() {
						try {
							Log.i(TAG, "start to send exception log");
							mail.send();
							Log.i(TAG, "send exception log scuessfully");
						} catch (Exception e) {
							Log.i(TAG, "send exception log failed");
							Log.e(TAG, Tools.parse(e));
						}
						Process.killProcess(Process.myPid());
						System.exit(0);
					};
				}.start();
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					Log.e(TAG, Tools.parse(e));
				}
				Log.i(TAG, "start to exit app");
			}
		});
	}
	
}
