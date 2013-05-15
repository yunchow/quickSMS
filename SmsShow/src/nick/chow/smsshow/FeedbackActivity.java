package nick.chow.smsshow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author zhouyun
 *
 */
public class FeedbackActivity extends Activity {
	private EditText question;
	private EditText contactMetod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		question = (EditText) findViewById(R.id.question);
		contactMetod = (EditText) findViewById(R.id.contactMethod);
	}
	
	public void onFeedback(View view) {
		String qcontent = question.getText().toString();
		if (qcontent.length() < 2) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("Q短信问题反馈");
			builder.setMessage("请输入您要反馈的问题（最少两个字）");
			builder.setCancelable(true);
			builder.setPositiveButton("确定", null);
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			String[] params = new String[]{qcontent, contactMetod.getText().toString()};
			new SendMail().execute(params);
		}
	}
	
	class SendMail extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
	        String from = params[1].length() > 0 ? "，" + params[1] : "";
	        Intent service = new Intent();
	        service.setClass(FeedbackActivity.this, FeedbackService.class);
	        service.putExtra("from", from);
	        service.putExtra("content", params[0]);
	        startService(service);
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			Toast.makeText(FeedbackActivity.this, getString(R.string.feedbackthanks), Toast.LENGTH_SHORT).show();
		}
		
	}
	
}
