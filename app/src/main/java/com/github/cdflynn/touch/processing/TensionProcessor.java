package com.github.cdflynn.touch.processing;

import android.support.annotation.FloatRange;
import android.view.MotionEvent;
import android.view.View;

import com.github.cdflynn.touch.util.Geometry;

public class TensionProcessor implements TouchProcessor {

    private static final float DEFAULT_TENSION = .5f;

    private TouchState mState;
    private TouchStateTracker mTracker;
    private float mTensionFactor = DEFAULT_TENSION;

    public TensionProcessor(TouchStateTracker touchStateTracker, TouchState state) {
        mState = state;
        mTracker = touchStateTracker;
    }

    @Override
    public void onTouchEvent(View v, MotionEvent event) {
        mTracker.onTouchEvent(v, event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                return;
            default:
                break;
        }
        final float deltaX = mState.xCurrent - mState.xDown;
        final float deltaY = mState.yCurrent - mState.yDown;
        final float tensionDeltaX = deltaX * (1 - mTensionFactor);
        final float tensionDeltaY = deltaY * (1 - mTensionFactor);
        mState.xCurrent = tensionDeltaX + mState.xDown;
        mState.yCurrent = tensionDeltaY + mState.yDown;
        mState.distance = Geometry.distance(mState.xDown, mState.yDown, mState.xCurrent, mState.yCurrent);
    }

    public void setTension(@FloatRange(from = 0, to = 1) float tension) {
        mTensionFactor = tension;
    }
}
