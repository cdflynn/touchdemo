package com.github.cdflynn.touch.view.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.InterpolatedTensionProcessor;
import com.github.cdflynn.touch.processing.TouchState;
import com.github.cdflynn.touch.processing.TouchStateTracker;

import butterknife.Bind;

public class ShuffleView extends FrameLayout {

    private static final float TENSION = 1.5f;
    private static final int RADIUS_MIN = 200;
    private static final int RADIUS_MAX = 350;
    private static final float RADIUS_THRESHOLD = ((float)(RADIUS_MAX - RADIUS_MIN) * .75f) + RADIUS_MIN;
    private static final long ELEVATION_ANIMATION_DURATION = 200L;
    private static final int CARD_A = 1;
    private static final int CARD_B = 2;

    static class Views extends BaseViews {

        @Bind(R.id.card_b)
        View cardB;
        @Bind(R.id.card_a)
        View cardA;
        FrameLayout root;

        Views(View root) {
            super(root);
            this.root = (FrameLayout) root;
        }
    }

    private Views mViews;
    private TouchState mState;
    private InterpolatedTensionProcessor mTouchProcessor;
    private Rect mRestingCoords;
    private float mCardOffset;
    private boolean mDownInsideBounds = false;
    private int mCurrentCard = CARD_A;
    private float mXOffset;
    private float mYOffset;
    private float mElevationLow;
    private float mElevationMid;
    private float mElevationHigh;
    private ValueAnimator mElevationMidToLow;
    private ValueAnimator mElevationMidToHigh;
    private ValueAnimator mElevationHighToMid;
    private ValueAnimator mElevationLowToMid;
    private ValueAnimator mElevationLowToHigh;

    public ShuffleView(Context context) {
        super(context);
        init(context);
    }

    public ShuffleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShuffleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ShuffleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_shuffle, this);
        mState = new TouchState();
        mCardOffset = context.getResources().getDimension(R.dimen.card_a_translation);
        mTouchProcessor = new InterpolatedTensionProcessor(new TouchStateTracker(mState), mState);
        mTouchProcessor.setRadii(RADIUS_MIN, RADIUS_MAX);
        mTouchProcessor.setTension(TENSION);
        mRestingCoords = new Rect();
        mElevationLow = getResources().getDimension(R.dimen.elevation_small);
        mElevationMid = getResources().getDimension(R.dimen.elevation_medium);
        mElevationHigh = getResources().getDimension(R.dimen.elevation_large);
        mViews = new Views(this);
        mViews.cardB.setZ(mElevationLow);
        mViews.cardA.setZ(mElevationMid);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mRestingCoords.set(mViews.cardA.getLeft(),
                    mViews.cardA.getTop(),
                    mViews.cardA.getRight(),
                    mViews.cardA.getBottom());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN
                && insideRestingRect(ev)) {
            mXOffset = ev.getX() - mRestingCoords.left;
            mYOffset = ev.getY() - mRestingCoords.top;
            mDownInsideBounds = true;
        } else if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mDownInsideBounds = false;
            settle();
            mState.reset();
            return super.onInterceptTouchEvent(ev);
        }

        mTouchProcessor.onTouchEvent(this, ev);
        drawState(mState);
        return super.onInterceptTouchEvent(ev);
    }

    private float lastDistance = 0;
    private boolean didPassThreshold = false;

    private void drawState(TouchState s) {
        if (!mDownInsideBounds) {
            return;
        }
        float toX = s.xCurrent;
        float toY = s.yCurrent;

        if (toX == TouchState.NONE || toY == TouchState.NONE) {
            toX = s.xDown;
            toY = s.yDown;
            didPassThreshold = false;
        }

        final View currentCard = mCurrentCard == CARD_A ? mViews.cardA : mViews.cardB;
        final View otherCard = mCurrentCard == CARD_A ? mViews.cardB : mViews.cardA;

        if (almostEqual(s.distance, RADIUS_MAX, .01f) && lastDistance < (RADIUS_MAX - .05f)) {
            didPassThreshold = true;
            if (mElevationMidToLow != null) {
                mElevationMidToLow.cancel();
            }
            if (mElevationLowToHigh != null) {
                mElevationLowToHigh.cancel();
            }
            mElevationMidToLow = ValueAnimator.ofFloat(mViews.cardA.getZ(), mElevationLow);
            mElevationMidToLow.setDuration(ELEVATION_ANIMATION_DURATION);
            mElevationMidToLow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentCard.setZ((float) animation.getAnimatedValue());
                }
            });
            mElevationMidToLow.start();

            mElevationLowToHigh = ValueAnimator.ofFloat(mViews.cardB.getZ(), mElevationHigh);
            mElevationLowToHigh.setDuration(ELEVATION_ANIMATION_DURATION);
            mElevationLowToHigh.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    otherCard.setZ((float) animation.getAnimatedValue());
                }
            });
            mElevationLowToHigh.start();
            lastDistance = RADIUS_MAX;

        } else {
            lastDistance = s.distance;
        }

        currentCard.animate()
                .x(toX - mXOffset)
                .y(toY - mYOffset)
                .setDuration(0)
                .start();
    }

    private void settle() {
        View currentCard = mCurrentCard == CARD_A ? mViews.cardA : mViews.cardB;
        View otherCard = mCurrentCard == CARD_A ? mViews.cardB: mViews.cardA;
        float currentCardRestingX;
        float currentCardRestingY;
        float currentCardRestingZ;
        float otherCardRestingX;
        float otherCardRestingY;
        float otherCardRestingZ;

        if (didPassThreshold) {
            currentCardRestingX = mRestingCoords.left - 2 * mCardOffset;
            currentCardRestingY = mRestingCoords.top - 2 * mCardOffset;
            currentCardRestingZ = mElevationLow;
            otherCardRestingX = mRestingCoords.left;
            otherCardRestingY = mRestingCoords.top;
            otherCardRestingZ = mElevationMid;
        } else {
            currentCardRestingX = mRestingCoords.left;
            currentCardRestingY = mRestingCoords.top;
            currentCardRestingZ = mElevationMid;
            otherCardRestingX = mRestingCoords.left - 2 * mCardOffset;
            otherCardRestingY = mRestingCoords.top - 2 * mCardOffset;
            otherCardRestingZ = mElevationLow;
        }

        currentCard.animate()
                .x(currentCardRestingX)
                .y(currentCardRestingY)
                .z(currentCardRestingZ)
                .setDuration(200)
                .start();

        otherCard.animate()
                .x(otherCardRestingX)
                .y(otherCardRestingY)
                .z(otherCardRestingZ)
                .setDuration(200)
                .start();
        if (didPassThreshold) {
            mCurrentCard = mCurrentCard == CARD_A ? CARD_B : CARD_A;
        }
    }

    public boolean insideRestingRect(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        return mRestingCoords.contains((int)x, (int)y);
    }

    private void toggleElevation(View clicked) {
        View other = clicked.getId() == mViews.cardB.getId() ? mViews.cardA
                : mViews.cardB;
        other.bringToFront();
        mViews.root.invalidate();
        clicked.animate()
                .translationZ(mElevationLow)
                .setDuration(250)
                .start();
        other.animate()
                .translationZ(mElevationMid)
                .setDuration(250)
                .start();
    }

    public static boolean almostEqual(float firstValue,float secondValue,float epsilon){
        float difference = Math.abs(firstValue-secondValue);
        return difference <= epsilon;
    }
}
