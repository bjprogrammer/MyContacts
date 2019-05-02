package com.bobby.mycontacts.utils;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;

public class HelperFunctions {
    public static Flashbar.Builder setSnackbar(String title, String message, int icon, Activity activity, int background) {

        return new Flashbar.Builder(activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title(title)
                .titleColor(Color.WHITE)
                .message(message)
                .messageColor(Color.WHITE)
                .enterAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(450)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(activity)
                        .animateBar()
                        .duration(250)
                        .slideFromLeft()
                        .accelerate())
                .backgroundColor(background)
                .showOverlay()
                .overlayBlockable()
                .showIcon(1.0f, ImageView.ScaleType.CENTER_CROP)
                .icon(icon);
    }
}