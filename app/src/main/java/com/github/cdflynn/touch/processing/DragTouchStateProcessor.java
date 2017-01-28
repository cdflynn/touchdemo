package com.github.cdflynn.touch.processing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.cdflynn.touch.util.Geometry;

public class DragTouchStateProcessor implements TouchProcessor {

    private static final int EVENT_HORIZON_DEFAULT = 10;
    private static final long ANIM_DURATION = 300L;

    private TouchState mTouchState;
    private TouchStateView mTouchStateView;
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private int mEventHorizon = EVENT_HORIZON_DEFAULT;
    private Point mAnchor1;
    private Point mAnchor2;
    private Path mPathToAnchor;
    private float[] mPoints = new float[2];

    private ValueAnimator mAnimator;
    private boolean mAnimating;
    private boolean mPointerIsDown;
    @Nullable
    private Point mClosestAnchor;
    @Nullable
    private Point mAnimationTarget;
    private float mAnimationDistance;

    public DragTouchStateProcessor(TouchState touchState, TouchStateView v) {
        mTouchState = touchState;
        mTouchStateView = v;
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setInterpolator(mInterpolator);
        mAnimator.setDuration(ANIM_DURATION);
        mPathToAnchor = new Path();
    }

    @Override
    public void onTouchEvent(View v, MotionEvent event) {

        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mPointerIsDown = false;
                if (!mAnimating) {
                    mTouchState.reset();
                }
                return;
            case MotionEvent.ACTION_DOWN:
                if (mAnimating) {
                    mAnimator.cancel();
                }
                mPointerIsDown = true;
                mTouchState.xDown = event.getX();
                mTouchState.yDown = event.getY();
                mTouchState.xDownRaw = event.getRawX();
                mTouchState.yDownRaw = event.getRawY();
                mClosestAnchor = getClosestAnchor(mTouchState.xDown, mTouchState.yDown);
                if (shouldAnimateTo(mClosestAnchor, mTouchState)) {
                    animateTo(mClosestAnchor);
                }
                return;
            case MotionEvent.ACTION_MOVE:
                mTouchState.xCurrent = event.getX();
                mTouchState.yCurrent = event.getY();
                mTouchState.xCurrentRaw = event.getRawX();
                mTouchState.yCurrentRaw = event.getRawY();
                if (!mAnimating) {
                    mTouchState.distance = Geometry.distance(mTouchState.xCurrent, mTouchState.yCurrent,
                            mTouchState.xDown, mTouchState.yDown);
                }
                mClosestAnchor = getClosestAnchor(mTouchState.xCurrent, mTouchState.yCurrent);
                if (shouldAnimateTo(mClosestAnchor, mTouchState)) {
                    animateTo(mClosestAnchor);
                }
        }
    }

    private void animateTo(Point p) {
        mAnimationTarget = p;
        mAnimating = true;
        mAnimationDistance = Geometry.distance(p.x, p.y, mTouchState.xDown, mTouchState.yDown);

        mPathToAnchor.reset();
        mPathToAnchor.moveTo(mTouchState.xDown, mTouchState.yDown);
        mPathToAnchor.lineTo(p.x, p.y);

        mAnimator.removeAllUpdateListeners();
        mAnimator.removeAllListeners();
        mAnimator.cancel();
        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
            }
        });
        mAnimator.start();
    }

    private final ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float fraction = animation.getAnimatedFraction();
            Geometry.setPointFromPercent(mPathToAnchor, mAnimationDistance, fraction, mPoints);
            mTouchState.xDown = mPoints[0];
            mTouchState.yDown = mPoints[1];
            if (mPointerIsDown) {
                mTouchState.distance = Geometry.distance(mTouchState.xCurrent, mTouchState.yCurrent, mPoints[0], mPoints[1]);
            } else {
                mTouchState.xCurrent = mTouchState.xDown;
                mTouchState.yCurrent = mTouchState.yDown;
                mTouchState.distance = 0;
            }
            mTouchStateView.drawTouchState(mTouchState);
        }
    };

    private Point getClosestAnchor(float x, float y) {
        final float d1 = Geometry.distance(x, y, mAnchor1.x, mAnchor1.y);
        final float d2 = Geometry.distance(x, y, mAnchor2.x, mAnchor2.y);

        if (Geometry.approximately(d1, d2, .1f)) {
            return mAnchor1;
        }

        if (d1 < d2) {
            return mAnchor1;
        } else {
            return mAnchor2;
        }
    }

    private boolean shouldAnimateTo(Point p, TouchState s) {
        if (mAnimating && (p.equals(mAnimationTarget))) {
            return false;
        }

        final float d = Geometry.distance(s.xCurrent, s.yCurrent, p.x, p.y);
        return d < mEventHorizon;
    }

    public void anchors(Point anchor1, Point anchor2) {
        mAnchor1 = anchor1;
        mAnchor2 = anchor2;
    }

    /**
     * The distance threshold to trigger a move animation on the mState.DOWN point
     */
    public void eventHorizon(int eventHorizon) {
        mEventHorizon = eventHorizon;
    }

    public void setTouchStateView(TouchStateView v) {
        mTouchStateView = v;
    }
}
