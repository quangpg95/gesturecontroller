package com.example.thinh.gesturecontroller.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//Bkav QuangNDb class tien ich pref
public class PrefUtil {

    //Bkav QuangNDb get value int in pref
    public static int getInt(Context context, String key, int defaultValue) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    //Bkav QuangNDb get value boolean in pref
    public static boolean getBool(Context context, String key, boolean defaultValue) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    //Bkav QuangNDb set int value in pref
    public static void setInt(Context context, String key, int newValue) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, newValue);
        editor.apply();
    }


}
