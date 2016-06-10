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

    private static final float TENSION = 1.1f;
    private static final float AFFORDANCE = .05f;
    private static final int RADIUS_MIN = 100;
    private static final int RADIUS_MAX = 350;
    private static final long SETTLE_ANIMATION_DURATION = 200L;
    private static final long ELEVATION_ANIMATION_DURATION = 200L;
    private static final int FLAG_CARD_A = 1;
    private static final int FLAG_CARD_B = 2;

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
    /**
     * The resting coordinates of the top card.
     */
    private Rect mRestingBoundary;
    /**
     * The absolute value amount that each card is shifted [x,y] so that they're not
     * stacked right on top of each other.
     */
    private float mCardOffset;
    /**
     * Extra touch tracking variable.  Remembers the last known drag distance.
     */
    private float mLastDistance = 0;
    /**
     * Has the current gesture ever passed the radius threshold to swap the cards?
     */
    private boolean mDidPassThreshold = false;
    /**
     * Was the last observed {@link MotionEvent#ACTION_DOWN} inside the top card's boundary?
     */
    private boolean mDownInsideBounds = false;
    /**
     * Flag to indicate which card is on top.
     */
    private int mTopCardFlag = FLAG_CARD_A;
    private float mXOffset;
    private float mYOffset;
    private float mElevationLow;
    private float mElevationMid;
    private float mElevationHigh;

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
        mRestingBoundary = new Rect();
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
            mRestingBoundary.set(mViews.cardA.getLeft(),
                    mViews.cardA.getTop(),
                    mViews.cardA.getRight(),
                    mViews.cardA.getBottom());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN && insideRestingRect(ev)) {
            mXOffset = ev.getX() - mRestingBoundary.left;
            mYOffset = ev.getY() - mRestingBoundary.top;
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

    private void drawState(TouchState s) {
        if (!mDownInsideBounds) {
            return;
        }
        float toX = s.xCurrent;
        float toY = s.yCurrent;

        if (toX == TouchState.NONE || toY == TouchState.NONE) {
            toX = s.xDown;
            toY = s.yDown;
            mDidPassThreshold = false;
        }

        final View topCard = getTopCard();
        final View bottomCard = getBottomCard();

        if (approximately(s.distance, RADIUS_MAX, AFFORDANCE)
                && mLastDistance < (RADIUS_MAX - AFFORDANCE)) {
            mDidPassThreshold = true;
            // use some value animators instead of View.animate() so we don't
            // conflict with the translation x/y View.animate() call on the top card.
            ValueAnimator topAnimator = ValueAnimator.ofFloat(mViews.cardA.getZ(), mElevationLow)
                    .setDuration(ELEVATION_ANIMATION_DURATION);
            topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    topCard.setZ((float) animation.getAnimatedValue());
                }
            });
            topAnimator.start();

            ValueAnimator bottomAnimator = ValueAnimator.ofFloat(mViews.cardB.getZ(), mElevationHigh)
                    .setDuration(ELEVATION_ANIMATION_DURATION);
            bottomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bottomCard.setZ((float) animation.getAnimatedValue());
                }
            });
            bottomAnimator.start();
            mLastDistance = RADIUS_MAX;
        } else {
            mLastDistance = s.distance;
        }

        topCard.animate()
                .x(toX - mXOffset)
                .y(toY - mYOffset)
                .setDuration(0)
                .start();
    }

    /**
     * Move child views to their resting positions.
     */
    private void settle() {
        final View topCard = getTopCard();
        final View bottomCard = getBottomCard();
        float topCardRestingX;
        float topCardRestingY;
        float topCardRestingZ;
        float bottomCardRestingX;
        float bottomCardRestingY;
        float bottomCardRestingZ;

        if (mDidPassThreshold) {
            topCardRestingX = mRestingBoundary.left - 2 * mCardOffset;
            topCardRestingY = mRestingBoundary.top - 2 * mCardOffset;
            topCardRestingZ = mElevationLow;
            bottomCardRestingX = mRestingBoundary.left;
            bottomCardRestingY = mRestingBoundary.top;
            bottomCardRestingZ = mElevationMid;
        } else {
            topCardRestingX = mRestingBoundary.left;
            topCardRestingY = mRestingBoundary.top;
            topCardRestingZ = mElevationMid;
            bottomCardRestingX = mRestingBoundary.left - 2 * mCardOffset;
            bottomCardRestingY = mRestingBoundary.top - 2 * mCardOffset;
            bottomCardRestingZ = mElevationLow;
        }

        topCard.animate()
                .x(topCardRestingX)
                .y(topCardRestingY)
                .z(topCardRestingZ)
                .setDuration(SETTLE_ANIMATION_DURATION)
                .start();

        bottomCard.animate()
                .x(bottomCardRestingX)
                .y(bottomCardRestingY)
                .z(bottomCardRestingZ)
                .setDuration(SETTLE_ANIMATION_DURATION)
                .start();
        if (mDidPassThreshold) {
            mTopCardFlag = mTopCardFlag == FLAG_CARD_A ? FLAG_CARD_B : FLAG_CARD_A;
        }
    }

    private boolean insideRestingRect(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        return mRestingBoundary.contains((int) x, (int) y);
    }

    /**
     * Get whichever card is on top right now.
     */
    private View getTopCard() {
        return mTopCardFlag == FLAG_CARD_A ? mViews.cardA : mViews.cardB;
    }

    /**
     * Get whichever card is on bottom right now.
     */
    private View getBottomCard() {
        return mTopCardFlag == FLAG_CARD_A ? mViews.cardB : mViews.cardA;
    }

    /**
     * Check if x and y are within affordance of each other.
     */
    private static boolean approximately(float x, float y, float affordance) {
        float difference = Math.abs(x - y);
        return difference <= affordance;
    }
}
