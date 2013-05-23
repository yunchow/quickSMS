package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author zhouyun
 *
 */
public class DebugActivity extends Activity {
	private String exceptionDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		TextView tv = (TextView)findViewById(R.id.exceptionMsg);
		exceptionDetail = getIntent().getStringExtra(Constants.EXCEPTION);
		tv.setText(exceptionDetail);
	}
	
	public void feedbackLog(View view) {
		Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
		intent.putExtra(Constants.EXCEPTION, exceptionDetail);
		startActivity(intent);
	}

}
