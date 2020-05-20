package com.example.thinh.gesturecontroller.ui.prefs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.ui.adapter.ColorAdapter;
import com.example.thinh.gesturecontroller.util.PrefUtil;

//Bkav QuangNDb dialog hien thi hop thoai chon color va opacity
public class ColorChooserDialog extends AppCompatDialogFragment implements ColorAdapter.OnColorSelectedListener {


    public interface OnColorSelectedListener {

        void onColorSelected(int newColor);
    }

    private static final String COLOR_CHOICES_KEY = "color_choices";
    private static final String SELECTED_COLOR_KEY = "selected_color";

    private RecyclerView mColorGrid;
    private View mPreview;
    private ColorAdapter mColorAdapter;
    private OnColorSelectedListener mColorSelectedListener;
    private int[] mColorChoices;
    private int mOpacity;
    private int mColor;
    private TextView mProgress;
    private SeekBar mOpacitySeekbar;
    //the color to be checked
    private int mSelectedColorValue;

    public static ColorChooserDialog newInstance(int[] colors, int selectedColorValue) {
        Bundle args = new Bundle();
        args.putIntArray(COLOR_CHOICES_KEY, colors);
        args.putInt(SELECTED_COLOR_KEY, selectedColorValue);
        ColorChooserDialog fragment = new ColorChooserDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mColorChoices = args.getIntArray(COLOR_CHOICES_KEY);
        mSelectedColorValue = args.getInt(SELECTED_COLOR_KEY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_colors, null);
        mPreview = rootView.findViewById(R.id.preview);
        mColorGrid = rootView.findViewById(R.id.color_grid);
        mColorAdapter = new ColorAdapter(this);
        mColorGrid.setAdapter(mColorAdapter);
        mColorAdapter.updateColor(mColorChoices);
        mOpacitySeekbar = rootView.findViewById(R.id.seekbar_opacity);
        mProgress = rootView.findViewById(R.id.lbl_progress);
        mOpacity = PrefUtil.getInt(getActivity(), getString(R.string.pref_opacity_key), 255);
        mOpacitySeekbar.setProgress(mOpacity);
        mColor = PrefUtil.getInt(getActivity(), getString(R.string.pref_color_key), ContextCompat.getColor(getActivity(), R.color.colorPrimaryAlternate));
        mPreview.setBackgroundColor(android.support.v4.graphics.ColorUtils.setAlphaComponent(mColor, mOpacity));
        mProgress.setText(String.valueOf(mOpacity));
        mOpacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Bkav QuangNDb save pref opacity
                PrefUtil.setInt(getActivity(), getString(R.string.pref_opacity_key), progress);
                mOpacity = progress;
                mProgress.setText(String.valueOf(progress));
                updateColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .create();
    }

    public void setOnColorSelectedListener(OnColorSelectedListener colorSelectedListener) {
        this.mColorSelectedListener = colorSelectedListener;
    }

    //Bkav QuangNDb goi tu thang ColorAdapter sang
    @Override
    public void onColorSelected(int newColor) {
        mColor = newColor;
        updateColor();
        mColorAdapter.notifyDataSetChanged();
    }

    //Bkav QuangNDb cap nhat color den cac ben lien quan
    private void updateColor() {
        final int finalColor = android.support.v4.graphics.ColorUtils.setAlphaComponent(mColor, mOpacity);
        mColorSelectedListener.onColorSelected(finalColor);
        mPreview.setBackgroundColor(finalColor);
    }
}
