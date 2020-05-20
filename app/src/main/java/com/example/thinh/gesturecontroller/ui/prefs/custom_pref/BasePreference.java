package com.example.thinh.gesturecontroller.ui.prefs.custom_pref;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public abstract class BasePreference extends Preference {
    protected int value = -1;
    protected int itemLayoutId;

    public BasePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BasePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePreference(Context context) {
        super(context);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            this.value = value;
            persistInt(value);
            notifyChanged();
        }
    }

    // TODO: ham nay bi deprecated nhung chua tim ra phuong an thay the
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public int getValue() {
        return value;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    protected abstract void initAttrs(AttributeSet attrs, int defStyle);


}
