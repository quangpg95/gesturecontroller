package com.example.thinh.gesturecontroller.ui.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.ui.prefs.fragment.GeneralPreferenceFragment;

public class MainActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    private Button mResetButton;
    private Button mGetInforButton;
    private TextView mCountTextView;
    public static final String KEY_COUNT_EVENT = "key_count_event";
    SharedPreferences mSharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCountTextView = (TextView) findViewById(R.id.count);
        mResetButton = (Button) findViewById(R.id.reset);
        mGetInforButton = (Button) findViewById(R.id.get_infor);
        checkWriteSystemSetting();
        addFragment();

        mSharedpreferences = getSharedPreferences(KEY_COUNT_EVENT, Context.MODE_PRIVATE);
        mCountTextView.setText("So lan su dung gesture: " + mSharedpreferences.getInt(KEY_COUNT_EVENT, -1));

        mGetInforButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountTextView.setText("So lan su dung gesture: " + mSharedpreferences.getInt(KEY_COUNT_EVENT, -1));
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSharedpreferences.edit();
                editor.putInt(KEY_COUNT_EVENT, 0);
                editor.commit();
            }
        });
    }

    private void addFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(GeneralPreferenceFragment.TAG);
        if (fragment == null) {
            fragment = new GeneralPreferenceFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.parent_layout, fragment, GeneralPreferenceFragment.TAG);
        ft.commit();
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        GeneralPreferenceFragment mPrefsFragment = new GeneralPreferenceFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        mPrefsFragment.setArguments(args);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        mFragmentTransaction.add(R.id.parent_layout, mPrefsFragment, preferenceScreen.getKey());
        mFragmentTransaction.addToBackStack(preferenceScreen.getKey());
        mFragmentTransaction.commit();
        return true;
    }

    // thinhavb: ghi de setting he thong
    private void checkWriteSystemSetting() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }


    // thinhavb: dem so lan sun dung gesture

}
