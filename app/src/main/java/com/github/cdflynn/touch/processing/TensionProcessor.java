package com.github.cdflynn.touch.processing;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.IntRange;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.cdflynn.touch.util.Geometry;

public class TensionProcessor implements TouchProcessor {

    private static final int DEFAULT_MAX_RADIUS = 1400;
    private static final int DEFAULT_MIN_RADIUS = 100;
    private static final float DEFAULT_INTERPOLATION_FACTOR = 1.0f;
    private static final float DEFAULT_TENSION_FACTOR = .5f;

    private TouchState mState;
    private TouchStateTracker mTracker;
    private Interpolator mInterpolator = new DecelerateInterpolator(DEFAULT_INTERPOLATION_FACTOR);
    private int mMinRadius = DEFAULT_MIN_RADIUS;
    private int mMaxRadius = DEFAULT_MAX_RADIUS;
    private float mTensionFactor = DEFAULT_TENSION_FACTOR;

    public TensionProcessor(TouchStateTracker touchStateTracker, TouchState state) {
        mState = state;
        mTracker = touchStateTracker;
    }

    /**
     * Set the radius boundaries where the tension factor will be applied.  Touches between
     * the min and max radius will be subject to a tension multiplier based on the interpolation.
     */
    public void setRadii(@IntRange(from = 0) int min, int max) {
        if (min < 0) {
            throw new IllegalArgumentException("min radius must not be less than zero");
        }
        if (min > max) {
            throw new IllegalArgumentException("min radius must be less than max radius");
        }
        mMinRadius = min;
        mMaxRadius = max;
    }

    public void setTension(float tension) {
        mInterpolator = new DecelerateInterpolator(tension);
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
        final float interpolatedTension = interpolatedTension(deltaX, deltaY);
        final float interpolatedDistance = interpolatedDistance(deltaX, deltaY, interpolatedTension);
        Log.d("Collin", "tension factor = " + interpolatedTension + "  :::  interpolated distance = " + interpolatedDistance);
        interpolatedCurrent(mState, interpolatedDistance, mCoords);
        mState.xCurrent = mCoords[0];
        mState.yCurrent = mCoords[1];
        mState.distance = Geometry.distance(mState.xDown, mState.yDown, mState.xCurrent, mState.yCurrent);
    }

    private Path mPath = new Path();
    private PathMeasure mPathMeasure = new PathMeasure();
    private float[] mCoords = new float[2];

    private void interpolatedCurrent(TouchState s, float distance, float[] coords) {
        mPath.reset();
        mPath.moveTo(s.xDown, s.yDown);
        mPath.lineTo(s.xCurrent, s.yCurrent);
        mPathMeasure.setPath(mPath, false);
        mPathMeasure.getPosTan(distance, coords, null);
    }

    private float interpolatedDistance(float deltaX, float deltaY, float interpolatedTension) {
        float realRadius = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        if (realRadius < mMinRadius) {
            return realRadius;
        }

        final float radiusSurplus = realRadius - mMinRadius;

        if (realRadius > mMaxRadius) {
            return (mMaxRadius - mMinRadius) * interpolatedTension + mMinRadius;
        }

        return mMinRadius + (radiusSurplus * interpolatedTension);
    }

    private float interpolatedTension(float deltaX, float deltaY) {
        float realRadius = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        if (realRadius < mMinRadius) {
            return 1;
        }

        if (realRadius > mMaxRadius) {
            return mTensionFactor;
        }

        final float radiusSurplus = realRadius - mMinRadius;
        final float radiusSurplusPercentage = Math.min(1, radiusSurplus/ (mMaxRadius - mMinRadius));
        final float tensionRange = 1-mTensionFactor;
        final float interpolation = mInterpolator.getInterpolation(radiusSurplusPercentage);
        return  1 - (interpolation * tensionRange);
    }
}