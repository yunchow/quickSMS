package nick.chow.app.component;

import nick.chow.smsshow.R;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author zhouyun
 *
 */
public class SMSListView extends ListView implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	private static String TAG = "SMSListView";
	
	public SMSListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG, "SMSListView(Context context, AttributeSet attrs)");
		setOnItemClickListener(this);
		setOnItemLongClickListener(this);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AlertDialog.Builder builder = new Builder(getContext());
		builder.setTitle("324324324324");
		builder.setItems(getResources().getStringArray(R.array.menu1), null);
		builder.setPositiveButton("打开短信", null);
		builder.setNegativeButton("关闭", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		AlertDialog.Builder builder = new Builder(getContext());
		builder.setTitle("324324324324");
		builder.setItems(getResources().getStringArray(R.array.menu2), null);
		builder.setPositiveButton("打开短信", null);
		builder.setNegativeButton("关闭", null);
		AlertDialog dialog = builder.create();
		dialog.show();
		return true;
	}
	
}
