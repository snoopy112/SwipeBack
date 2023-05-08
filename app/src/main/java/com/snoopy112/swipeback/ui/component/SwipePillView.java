package com.snoopy112.swipeback.ui.component;

import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
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

    private Context mContext;

    private ViewOnTouchListener mOnTouchListener;

    private TextView swipe_pill;

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

        swipe_pill = (TextView) swipePillView.findViewById(R.id.swipe_pill);
        swipe_pill.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                    return true;
                }
                return false;
            }
        });

        swipe_pill.setOnDragListener(new OnDragListener() {
            int y0, y1, dY, dT;
            long t0, t1;
            final int minDelta = 70;
            final int longSwipeTime = 600;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    int[] oldFolderCellPosition = new int[2];
                    v.getLocationOnScreen(oldFolderCellPosition);
                    y0 = oldFolderCellPosition[1];
                    Log.d(TAG, "y0: " + y0);
                    t0 = currentTimeMillis();
                    Log.d(TAG, "t0: " + t0);
                    return true;
                }
                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    y1 = (int) event.getY();
                    Log.d(TAG, "y1: " + y1);
                    if (y1 != 0) {
                        dY = y0 - y1;
                        if (dY >= minDelta) {
                            Log.d(TAG, "dY: " + dY);
                            t1 = currentTimeMillis();
                            Log.d(TAG, "t1: " + t1);
                            dT = (int) (t1 - t0);
                            Log.d(TAG, "dT: " + dT);
                            if (dT >= longSwipeTime) {
                                Log.d(TAG, "Long swipe detected");
                                //Toast.makeText(mContext, "Recent Apps", Toast.LENGTH_SHORT).show();
                                if (mOnTouchListener != null) {
                                    mOnTouchListener.onRecentTap();
                                }
                            } else {
                                Log.d(TAG, "Short swipe detected");
                                //Toast.makeText(mContext, "Back", Toast.LENGTH_SHORT).show();
                                if (mOnTouchListener != null) {
                                    mOnTouchListener.onBackTap();
                                }
                            }
                        }
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
        void onBackTap();
        void onHomeTap();
        void onRecentTap();
    }
}