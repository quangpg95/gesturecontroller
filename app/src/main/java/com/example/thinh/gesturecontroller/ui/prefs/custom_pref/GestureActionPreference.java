package com.example.thinh.gesturecontroller.ui.prefs.custom_pref;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.ui.prefs.fragment.GestureActionChooserDialog;
import com.example.thinh.gesturecontroller.util.GestureUtil;
import com.example.thinh.gesturecontroller.util.PrefUtil;

public class GestureActionPreference extends BasePreference implements GestureActionChooserDialog.OnActionSelectedListener {
    private String[] actions;
    public GestureActionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs,defStyleAttr);
    }

    public GestureActionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs,0);
    }

    public GestureActionPreference(Context context) {
        super(context);
        initAttrs(null,0);
    }

    @Override
    protected void initAttrs(AttributeSet attrs, int defStyle) {
        actions = getContext().getResources().getStringArray(R.array.list_gesture_action_name);
        itemLayoutId = R.layout.pref_gesture_and_action_layout;
        setWidgetLayoutResource(itemLayoutId);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView textView = (TextView) holder.findViewById(R.id.lbl_action);
        String action;
        if (value == -1) {
            action = actions[PrefUtil.getInt(getContext()
                    , getContext().getString(R.string.pref_gesture_action_key), 0)];
        }else {
            action = actions[value];
        }
        textView.setText(action);
    }

    @Override
    protected void onClick() {
        super.onClick();
        GestureActionChooserDialog gestureActionChooserDialog = GestureActionChooserDialog.newInstance();
        gestureActionChooserDialog.setOnActionSelectedListener(this);
        AppCompatActivity activity = GestureUtil.resolveContext(getContext());
        gestureActionChooserDialog.show(activity.getSupportFragmentManager(),"tag");
    }

    @Override
    public void onActionSelected(int action) {
        setValue(action);
    }
}
