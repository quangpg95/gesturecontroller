package com.example.thinh.gesturecontroller.controller;

import android.content.Context;
import android.provider.Settings;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

public class BrightnessViewController extends VerticalSeekBarController {
    private final static int MAXIMUM_BACKLIGHT_BRIGHTNESS = 255;
    private final static int DEFAULT_BACKLIGHT_BRIGHTNESS = 150;

    private int mModeBrightness = -1;

    private boolean mIsVisible;

    public BrightnessViewController(Context context) {
        super(context);
        mVerticalSeekbarView.setMax(MAXIMUM_BACKLIGHT_BRIGHTNESS);
    }

    protected void seekbarChange(BoxedVertical boxedPoints, int points) {
        if (!mIsVisible){
            return;
        }
        if (mModeBrightness == 0) {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, points);
        } else if (mModeBrightness == 1) {
            float value = (((float) points * 2) / MAXIMUM_BACKLIGHT_BRIGHTNESS) - 1.0f;
            Settings.System.putFloat(mContext.getContentResolver(), "screen_auto_brightness_adj", value);
        }
    }

    @Override
    public void addViewToWindow() {
        mIsVisible = true;
        mModeBrightness = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        mVerticalSeekbarView.setValue(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, DEFAULT_BACKLIGHT_BRIGHTNESS));
        super.addViewToWindow();
    }

    @Override
    public void setVisibility(boolean visibility){
        mIsVisible = visibility;
    }

}
