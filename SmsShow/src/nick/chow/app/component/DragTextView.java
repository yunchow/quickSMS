package nick.chow.app.component;

import nick.chow.app.context.Tools;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

	public DragTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnLongClickListener(this);
	}
	
	@Override
	public boolean onLongClick(View v) {
		Tools.show(getContext(), "longclick");
		Log.i(tag, "start to move the window");
		startDrag = true;
		outerLayout = (LinearLayout)getParent();
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int currentX = (int) event.getX();
		int currentY = (int) event.getY();
		Log.i(tag, "action = " + action);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			if (outerLayout != null && startDrag) {
				outerLayout.setX(currentX);
				outerLayout.setY(currentY);
				return true;
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			
			break;
		default:
			break;
		}
		
		return super.onTouchEvent(event);
	}

}
