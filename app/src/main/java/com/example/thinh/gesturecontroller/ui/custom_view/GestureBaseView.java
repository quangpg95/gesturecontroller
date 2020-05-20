package com.example.thinh.gesturecontroller.ui.custom_view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.controller.GestureViewController;
import com.example.thinh.gesturecontroller.observer.SizeSettingObserver;
import com.example.thinh.gesturecontroller.observer.SizeSettingSubject;

//Bkav QuangNDb view hien thi va xu ly gesture de window manager add len
public abstract class GestureBaseView extends FrameLayout implements SizeSettingObserver {

    protected View mDividerView;
    protected View mFakeView; // thinhavb: view dung de hien de ng dung biet vi tri cua thanh dieu huong
    protected Context mContext;
    protected DisplayMetrics mDisplayMetrics;
    protected LayoutParams mParams;
    protected LayoutParams mParamsFakeView;
    protected GestureViewController mGestureViewController;
    protected SizeSettingSubject mSizeSettingSubject;

    public GestureBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDividerView = findViewById(R.id.img_divider);
        mFakeView = findViewById(R.id.view_fake);
        mParams = new LayoutParams(mDividerView.getWidth(), mDividerView.getHeight());
        mParamsFakeView = new LayoutParams(mFakeView.getWidth(), mFakeView.getHeight());
    }

    //Bkav QuangNDb set observerable cho view
    public void setSizeSettingSubject(SizeSettingSubject sizeSettingSubject) {
        this.mSizeSettingSubject = sizeSettingSubject;
        mSizeSettingSubject.registerObserver(this);
    }

    //Bkav QuangNDb max height cua view
    public int getMaxWindowHeight() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.divider_height);
    }

    //Bkav QuangNDb mapping size width tu setting sang
    public abstract int mappingSizeWidth(int width);

    //Bkav QuangNDb mapping size height tu setting sang
    public abstract int mappingSizeHeight(int height);


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureViewController.onTouchEvent(event);
    }


    public void setController(GestureViewController controller){
        mGestureViewController = controller;
    }

    //Bkav QuangNDb doi mau thanh divider
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void updateColor(int color) {
        mFakeView.getBackground().setTint(color);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mGestureViewController.getGestureController().updateResources(mContext);
    }

    //Bkav QuangNDb update transparent tat ca cac indicator == set opacity =0
    public void updateTransparent(int color) {}

    protected void updateSize(int width, int height, boolean isHome) {
        mParams.width = width;
        mParams.height = height;
        mDividerView.setLayoutParams(mParams);

        int defaultSizeFake = 20;
        if (isHome){
            mParamsFakeView.gravity = Gravity.BOTTOM;
            mParamsFakeView.width = width;
            mParamsFakeView.height = defaultSizeFake;
        } else {
            if (getLeftSide()){
                mParamsFakeView.gravity = Gravity.LEFT;
            } else {
                mParamsFakeView.gravity = Gravity.RIGHT;
            }
            mParamsFakeView.width = defaultSizeFake;
            mParamsFakeView.height = height;
        }
        mFakeView.setLayoutParams(mParamsFakeView);
    }

    protected boolean getLeftSide(){
        return false;
    }
}
