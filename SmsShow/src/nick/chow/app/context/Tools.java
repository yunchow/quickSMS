package nick.chow.app.context;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.widget.Toast;

public class Tools {

	public static void show(Context context, String text) {
		//Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	public static void show(Context context, Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Toast.makeText(context, sw.toString(), Toast.LENGTH_LONG).show();
	}
	
}
