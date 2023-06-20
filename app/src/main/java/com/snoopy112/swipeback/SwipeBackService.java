package com.snoopy112.swipeback;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import com.snoopy112.swipeback.ui.component.TouchViewManager;


public class SwipeBackService extends AccessibilityService implements TouchViewManager.KeyServiceListener {

    private static final String TAG = "SwipeBackService";

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        super.onServiceConnected();
        TouchViewManager.getInstance().setKeyServiceListener(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void tapOnScreen(float x, float y) {
        Log.d(TAG, "tapOnScreen");
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription clickStroke =
                new GestureDescription.StrokeDescription(clickPath, 10, 1);
        GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
        clickBuilder.addStroke(clickStroke);
        this.dispatchGesture(clickBuilder.build(), null, null);
    }

    @Override
    public void backKey() {
        Log.d(TAG, "backKey");
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void homeKey() {
        Log.d(TAG, "homeKey");
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    @Override
    public void recentKey() {
        Log.d(TAG, "recentKey");
        performGlobalAction(GLOBAL_ACTION_RECENTS);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Intent to = new Intent(this, TouchWindowService.class);
        stopService(to);
        super.onDestroy();
    }
}