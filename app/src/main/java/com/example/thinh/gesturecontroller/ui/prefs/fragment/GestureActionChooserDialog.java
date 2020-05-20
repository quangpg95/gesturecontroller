package com.example.thinh.gesturecontroller.ui.prefs.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.factory.Factory;
import com.example.thinh.gesturecontroller.ui.adapter.GestureActionAdapter;

public class GestureActionChooserDialog extends AppCompatDialogFragment implements GestureActionAdapter.OnActionSelectedListener {

    public interface OnActionSelectedListener {
        void onActionSelected(int action);
    }

    private RecyclerView mActionList;
    private GestureActionAdapter mActionAdapter;
    private OnActionSelectedListener mOnActionSelectedListener;

    public static GestureActionChooserDialog newInstance() {
        
        Bundle args = new Bundle();
        
        GestureActionChooserDialog fragment = new GestureActionChooserDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View rootView = layoutInflater.inflate(R.layout.dialog_guesture_action, null);
        mActionList = rootView.findViewById(R.id.list_action);
        mActionAdapter = new GestureActionAdapter(Factory.get().getApplicationContext(),this);
        mActionList.setAdapter(mActionAdapter);
        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .create();
    }

    public void setOnActionSelectedListener(OnActionSelectedListener onActionSelectedListener) {
        this.mOnActionSelectedListener = onActionSelectedListener;
    }

    @Override
    public void onActionSelected(int action) {
        mOnActionSelectedListener.onActionSelected(action);
        dismiss();
    }
}
