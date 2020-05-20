package com.example.thinh.gesturecontroller.ui.prefs.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.factory.Factory;

public class GeneralPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = "PositionAndSizePreferen";
    Preference mColorPreference;
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.pref_general, s);
        mColorPreference = findPreference(getString(R.string.pref_color_key));
    }

    //Bkav QuangNDb dang ky lang nghe settings thay doi
    private void registerChangePref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    //Bkav QuangNDb dang ky lang nghe settings thay doi
    private void unregisterChangePref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        registerChangePref();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterChangePref();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.background_material_light));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_transparent_indicators_key))) {
            boolean isTrans = sharedPreferences.getBoolean(key, false);
            mColorPreference.setEnabled(!isTrans);
        }
    }
}
