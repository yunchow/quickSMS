package nick.chow.app.context;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import nick.chow.smsshow.DebugActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author zhouyun
 * 
 */
public class Tools {

	public static void show(Context context, String text) {
		if (!Constants.RELEASE) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}

	public static String parse(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static void show(Context context, Exception e) {
		if (!Constants.RELEASE) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Intent intent = new Intent(context, DebugActivity.class);
			intent.putExtra(Constants.EXCEPTION, sw.toString());
			context.startActivity(intent);
		}
	}

	public static void root() {
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			
		}
	}

	public static boolean hasRoot() {
		char[] arrayOfChar = new char[1024];
		try {
			int j = new InputStreamReader(Runtime.getRuntime().exec("su -c ls")
					.getErrorStream()).read(arrayOfChar);
			if (j == -1) {
				return true;
			}
		} catch (IOException e) {

		}
		return false;
	}

}
