package ws.isak.audiocapturesas.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by isakherman on 7/11/16.
 */

public class NonSwipeableViewPager extends ViewPager {

    public NonSwipeableViewPager (Context context) {
        super (context);
    }

    public NonSwipeableViewPager (Context context, AttributeSet attributes) {
        super (context, attributes);
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent arg0) {
        return false;               //no swipe to switch pages
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        return false;               //no swipe to switch pages
    }
}
