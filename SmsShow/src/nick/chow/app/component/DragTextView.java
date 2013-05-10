package nick.chow.app.component;

import nick.chow.app.context.Tools;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author zhouyun
 *
 */
public class DragTextView extends TextView implements View.OnLongClickListener {
	private final String tag = getClass().getSimpleName();
	
	private boolean startDrag = false;
	private View outerLayout;
	
	private ImageView imageView;
	private Bitmap bitmap;
	private WindowManager windowManager;
	private Vibrator vibrator;
	
	WindowManager.LayoutParams layout;

	public DragTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnLongClickListener(this);
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	@Override
	public boolean onLongClick(View v) {
		Tools.show(getContext(), "longclick");
		Log.i(tag, "start to move the window");
		startDrag = true;
		outerLayout = (LinearLayout)getParent();
		outerLayout.setVisibility(INVISIBLE);
		outerLayout.setDrawingCacheEnabled(true);
		
		vibrator.vibrate(100);
		
		bitmap = Bitmap.createBitmap(outerLayout.getDrawingCache());
		imageView = new ImageView(getContext());
		imageView.setImageBitmap(bitmap);
		imageView.setAlpha(0.8f);
		
		layout = new LayoutParams();
		layout.width = LayoutParams.WRAP_CONTENT;
		layout.height = LayoutParams.WRAP_CONTENT;
		layout.gravity = Gravity.LEFT | Gravity.TOP;
		
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		
		int[] location = new int[2];
		this.getLocationOnScreen(location);
		layout.x = location[0];
		layout.y = location[1];
		
		Log.i(tag, "location[0] = " + location[0]);
		Log.i(tag, "location[1] = " + location[1]);
		
		windowManager.addView(imageView, layout);
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int rawX = (int) event.getRawX();
		int rawY = (int) event.getRawY();
		int currentX = (int) event.getX();
		//int currentY = (int) event.getY();
		Log.i(tag, "action = " + action);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (outerLayout != null && startDrag && imageView != null) {
				layout.x = rawX - currentX;
				layout.y = rawY /*- currentY*/;
				windowManager.updateViewLayout(imageView, layout);
				return true;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (outerLayout != null && imageView != null 
					&& bitmap != null && layout != null) {
				outerLayout.setX(layout.x);
				outerLayout.setY(layout.y);
				outerLayout.setVisibility(VISIBLE);
				windowManager.removeView(imageView);
				imageView.setImageBitmap(null);
				imageView = null;
				bitmap.recycle();
				bitmap = null;
				startDrag = false;
			}
			break;
		default:
			break;
		}
		
		return super.onTouchEvent(event);
	}

}
