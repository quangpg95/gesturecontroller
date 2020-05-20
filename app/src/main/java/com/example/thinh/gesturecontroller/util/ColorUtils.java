package com.example.thinh.gesturecontroller.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ImageView;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.ui.prefs.fragment.ColorChooserDialog;

public class ColorUtils {

    //Bkav QuangNDb set drawable cho image view item color
    public static void setColorViewValue(ImageView imageView, int color, boolean selected) {
        Resources res = imageView.getContext().getResources();

        Drawable currentDrawable = imageView.getDrawable();
        GradientDrawable colorChoiceDrawable;
        if (currentDrawable instanceof GradientDrawable) {
            // Reuse drawable
            colorChoiceDrawable = (GradientDrawable) currentDrawable;
        } else {
            colorChoiceDrawable = new GradientDrawable();
            colorChoiceDrawable.setShape(GradientDrawable.OVAL);
        }

        // Set stroke to dark version of color
        int darkenedColor = Color.rgb(
                Color.red(color) * 192 / 256,
                Color.green(color) * 192 / 256,
                Color.blue(color) * 192 / 256);

        colorChoiceDrawable.setColor(color);
        colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, res.getDisplayMetrics()), darkenedColor);

        Drawable drawable = colorChoiceDrawable;
        if (selected) {
            VectorDrawable checkmark = (VectorDrawable) res.getDrawable(isColorDark(color)
                    ? R.drawable.ic_check_white_24dp
                    : R.drawable.ic_check_black_24dp);
//            checkmark.setGravity(Gravity.CENTER);
            drawable = new LayerDrawable(new Drawable[]{
                    colorChoiceDrawable,
                    checkmark});
        }

        imageView.setImageDrawable(drawable);
    }

    private static final int BRIGHTNESS_THRESHOLD = 150;
    //Bkav QuangNDb check mau toi
    private static boolean isColorDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }

    //Bkav QuangNDb lay chuoi mau luu trong xml(color.xml)
    public static int[] extractColorArray(@ArrayRes int arrayId, Context context) {
        String[] choicesString = context.getResources().getStringArray(arrayId);
        int[] choicesInt = context.getResources().getIntArray(arrayId);

        // If user uses color reference(i.e. @color/color_choice) in the array,
        // the choicesString contains null values. We use the choicesInt in such case.
        boolean isStringArray = choicesString[0] != null;
        int length = isStringArray ? choicesString.length : choicesInt.length;

        int[] colorChoices = new int[length];
        for (int i = 0; i < length; i++) {
            colorChoices[i] = isStringArray ? Color.parseColor(choicesString[i]) : choicesInt[i];
        }

        return colorChoices;
    }

    //Bkav QuangNDb hien thi dialog chon mau va opacity
    public static void showDialog(Context context, ColorChooserDialog.OnColorSelectedListener listener, int[] colorChoices, int selectedColorValue) {
        ColorChooserDialog fragment = ColorChooserDialog.newInstance(colorChoices, selectedColorValue);
        fragment.setOnColorSelectedListener(listener);
        AppCompatActivity activity = GestureUtil.resolveContext(context);
        fragment.show(activity.getSupportFragmentManager(),"tag");
    }

}
