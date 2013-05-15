package nick.chow.smsshow;

import java.io.PrintWriter;
import java.io.StringWriter;

import nick.chow.app.context.Constants;
import nick.chow.app.context.Mail;
import nick.chow.app.context.MenuItemSelector;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * @author zhouyun
 *
 */
public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onAbout(View view) {
		Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
		startActivity(intent);
	}
	
	public void onTest(View view) {
		Intent intent = new Intent(getApplicationContext(), SMSPopupActivity.class);
		intent.putExtra(Constants.IS_TEST, true);
		startActivity(intent);
	}
	
	class SendMail extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Mail m = new Mail("yunzhounj@gmail.com", "googlePassw0rd");
	        String[] toArr = {"yunchow@qq.com"}; 
	        boolean send = false;
	        try {
	        	m.set_to(toArr); 
	            m.set_from("nick@chow.com"); 
	            m.set_subject("Q短信用户反馈"); 
	            m.setBody("感觉做的挺好的，继续加油。");
	            send = m.send();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				return sw.toString();
			}
			return "send scuessfully : send = " + send;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void onSetting(View view) {
		Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
		startActivity(intent);
	}
	
	public void onFeedback(View view) {		
        new SendMail().execute(new String[]{});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		new MenuItemSelector(this).onItemSelect(item);
		return super.onMenuItemSelected(featureId, item);
	}

}
