package org.xerrard.suspendedballdemo;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View
        .OnClickListener {

    Button mFloatingBtn;
    WindowManager.LayoutParams mLayoutParams;
    WindowManager mWM;
    int mLastX;
    int mLastY;
    boolean isHovered = false;
    Intent mHoverIntent;
    Intent mNoHoverIntent;
    private final static String HOVER_ACTION = "com.android.hover";
    private final static String NO_HOVER_ACTION = "com.android.hovered";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWM = getWindowManager();

        setFloatingButton();

        mHoverIntent= new Intent(HOVER_ACTION);
        mNoHoverIntent = new Intent(NO_HOVER_ACTION);
    }

    private void setFloatingButton() {
        mFloatingBtn = new Button(this);
        mFloatingBtn.setText("Suspend");
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                0,
                0,
                PixelFormat.TRANSPARENT);
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = 100;
        mLayoutParams.y = 300;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        //FrameLayout mFrameLayout = new FrameLayout(this);

        mFloatingBtn.setOnTouchListener(this);
        mFloatingBtn.setOnClickListener(this);
        mWM.addView(mFloatingBtn, mLayoutParams);
        //ViewGroup decor = (ViewGroup) getWindow().peekDecorView();
        //decor.addView(mFloatingBtn,mLayoutParams);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                mLayoutParams.x = mLayoutParams.x + deltaX;
                mLayoutParams.y = mLayoutParams.y + deltaY;
                mWM.updateViewLayout(v, mLayoutParams);
                mLastX = x;
                mLastY = y;
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        //hoverOperate();
        sendHoverBroadcast();
    }

    private void sendHoverBroadcast() {
        if (isHovered) {
            sendBroadcast(mNoHoverIntent);
            isHovered = false;
            mFloatingBtn.setText("Suspend");
        }else{
            sendBroadcast(mHoverIntent);
            isHovered = true;
            mFloatingBtn.setText("Recover");
        }
    }


    private void hoverOperate() {
        Class windowClass = null;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        Window mWindow = getWindow();

        try {
            windowClass = Class.forName(mWindow.getClass().getName());
            Method layoutToHover = windowClass.getDeclaredMethod("layoutToHover");
            layoutToHover.setAccessible(true);
            Method layoutToNoHover = windowClass.getDeclaredMethod("layoutToNoHover");
            layoutToNoHover.setAccessible(true);

            if (isHovered) {
                layoutToNoHover.invoke(mWindow);
                isHovered = false;
            } else {
                layoutToHover.invoke(mWindow);
                isHovered = true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
