package com.example.thinh.gesturecontroller.controller;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.provider.Settings;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;


public class VolumeViewController extends VerticalSeekBarController {
    private AudioManager mAudioManager;

    private boolean mIsVisible;


    public VolumeViewController(Context context) {
        super(context);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mVerticalSeekbarView.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    }

    protected void seekbarChange(BoxedVertical boxedPoints, int points){
        if (!mIsVisible){
            return;
        }
        final int value = points;
        AsyncTask.execute(new Runnable() {
            public void run() {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, value);
            }
        });
    }

    @Override
    public void addViewToWindow() {
        int startVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVerticalSeekbarView.setValue(startVolume);
        super.addViewToWindow();
    }

    @Override
    public void setVisibility(boolean visibility){
        mIsVisible = visibility;
    }
}
