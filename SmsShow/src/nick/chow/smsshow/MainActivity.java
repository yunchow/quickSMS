package nick.chow.smsshow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView testView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		testView = (TextView) findViewById(R.id.testView);
	}
	
	public void testEnv(View view) {
		Cursor cursor = queryAllUnReadSMS();//getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
		for (;cursor.moveToNext();) {
			String each = "";
			String title = "";
			for (int k = cursor.getColumnCount(), i = 0; i < k; i++) {
				Log.i("MainActivity", cursor.getColumnName(i) + " = " + cursor.getString(i));
				title += cursor.getColumnName(i) + " ";
				each += cursor.getString(i) + " ";
			}
			if (cursor.isFirst()) {
				testView.append(title + "\r\n");
			}
			testView.append(each + "\r\n");
		}
		testView.append("query finished\r\n");
		
		// ------------------------------------
		Intent intent = new Intent(getApplicationContext(), SMSPopupActivity.class);
		startActivity(intent);
	}
	
	public Cursor queryAllUnReadSMS() {
		String[] projection = new String[]{"_id", "address", "date_sent", "body", "read"};
		return getContentResolver().query(Uri.parse("content://sms/"), projection, "read=?", new String[]{"0"}, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
