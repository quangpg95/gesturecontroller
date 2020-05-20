package com.example.thinh.gesturecontroller.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.util.ColorUtils;
import com.example.thinh.gesturecontroller.util.PrefUtil;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorHolder> {
    public interface OnColorSelectedListener {
        void onColorSelected(int newColor);
    }
    private OnColorSelectedListener mColorSelectedListener;
    private int[] mColors = new int[]{};
    public void updateColor(int[] colors) {
        mColors = colors;
        notifyDataSetChanged();
    }

    public ColorAdapter(OnColorSelectedListener colorSelectedListener) {
        this.mColorSelectedListener = colorSelectedListener;
    }

    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_color
                , viewGroup, false);
        return new ColorHolder(view, mColorSelectedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorHolder colorHolder, int i) {
        colorHolder.bind(mColors[i]);
    }

    @Override
    public int getItemCount() {
        return mColors.length;
    }


    static class ColorHolder extends RecyclerView.ViewHolder {

        private ImageView mColor;
        private OnColorSelectedListener mColorSelectedListener;
        private Context mContext;

        public ColorHolder(@NonNull View itemView, final OnColorSelectedListener colorSelectedListener) {
            super(itemView);
            mColor = itemView.findViewById(R.id.color_view);
            mColorSelectedListener = colorSelectedListener;
            mContext = itemView.getContext();
        }

        void bind(final int color) {
            int currentPrefColor = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_color_key), ContextCompat.getColor(mContext,R.color.colorPrimaryAlternate));
            int colorWithOpacity = android.support.v4.graphics.ColorUtils.setAlphaComponent(color, PrefUtil.getInt(mContext, mContext.getString(R.string.pref_opacity_key), 0));
            if (currentPrefColor == colorWithOpacity) {
                ColorUtils.setColorViewValue(mColor, color, true);
            }else {
                ColorUtils.setColorViewValue(mColor, color, false);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mColorSelectedListener.onColorSelected(color);
                }
            });
        }

    }
}
