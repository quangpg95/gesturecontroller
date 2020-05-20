package com.example.thinh.gesturecontroller.ui.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

public class SlideGestureView extends GestureBaseView {

    private boolean mIsLeftSide;

    public SlideGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //Bkav QuangNDb doan nay nguoc vi thanh slide no co chieu nguoc lai voi thanh home
    @Override
    public int mappingSizeWidth(int width) {
        final double ratio = (double) width / 100;
        return (int) (getMaxWindowHeight() * ratio);
    }
    //Bkav QuangNDb doan nay nguoc vi thanh slide no co chieu nguoc lai voi thanh home
    @Override
    public int mappingSizeHeight(int height) {
        final double ratio = (double) height / 100;
        return (int) (mDisplayMetrics.heightPixels * ratio);
    }
    //Bkav QuangNDb doan nay nguoc vi thanh slide no co chieu nguoc lai voi thanh home
    @Override
    public void updateSize(int width, int height) {
        super.updateSize(mappingSizeWidth(height), mappingSizeHeight(width), false);
    }

    // thinhavb : check xem cot hien thi ben trai hay ben phai
    public void isLeftSide(boolean isLeft){
        mIsLeftSide = isLeft;
    }

    @Override
    protected boolean getLeftSide(){
        return mIsLeftSide;
    }
}
