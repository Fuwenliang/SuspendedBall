package org.xerrard.suspendedballdemo;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by Administrator on 2015/11/16.
 */
public class FloatingButton extends Button {
    int LastX;
    int LastY;

    public FloatingButton(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getRawX();
        int Y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LastX = X;
                LastY = Y;
                break;
            case MotionEvent.ACTION_MOVE: {
                int offsetX = X - LastX;
                int offsetY = Y - LastY;
                layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom()
                        + offsetY);
                LastX = X;
                LastY = Y;

                break;
            }
            default:
                break;
        }
        return true;
    }
}
