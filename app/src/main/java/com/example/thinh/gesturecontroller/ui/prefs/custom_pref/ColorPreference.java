package com.example.thinh.gesturecontroller.ui.prefs.custom_pref;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.ui.prefs.fragment.ColorChooserDialog;
import com.example.thinh.gesturecontroller.util.ColorUtils;
import com.example.thinh.gesturecontroller.util.PrefUtil;

public class ColorPreference extends BasePreference implements ColorChooserDialog.OnColorSelectedListener {

    private int[] colorChoices = {};

    public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs,defStyleAttr);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
    }

    public ColorPreference(Context context) {
        super(context);
        initAttrs(null,0);
    }


    protected void initAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.ColorPreferenceCompat, defStyle, defStyle);

        try {
            int choicesResId = a.getResourceId(R.styleable.ColorPreferenceCompat_colorChoices,
                    R.array.default_color_choice_values);
            colorChoices = ColorUtils.extractColorArray(choicesResId, getContext());

        } finally {
            a.recycle();
        }
        itemLayoutId = R.layout.pref_color_layout;
        setWidgetLayoutResource(itemLayoutId);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ImageView previewView = (ImageView) holder.findViewById(R.id.color_view);
        ColorUtils.setColorViewValue(previewView, value == -1 ? PrefUtil.getInt(getContext()
                , getContext().getString(R.string.pref_color_key)
                , ContextCompat.getColor(getContext(), R.color.colorPrimaryAlternate)) : value, false);
    }


    @Override
    protected void onClick() {
        super.onClick();
        ColorUtils.showDialog(getContext(), this,  colorChoices, getValue());
    }

    @Override
    public void onColorSelected(int newColor) {
        setValue(newColor);
    }
}
