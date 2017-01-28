package com.github.cdflynn.touch.processing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.cdflynn.touch.util.Geometry;

public class SettleAnimator implements TouchStateAnimator {

    private static final long DEFAULT_DURATION = 300L;

    private ValueAnimator mAnimator;
    private Interpolator mInterpolator = new AccelerateInterpolator();
    private long mAnimationDuration = DEFAULT_DURATION;
    private Path mLineToCenter = new Path();
    private PathMeasure mPathMeasure = new PathMeasure();

    @Override
    public void cancel() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    @Override
    public void setDuration(long durationMs) {
        mAnimationDuration = durationMs < 0L ? DEFAULT_DURATION : durationMs;
    }


    @Override
    public void setInterpolator(Interpolator i) {
        mInterpolator = i == null ? new AccelerateInterpolator() : i;
    }

    /**
     * Animate the curves from the current pointer position to the down position.
     */
    @Override
    public void start(final TouchState s, final TouchStateView view) {
        mLineToCenter.reset();
        mLineToCenter.moveTo(s.xCurrent, s.yCurrent);
        mLineToCenter.lineTo(s.xDown, s.yDown);
        final float xTo = s.xDown;
        final float yTo = s.yDown;
        final float[] points = new float[2];
        final float fromDistance = s.distance;
        // ...
        mAnimator = ValueAnimator.ofFloat(1f, 0f);
        mAnimator.setInterpolator(mInterpolator);
        mAnimator.setDuration(mAnimationDuration)
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (view == null) {
                            cancel();
                            return;
                        }
                        s.xDown = xTo;
                        s.yDown = yTo;
                        final float fraction = animation.getAnimatedFraction();
                        s.distance = (1 - fraction) * fromDistance;
                        Geometry.setPointFromPercent(mLineToCenter, fromDistance, fraction, points);
                        s.xCurrent = points[0];
                        s.yCurrent = points[1];
                        view.drawTouchState(s);
                    }
                });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                s.reset();
                if (view != null) {
                    view.drawTouchState(s);
                }
            }

            @Override
            public void onAnimationPause(Animator animation) {
                s.reset();
                if (view != null) {
                    view.drawTouchState(s);
                }
            }
        });
        mAnimator.start();
    }
}
