package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.github.cdflynn.touch.processing.SettleAnimator;
import com.github.cdflynn.touch.processing.TouchState;
import com.github.cdflynn.touch.processing.TouchStateView;
import com.github.cdflynn.touch.view.interfaces.MotionEventStream;

public class AnimatedBezierView extends BezierView implements MotionEventStream, TouchStateView {

    private static final int ANIMATION_DURATION_MS_DEFAULT = 150;
    private static final Interpolator INTERPOLATOR_DEFAULT = new AccelerateInterpolator(.5f);

    private SettleAnimator mSettleAnimator;

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
        mSettleAnimator = new SettleAnimator();
        mSettleAnimator.setDuration(ANIMATION_DURATION_MS_DEFAULT);
        mSettleAnimator.setInterpolator(INTERPOLATOR_DEFAULT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mSettleAnimator.start(mState, this);
                return super.onTouchEvent(event);
            default:
                mSettleAnimator.cancel();
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void drawTouchState(TouchState s) {
        calculatePath(mState);
        invalidate();
    }

    public void setAnimationDuration(int durationMs) {
        mSettleAnimator.setDuration(durationMs);
    }

    public void setInterpolator(@NonNull Interpolator interpolator) {
        mSettleAnimator.setInterpolator(interpolator);
    }
}
