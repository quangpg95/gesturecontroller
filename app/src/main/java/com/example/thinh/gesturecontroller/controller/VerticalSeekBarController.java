package com.example.thinh.gesturecontroller.controller;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import com.example.thinh.gesturecontroller.R;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

import static android.content.Context.WINDOW_SERVICE;

public class VerticalSeekBarController {
    private final static int TIME_HIDE_SEEKBAR = 3000;
    private final static int RADIUS_SEEKBAR = 50;

    protected Context mContext;
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;
    protected BoxedVertical mVerticalSeekbarView;
    private int mHeightSeekBar;
    private int mWidthSeekBar;

    private Handler mHandler = new Handler();

    private Runnable mAutoHideSeekbarRunnable = new Runnable() {
        @Override
        public void run() {
            removeViewVolume();
        }
    };

    public VerticalSeekBarController(Context context) {
        mContext = context;

        mHeightSeekBar = (int) context.getResources().getDimension(R.dimen.height_verical_seekbar);
        mWidthSeekBar = (int) context.getResources().getDimension(R.dimen.width_verical_seekbar);

        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams(mWidthSeekBar, mHeightSeekBar
                , WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;

        mVerticalSeekbarView = (BoxedVertical) LayoutInflater.from(mContext).inflate(R.layout.volume_layout, null);
        mVerticalSeekbarView.setCornerRadius(RADIUS_SEEKBAR);
        mVerticalSeekbarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    removeViewVolume();
                    return true;
                }
                return false;
            }
        });

        mVerticalSeekbarView.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                try {
                    seekbarChange(boxedPoints, points);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {
                if (mAutoHideSeekbarRunnable != null) {
                    mHandler.removeCallbacks(mAutoHideSeekbarRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {
                setTimeHideSeekbar();
            }
        });
    }

    public void addViewToWindow() {
        setTimeHideSeekbar();
        if (mVerticalSeekbarView.getWindowToken() == null) {
            mWindowManager.addView(mVerticalSeekbarView, mLayoutParams);
        }
    }

    public void positionAddView(int x, int y) {
        mLayoutParams.x = x;
        mLayoutParams.y = y;
    }

    protected void seekbarChange(BoxedVertical boxedPoints, int points) throws Exception {
    }

    private void setTimeHideSeekbar() {
        mHandler.postDelayed(mAutoHideSeekbarRunnable, TIME_HIDE_SEEKBAR);
    }

    private void removeViewVolume() {
        if (mVerticalSeekbarView.getWindowToken() != null) {
            mWindowManager.removeView(mVerticalSeekbarView);
        }
        setVisibility(false);
    }

    public int getHeightSeekbar() {
        return mHeightSeekBar;
    }

    public int getWidthSeekbar() {
        return mWidthSeekBar;
    }

    // thinhavb: dung de check xem co dang hien tren man hinh hay khong
    public void setVisibility(boolean visibility){}

}
