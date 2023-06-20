package com.snoopy112.swipeback;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Switch startSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
    }

    private void initView() {
        Log.d(TAG, "initView");
        startSwitch = findViewById(R.id.start_switch);
        startSwitch.setChecked(isMyServiceRunning(TouchWindowService.class));

        TextView enableAccess = findViewById(R.id.enable_access);
        TextView enableOverlay = findViewById(R.id.enable_overlay);

        startSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startSwitch.isChecked()) startTaskService(); else stopTaskService();
            }
        });

        enableAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
            }
        });

        enableOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        startSwitch.setChecked(isMyServiceRunning(TouchWindowService.class));
    }


    private void startTaskService() {
        Log.d(TAG, "startTaskService");
        Intent to = new Intent(this, TouchWindowService.class);
        if (checkAccessibilityPerms() && checkOverlayPerms())
            startService(to);
    }

    private void stopTaskService(){
        Log.d(TAG, "stopTaskService");
        Intent to = new Intent(this, TouchWindowService.class);
        stopService(to);
    }

    private boolean checkOverlayPerms() {
        final int REQUEST_CODE = 1;
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> Service) {
        ComponentName expectedComponentName = new ComponentName(context, Service);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }
        return false;
    }

    private boolean checkAccessibilityPerms() {
        final int REQUEST_CODE = 1002;
        if (!isAccessibilityServiceEnabled(this, SwipeBackService.class)) {
            Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            startActivityForResult(intent, REQUEST_CODE);
            return false;
        }
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}