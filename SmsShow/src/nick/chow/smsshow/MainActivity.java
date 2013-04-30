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
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView testView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		testView = (TextView) findViewById(R.id.testView);
		
		/*ContentValues cv = new ContentValues();
		cv.put("read", "0");
		int updated = getContentResolver().update(Uri.parse("content://sms/"), cv, "thread_id=?", new String[]{"3"});
		Log.i("MainActivity", "update = " + updated);*/
	}
	
	public void testEnv(View view) {
		testView.append("开始查找短信。。。。\r\n");
		Cursor cursor = queryAllUnReadSMS();//getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
		Toast.makeText(getApplicationContext(), "查询结束 " + cursor, Toast.LENGTH_SHORT).show();
		testView.append("共查找到"+ cursor.getCount() +"条短信\r\n");
		testView.append("开始列表显示短信\r\n");
		for (;cursor.moveToNext();) {
			String each = "";
			String title = "";
			for (int k = cursor.getColumnCount(), i = 0; i < k; i++) {
				Log.i("MainActivity", cursor.getColumnName(i) + " = " + cursor.getString(i));
				title += cursor.getColumnName(i) + " ";
				each += cursor.getString(i) + " ";
			}
			//title += cursor.getColumnName(7) + " ";
			//each += cursor.getString(7) + " ";
			if (cursor.isFirst()) {
				testView.append(title + "\r\n");
			}
			testView.append(each + "\r\n");
		}
		testView.append("显示结束\r\n");
		
		// ------------------------------------
		Intent intent = new Intent(getApplicationContext(), SMSPopupActivity.class);
		startActivity(intent);
	}
	
	public Cursor queryAllUnReadSMS() {
		String[] projection = new String[]{"_id", "address", "date_sent", "body", "read"};
		Toast.makeText(getApplicationContext(), "开始查询", Toast.LENGTH_SHORT).show();
		return getContentResolver().query(Uri.parse("content://sms/"), projection, "read=?", new String[]{"0"}, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
