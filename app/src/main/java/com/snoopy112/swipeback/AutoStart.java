package com.snoopy112.swipeback;

import static com.snoopy112.swipeback.MainActivity.isAccessibilityServiceEnabled;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;


public class AutoStart extends BroadcastReceiver
{
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent to = new Intent(context, TouchWindowService.class);
        if (isAccessibilityServiceEnabled(context, SwipeBackService.class) && Settings.canDrawOverlays(context))
            context.startService(to);
    }
}