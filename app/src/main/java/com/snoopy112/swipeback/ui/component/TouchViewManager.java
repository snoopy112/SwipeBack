package com.snoopy112.swipeback.ui.component;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import com.snoopy112.swipeback.AppContext;


public class TouchViewManager {

    private static final String TAG = "TouchViewManager";

    private static TouchViewManager touchViewManager = null;

    private TouchViewManager() {
        init();
    }

    public static TouchViewManager getInstance() {
        if (touchViewManager == null) {
            synchronized (TouchViewManager.class) {
                if (touchViewManager == null)
                    touchViewManager = new TouchViewManager();
            }
        }
        return touchViewManager;
    }

    private WindowManager.LayoutParams swipePillLayoutParams = null;
    private WindowManager windowManager = null;
    private SwipePillView swipePillView = null;

    private KeyServiceListener keyServiceListener;


    private void init() {
        swipePillLayoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) AppContext.appContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void removeView() {
        windowManager.removeView(swipePillView);
    }

    public void showView() {
        Log.d(TAG, "showView");
        setLayoutParams();
        showSwipePillView();
    }

    private void setLayoutParams() {
        swipePillLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        swipePillLayoutParams.format = PixelFormat.TRANSLUCENT;
        swipePillLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        swipePillLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        swipePillLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        swipePillLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void showSwipePillView() {
        if (swipePillView == null)
            createSwipePillView();

        windowManager.addView(swipePillView, swipePillLayoutParams);
    }

    private void createSwipePillView() {
        swipePillView = new SwipePillView(AppContext.appContext);
        swipePillView.setmOnTouchListener(viewOnTouchListener);
    }


    private final SwipePillView.ViewOnTouchListener viewOnTouchListener = new SwipePillView.ViewOnTouchListener() {

        @Override
        public void onTap(float x, float y) {
            windowManager.removeView(swipePillView);

            if (keyServiceListener != null)
                keyServiceListener.tapOnScreen(x, y);

            windowManager.addView(swipePillView, swipePillLayoutParams);
        }

        @Override
        public void onBackTap() {
            if (keyServiceListener != null)
                keyServiceListener.backKey();
        }

        @Override
        public void onHomeTap() {
            if (keyServiceListener != null)
                keyServiceListener.homeKey();
        }

        @Override
        public void onRecentTap() {
            if (keyServiceListener != null)
                keyServiceListener.recentKey();
        }
    };

    public void setKeyServiceListener(KeyServiceListener keyServiceListener) {
        this.keyServiceListener = keyServiceListener;
    }

    public interface KeyServiceListener {
        void tapOnScreen(float x, float y);
        void backKey();
        void homeKey();
        void recentKey();
    }
}