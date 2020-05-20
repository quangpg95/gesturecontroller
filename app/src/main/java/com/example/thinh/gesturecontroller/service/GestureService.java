package com.example.thinh.gesturecontroller.service;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.thinh.gesturecontroller.controller.GestureViewController;

import java.io.IOException;

public class GestureService extends AccessibilityService {

    private GestureViewController mGestureWindowController;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mGestureWindowController = new GestureViewController(getBaseContext(), this);

    }

    // thinhavb: fake click xuong view duoi
    public static void clickAtPosition(int x, int y, AccessibilityNodeInfo node) {
        if (node == null) return;

        if (node.getChildCount() == 0) {
            Rect buttonRect = new Rect();
            node.getBoundsInScreen(buttonRect);
            if (buttonRect.contains(x, y)) {
                // Maybe we need to think if a large view covers item?
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                System.out.println("1º - Node Information: " + node.toString());
            }
        } else {
            Rect buttonRect = new Rect();
            node.getBoundsInScreen(buttonRect);
            if (buttonRect.contains(x, y)) {
                // Maybe we need to think if a large view covers item?
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                System.out.println("2º - Node Information: " + node.toString());
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                clickAtPosition(x, y, node.getChild(i));
            }
        }
    }

}
