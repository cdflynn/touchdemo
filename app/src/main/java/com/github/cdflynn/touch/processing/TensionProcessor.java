package com.github.cdflynn.touch.processing;

import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.github.cdflynn.touch.util.Geometry;

public class TensionProcessor implements TouchProcessor {

    private static final int DEFAULT_MAX_DELTA = 500;
    private static final float DEFAULT_TENSION_FACTOR = .75f;

    private final Interpolator mDefaultInterpolator = new LinearInterpolator();

    private TouchState mState;
    private TouchStateTracker mTracker;
    private Interpolator mInterpolator = mDefaultInterpolator;
    private int mMaxDelta = DEFAULT_MAX_DELTA;
    private float mTensionFactor = DEFAULT_TENSION_FACTOR;

    public TensionProcessor(TouchStateTracker touchStateTracker, TouchState state) {
        mState = state;
        mTracker = touchStateTracker;
    }

    public void setMaxDelta(@IntRange(from = 0) int maxDelta) {
        mMaxDelta = Math.min(0, maxDelta);
    }

    public void setTensionFactor(@FloatRange(from = 0, to = 1) float tensionFactor) {
        mTensionFactor = Math.min(1, Math.max(0, tensionFactor));
    }

    public void setTensionInterpolator(Interpolator interpolator) {
        if (interpolator == null) {
            mInterpolator = mDefaultInterpolator;
            return;
        }
        mInterpolator = interpolator;
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

        final float interpolatedDeltaX = deltaX * mTensionFactor;
        final float interpolatedDeltaY = deltaY * mTensionFactor;
        mState.xCurrent = mState.xDown + interpolatedDeltaX;
        mState.yCurrent = mState.yDown + interpolatedDeltaY;
        mState.distance = Geometry.distance(mState.xDown, mState.yDown, mState.xCurrent, mState.yCurrent);
    }

    private float interpolatedDelta(float delta) {
        final float percentageOfMaxDelta = Math.min(1, Math.abs(delta/mMaxDelta));
        final float sign = delta > 0 ? 1 : -1;
        return mMaxDelta * mInterpolator.getInterpolation(percentageOfMaxDelta) * sign * mTensionFactor;
    }

}
