package com.github.cdflynn.touch.view.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;

public class NoisyMotionEventView extends View {

    private static final long DURATION_MS = 300;
    private static final float Z_MIN = 3f;
    private static final float Z_MAX = 20f;

    public interface MotionEventListener {
        void onMotionEvent(MotionEvent e);
    }

    private MotionEventListener mListener;

    public NoisyMotionEventView(Context context) {
        super(context);
    }

    public NoisyMotionEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoisyMotionEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoisyMotionEventView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMotionEventListener(MotionEventListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener != null) {
            mListener.onMotionEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.animate().translationZ(Z_MIN)
                        .setDuration(DURATION_MS)
                        .start();
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_DOWN:
                this.animate().translationZ(Z_MAX)
                        .setDuration(DURATION_MS)
                        .start();
                return super.onTouchEvent(event);
            default:
                return super.onTouchEvent(event);
        }
    }
}
