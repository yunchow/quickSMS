package nick.chow.smsshow;

import nick.chow.app.context.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DebugActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		TextView tv = (TextView)findViewById(R.id.exceptionMsg);
		tv.setText(getIntent().getStringExtra(Constants.EXCEPTION));
	}

}
