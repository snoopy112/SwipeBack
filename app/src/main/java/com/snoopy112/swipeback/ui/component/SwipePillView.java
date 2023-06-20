package com.snoopy112.swipeback.ui.component;

import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
//import android.widget.Toast;

import com.snoopy112.swipeback.R;


public class SwipePillView extends FrameLayout {

    private static final String TAG = "SwipePillView";

    private final Context mContext;

    private ViewOnTouchListener mOnTouchListener;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private float x, y;

    private boolean isShadowEnabled = false;

    final int longSwipeTime = 600;
    final int minDelta = 70;

    public SwipePillView(Context context) {
        super(context);
        this.mContext = context;
        initView();

    }

    public SwipePillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public SwipePillView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View swipePillView = inflater.inflate(R.layout.swipe_pill_layout, null);
        this.addView(swipePillView);

        TextView swipe_pill = swipePillView.findViewById(R.id.swipe_pill);
        swipe_pill.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Log.d(TAG, "onTouch " + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDragAndDrop(data, shadowBuilder, view, DRAG_FLAG_OPAQUE);
                    isShadowEnabled = true;
                    x = event.getRawX();
                    y = event.getRawY();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isShadowEnabled)
                                view.cancelDragAndDrop();
                        }
                    }, longSwipeTime);
                    return true;
                }
                return false;
            }
        });

        swipe_pill.setOnDragListener(new OnDragListener() {
            int y0, y1, dY, dT;
            long t0, t1;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    int[] outLocation = new int[2];
                    v.getLocationOnScreen(outLocation);
                    y0 = outLocation[1];
                    Log.d(TAG, "y0: " + y0);
                    t0 = currentTimeMillis();
                    Log.d(TAG, "t0: " + t0);
                    return true;
                }
                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    isShadowEnabled = false;
                    y1 = (int) event.getY();
                    Log.d(TAG, "y1: " + y1);
                    dY = y0 - y1;
                    if (y1 != 0 && dY >= minDelta) {
                        Log.d(TAG, "dY: " + dY);
                        t1 = currentTimeMillis();
                        Log.d(TAG, "t1: " + t1);
                        dT = (int) (t1 - t0);
                        Log.d(TAG, "dT: " + dT);
                        if (dT >= longSwipeTime) {
                            Log.d(TAG, "Long swipe detected");
                            //Toast.makeText(mContext, "Recent Apps", Toast.LENGTH_SHORT).show();
                            if (mOnTouchListener != null)
                                mOnTouchListener.onRecentTap();
                        } else {
                            Log.d(TAG, "Short swipe detected");
                            //Toast.makeText(mContext, "Back", Toast.LENGTH_SHORT).show();
                            if (mOnTouchListener != null)
                                mOnTouchListener.onBackTap();
                        }
                    } else {
                        Log.d(TAG, String.format("onTap, x: %s, y: %s", x, y));
                        if (mOnTouchListener != null)
                            mOnTouchListener.onTap(x, y);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void setmOnTouchListener(ViewOnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }

    public interface ViewOnTouchListener {
        void onTap(float x, float y);
        void onBackTap();
        void onHomeTap();
        void onRecentTap();
    }
}