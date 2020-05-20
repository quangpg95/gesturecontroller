package com.example.thinh.gesturecontroller.ui.custom_view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.example.thinh.gesturecontroller.R;

//Bkav QuangNDb view home gesture
public class HomeGestureView extends GestureBaseView {

    public HomeGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getDefaultWindowHeight() {
        return mDisplayMetrics.heightPixels / (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 8);
    }

    @Override
    public int mappingSizeWidth(int width) {
        final double ratio = (double) width / 100;
        return (int) (mDisplayMetrics.widthPixels * ratio);
    }

    @Override
    public int mappingSizeHeight(int height) {
        final double ratio = (double) height / 100;
        return (int) (getMaxWindowHeight() * ratio);
    }

    @Override
    public void updateSize(int width, int height) {
        super.updateSize(mappingSizeWidth(width), mappingSizeHeight(height), true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
    }
}
