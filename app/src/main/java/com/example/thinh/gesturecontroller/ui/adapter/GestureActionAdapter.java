package com.example.thinh.gesturecontroller.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thinh.gesturecontroller.R;

public class GestureActionAdapter extends RecyclerView.Adapter<GestureActionAdapter.GestureActionHolder> {

    private String[] mActionNames;
    private TypedArray mIcons;

    public interface OnActionSelectedListener {
        void onActionSelected(int action);
    }

    private OnActionSelectedListener mOnActionSelectedListener;

    public GestureActionAdapter(Context context, OnActionSelectedListener onActionSelectedListener) {
        mActionNames = context.getResources().getStringArray(R.array.list_gesture_action_name);
        mIcons = context.getResources().obtainTypedArray(R.array.list_gesture_action_icon);
        mOnActionSelectedListener = onActionSelectedListener;
    }

    @NonNull
    @Override
    public GestureActionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GestureActionHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gesture_action_items, viewGroup, false),mOnActionSelectedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GestureActionHolder gestureActionHolder, int i) {
        gestureActionHolder.bind(mActionNames[i], mIcons.getResourceId(i, 0), i);
    }

    @Override
    public int getItemCount() {
        return mActionNames.length;
    }


    static class GestureActionHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name;
        View layoutView;
        OnActionSelectedListener onActionSelectedListener;
        public GestureActionHolder(@NonNull View itemView, OnActionSelectedListener onActionSelectedListener) {
            super(itemView);
            name = itemView.findViewById(R.id.lbl_action_name);
            icon = itemView.findViewById(R.id.img_action_icon);
            layoutView = itemView;
            this.onActionSelectedListener = onActionSelectedListener;
        }

        void bind(String action, int resId, int index) {
            name.setText(action);
            icon.setImageResource(resId);
            layoutView.setOnClickListener(v ->{
                onActionSelectedListener.onActionSelected(index);
            });
        }
    }
}
