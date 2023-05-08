package com.snoopy112.swipeback;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
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