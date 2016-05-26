package com.github.cdflynn.touch.view.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.cdflynn.touch.view.interfaces.MotionEventStream;

public class AnimatedBezierView extends BezierView implements MotionEventStream {

    private static final int ANIMATION_DURATION_MS_DEFAULT = 150;
    private static final Interpolator INTERPOLATOR_DEFAULT = new AccelerateInterpolator(.5f);

    private Path mLineToCenter;
    private PathMeasure mPathMeasure;
    private Interpolator mInterpolator = INTERPOLATOR_DEFAULT;
    private int mAnimationDuration = ANIMATION_DURATION_MS_DEFAULT;

    public AnimatedBezierView(Context context) {
        super(context);
        init();
    }

    public AnimatedBezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedBezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AnimatedBezierView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mLineToCenter = new Path();
        mPathMeasure = new PathMeasure();
        drawControlPoints(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // TODO: the touch elevator isn't getting called,
        // TODO: fix that somehow.
        switch(event.getAction()) {
            case MotionEvent.ACTION_UP:
                mLineToCenter.reset();
                mLineToCenter.moveTo(mState.xCurrent, mState.yCurrent);
                mLineToCenter.lineTo(mState.xDown, mState.yDown);
                settle();
                return false;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setAnimationDuration(int durationMs) {
        mAnimationDuration = durationMs;
    }

    public void setInterpolator(@NonNull Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * Animate the curves from the current pointer position to the down position.
     */
    private void settle() {
        final float[] points = new float[2];
        final float fromDistance = mState.distance;
        ValueAnimator v = ValueAnimator.ofFloat(1f, 0f);
        v.setInterpolator(mInterpolator);
        v.setDuration(mAnimationDuration)
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float fraction = animation.getAnimatedFraction();
                        mState.distance = (1 - fraction) * fromDistance;
                        setPointFromPercent(mLineToCenter, fromDistance, fraction, points);
                        mState.xCurrent = points[0];
                        mState.yCurrent = points[1];
                        calculatePath();
                        invalidate();
                    }
                });
        v.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mState.reset();
                invalidate();
            }

            @Override
            public void onAnimationPause(Animator animation) {
                mState.reset();
                invalidate();
            }
        });
        v.start();
    }

    /**
     * Given some path and its length, find the point ([x,y]) on that path at
     * the given percentage of length.  Store the result in {@code points}.
     * @param path any path
     * @param length the length of {@code path}
     * @param percent the percentage along the path's length to find a point
     * @param points a float array of length 2, where the coordinates will be stored
     */
    private void setPointFromPercent(Path path, float length, float percent, float[] points) {
        mPathMeasure.setPath(path, false);
        mPathMeasure.getPosTan(length * percent, points, null);
    }
}
