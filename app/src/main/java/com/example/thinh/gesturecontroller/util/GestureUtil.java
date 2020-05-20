package com.example.thinh.gesturecontroller.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

//Bkav QuangNDb class chua util chung
public class GestureUtil {

    public static final int BOTTOM_SIDE_WINDOW = 0;
    public static final int LEFT_SIDE_WINDOW = 1;
    public static final int RIGHT_SIDE_WINDOW = 2;

    private static final boolean THINHAVB_SHOW_LOG = true;

    public static int dpToPixel(int dp, Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displaymetrics.density);
    }

    public static void thinhavbShowLog(String message) {
        if (THINHAVB_SHOW_LOG)
            Log.d("thinhavb_gestute", message);
    }

    @Nullable
    public static AppCompatActivity resolveContext(Context context) {
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextWrapper) {
            return resolveContext(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

}
