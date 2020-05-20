package com.example.thinh.gesturecontroller.observer;

import android.support.v4.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.List;

//Bkav QuangNDb class de cap nhat du lieu setting thay doi den tat ca cac observer
public class SizeSettingData implements SizeSettingSubject {

    private List<SizeSettingObserver> mSettingObserverList;
    private int mWidth;
    private int mHeight;
    private int mColor;
    private boolean mIsTransparent;

    public SizeSettingData() {
        this.mSettingObserverList = new ArrayList<>();
    }

    @Override
    public void registerObserver(SizeSettingObserver sizeSettingObserver) {
        mSettingObserverList.add(sizeSettingObserver);
    }

    @Override
    public void removeObserver(SizeSettingObserver sizeSettingObserver) {
        if (mSettingObserverList.indexOf(sizeSettingObserver) >= 0) {
            mSettingObserverList.remove(sizeSettingObserver);
        }
    }

    @Override
    public void notifyObservers() {
        for (SizeSettingObserver sizeSettingObserver : mSettingObserverList) {
            sizeSettingObserver.updateSize(mWidth, mHeight);
            if (mIsTransparent) {
                sizeSettingObserver.updateColor(ColorUtils.setAlphaComponent(mColor, 0));
            }else {
                sizeSettingObserver.updateColor(mColor);
            }
        }
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        notifyObservers();
    }

    public void setColor(int color) {
        mColor = color;
        notifyObservers();
    }

    public void setTransparent(boolean isTrans) {
        mIsTransparent = isTrans;
        notifyObservers();
    }
}
