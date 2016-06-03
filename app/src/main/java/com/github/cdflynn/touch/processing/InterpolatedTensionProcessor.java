package com.github.cdflynn.touch.processing;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.cdflynn.touch.util.Geometry;

public class InterpolatedTensionProcessor implements TouchProcessor {

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

    public InterpolatedTensionProcessor(TouchStateTracker touchStateTracker, TouchState state) {
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

    public void setTension(@FloatRange(from = 0) float tension) {
        mTensionFactor = tension;
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
        final float interpolatedDistance = interpolatedDistance(mState.distance);
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

    private float interpolatedDistance(float realDistance) {
        if (realDistance < mMinRadius) {
            return realDistance;
        }

        final float radiusSurplus = realDistance - mMinRadius;
        final float tensionZone = mMaxRadius - mMinRadius;
        final float tensionZoneRequiredPullDistance = tensionZone * (mTensionFactor + 1);

        if (realDistance >= (tensionZoneRequiredPullDistance + mMinRadius)) {
            return mMaxRadius;
        }

        final float realProgress = radiusSurplus / tensionZoneRequiredPullDistance;
        final float interpolatedProgress = mInterpolator.getInterpolation(realProgress);
        return mMinRadius + (interpolatedProgress * tensionZone);
    }
}