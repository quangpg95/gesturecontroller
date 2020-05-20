package com.example.thinh.gesturecontroller.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.service.GestureService;
import com.example.thinh.gesturecontroller.observer.SizeSettingData;
import com.example.thinh.gesturecontroller.ui.custom_view.GestureBaseView;
import com.example.thinh.gesturecontroller.ui.custom_view.HomeGestureView;
import com.example.thinh.gesturecontroller.ui.custom_view.SlideGestureView;
import com.example.thinh.gesturecontroller.util.GestureUtil;
import com.example.thinh.gesturecontroller.util.PrefUtil;

import static android.content.Context.WINDOW_SERVICE;

//Bkav QuangNDb class dung de quan ly cac view duoc add len window manager
public class GestureViewController implements SharedPreferences.OnSharedPreferenceChangeListener {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mBottomParams;
    private WindowManager.LayoutParams mLeftParams;
    private WindowManager.LayoutParams mRightParams;
    private Context mContext;
    private GestureBaseView mBottomView;
    private SlideGestureView mRightSlideView, mLeftSlideView;
    private int mWindowHeight;
    private int mWindowWidth;
    private int mColor;
    private boolean mIsTransparent;
    private boolean mIsHideHome;
    private boolean mIsHideLeft;
    private boolean mIsHideRight;
    private SizeSettingData mSizeSettingData;

    private GestureController mGestureController;

    private int mScreenWidth;

    private int mScreenHeight;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public GestureViewController(Context context, GestureService gestureService) {
        this.mContext = context;
        mGestureController = new GestureController(context, this, gestureService);
        onCreate();
    }

    private void onCreate() {
        Display display = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mSizeSettingData = new SizeSettingData();
        registerChangePref();
        addHomeParam();
        addLeftParam();
        addRightParam();
        mWindowHeight = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_width_window_key), 25);
        mWindowWidth = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_height_window_key), 25);
        mIsTransparent = PrefUtil.getBool(mContext, mContext.getString(R.string.pref_transparent_indicators_key), false);
        mIsHideHome = PrefUtil.getBool(mContext, mContext.getString(R.string.pref_hide_home_indicator_key), false);
        mIsHideLeft = PrefUtil.getBool(mContext, mContext.getString(R.string.pref_hide_right_indicator_key), false);
        mIsHideRight = PrefUtil.getBool(mContext, mContext.getString(R.string.pref_hide_left_indicator_key), false);
        mColor = PrefUtil.getInt(mContext
                , mContext.getString(R.string.pref_color_key)
                , ContextCompat.getColor(mContext, R.color.colorPrimaryAlternate));
        hideOrShowHomeIndicator();
        hideOrShowLeftIndicator();
        hideOrShowRightIndicator();
        setTransparentIndicators();
        setColorIndicators();
        setSizeIndicators();
    }

    //Bkav QuangNDb them view home(bottom)
    private void addHomeParam() {
        mBottomView = (HomeGestureView) LayoutInflater.from(mContext).inflate(R.layout.home_gesture_view, null);
        mBottomView.setController(this);
        mBottomParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mBottomParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mWindowManager.addView(mBottomView, mBottomParams);
        mBottomView.setSizeSettingSubject(mSizeSettingData);
    }

    //Bkav QuangNDb them view left
    private void addLeftParam() {
        mLeftSlideView = (SlideGestureView) LayoutInflater.from(mContext).inflate(R.layout.slide_gesture_view, null);
        mLeftSlideView.isLeftSide(true);
        mLeftSlideView.setController(this);
        mLeftParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mLeftParams.gravity = Gravity.START | Gravity.TOP;
        mWindowManager.addView(mLeftSlideView, mLeftParams);
        mLeftSlideView.setSizeSettingSubject(mSizeSettingData);
    }

    //Bkav QuangNDb them view right
    private void addRightParam() {
        mRightSlideView = (SlideGestureView) LayoutInflater.from(mContext).inflate(R.layout.slide_gesture_view, null);
        mRightSlideView.isLeftSide(false);
        mRightSlideView.setController(this);
        mRightParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mRightParams.gravity = Gravity.END | Gravity.TOP;
        mWindowManager.addView(mRightSlideView, mRightParams);
        mRightSlideView.setSizeSettingSubject(mSizeSettingData);
    }

    //Bkav QuangNDb dang ky lang nghe settings thay doi
    private void registerChangePref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    //Bkav QuangNDb bat setting thay doi
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mContext.getString(R.string.pref_height_window_key).equals(key)) {
            mWindowHeight = sharedPreferences.getInt(key, 25);
            setSizeIndicators();
        } else if (mContext.getString(R.string.pref_width_window_key).equals(key)) {
            mWindowWidth = sharedPreferences.getInt(key, 25);
            setSizeIndicators();
        } else if (mContext.getString(R.string.pref_color_key).equals(key)) {
            mColor = sharedPreferences.getInt(key, 0);
            setColorIndicators();
        } else if (mContext.getString(R.string.pref_transparent_indicators_key).equals(key)) {
            mIsTransparent = sharedPreferences.getBoolean(key, false);
            setTransparentIndicators();
        } else if (mContext.getString(R.string.pref_hide_home_indicator_key).equals(key)) {
            mIsHideHome = sharedPreferences.getBoolean(key, false);
            hideOrShowHomeIndicator();
        } else if (mContext.getString(R.string.pref_hide_right_indicator_key).equals(key)) {
            mIsHideRight = sharedPreferences.getBoolean(key, false);
            hideOrShowRightIndicator();
        } else if (mContext.getString(R.string.pref_hide_left_indicator_key).equals(key)) {
            mIsHideLeft = sharedPreferences.getBoolean(key, false);
            hideOrShowLeftIndicator();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureController.onTouchEvent(event);
    }

    // thinhavb : update lai vi tri hien thi cua view
    public void updatePositionView(float initialX, float initialY, float downX, float downY, float currentX, float currentY, int typeView) {
        switch (typeView) {
            case GestureUtil.LEFT_SIDE_WINDOW:
                mLeftParams.y = (int) (initialY + (currentY - downY));
                mWindowManager.updateViewLayout(mLeftSlideView, mLeftParams);
                break;
            case GestureUtil.RIGHT_SIDE_WINDOW:
                mRightParams.y = (int) (initialY + (currentY - downY));
                mWindowManager.updateViewLayout(mRightSlideView, mRightParams);
                break;
            case GestureUtil.BOTTOM_SIDE_WINDOW:
                mBottomParams.x = (int) (initialX + (currentX - downX));
                mWindowManager.updateViewLayout(mBottomView, mBottomParams);
                break;
        }
    }

    public int getParamX(int position) {
        int x = 0;
        switch (position) {
            case GestureUtil.LEFT_SIDE_WINDOW:
                x = mLeftParams.x;
                break;
            case GestureUtil.RIGHT_SIDE_WINDOW:
                x = mRightParams.x;
                break;
            case GestureUtil.BOTTOM_SIDE_WINDOW:
                x = mBottomParams.x;
                break;
        }
        return x;
    }

    public int getParamY(int position) {
        int y = 0;
        switch (position) {
            case GestureUtil.LEFT_SIDE_WINDOW:
                y = mLeftParams.y;
                break;
            case GestureUtil.RIGHT_SIDE_WINDOW:
                y = mRightParams.y;
                break;
            case GestureUtil.BOTTOM_SIDE_WINDOW:
                y = mBottomParams.y;
                break;
        }
        return y;
    }

    public GestureController getGestureController() {
        return mGestureController;
    }

    //Bkav QuangNDb an hien home indicator
    private void hideOrShowHomeIndicator() {
        mBottomView.setVisibility(mIsHideHome ? View.GONE : View.VISIBLE);
    }

    //Bkav QuangNDb an hien left indi
    private void hideOrShowLeftIndicator() {
        mLeftSlideView.setVisibility(mIsHideLeft ? View.GONE : View.VISIBLE);
    }

    //Bkav QuangNDb an hien right indi
    private void hideOrShowRightIndicator() {
        mRightSlideView.setVisibility(mIsHideRight ? View.GONE : View.VISIBLE);
    }

    //Bkav QuangNDb set size indicatiors
    private void setSizeIndicators() {
        mSizeSettingData.setSize(mWindowWidth, mWindowHeight);
    }

    //Bkav QuangNDb set color indicator
    private void setColorIndicators() {
        mSizeSettingData.setColor(mColor);
    }

    //Bkav QuangNDb set transparent indicator
    private void setTransparentIndicators() {
        mSizeSettingData.setTransparent(mIsTransparent);
    }

    // thinhavb: animation phong to view khi long press
    public void scaleView(int position) {
//
//        Animation anim = new ScaleAnimation(
//                1f, 1f, // Start and end values for the X axis scaling
//                0f, 1f, // Start and end values for the Y axis scaling
//                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
//                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
//        anim.setFillAfter(true); // Needed to keep the result of the animation
//        anim.setDuration(1000);


        AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
        animation.setStartOffset(5000);
        animation.setFillAfter(true);
        animation.setDuration(10000);
        if(position == GestureUtil.LEFT_SIDE_WINDOW){
            mLeftSlideView.startAnimation(animation);
        } else if(position == GestureUtil.RIGHT_SIDE_WINDOW){
            mRightSlideView.startAnimation(animation);
        }
    }
}
