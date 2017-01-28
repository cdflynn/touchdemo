package com.github.cdflynn.touch.view.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.DragTouchStateProcessor;
import com.github.cdflynn.touch.processing.TouchState;
import com.github.cdflynn.touch.processing.TouchStateView;

public class ReorderBezierView extends BezierView implements TouchStateView {

    private static final float ANCHOR_SIZE_MAX = 70;
    private static final float ANCHOR_SIZE_MIN = 5;
    private static final int ANCHOR_EVENT_HORIZON = 120;
    private static final long ANIM_DURATION = 2000L;

    private DragTouchStateProcessor mDragProcessor;
    private Point a1;
    private Point a2;
    private float mAnchorRadius = ANCHOR_SIZE_MIN;
    private Paint mPaint;
    @ColorInt
    private int mAnchorColorStart;
    @ColorInt
    private int mAnchorColorEnd;
    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private final ValueAnimator mAnchorAnimator = ValueAnimator.ofFloat(0, 1);
    private final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();

    public ReorderBezierView(Context context) {
        super(context);
        init();
    }

    public ReorderBezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReorderBezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ReorderBezierView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mDragProcessor = new DragTouchStateProcessor(mState, this);
        mDragProcessor.eventHorizon(ANCHOR_EVENT_HORIZON);
        setTouchProcessor(mDragProcessor);
        a1 = new Point();
        a2 = new Point();
        mPaint = createPaint();
        mAnchorColorStart = ContextCompat.getColor(getContext(), R.color.anchor);
        mAnchorColorEnd = ContextCompat.getColor(getContext(), R.color.transparent);
    }

    private Paint createPaint() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(ContextCompat.getColor(getContext(), R.color.anchor));
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setAntiAlias(true);
        p.setStrokeWidth(1f);
        return p;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            final int height = bottom - top;
            final int width = right - left;
            final boolean landscape = getResources().getBoolean(R.bool.is_landscape);
            if (landscape) {
                a1.x = width / 4;
                a1.y = height / 2;

                a2.x = (width / 4) * 3;
                a2.y = height / 2;
            } else {
                a1.x = width / 2;
                a1.y = height / 4;

                a2.x = width / 2;
                a2.y = (height / 4) * 3;
            }
            mDragProcessor.anchors(a1, a2);
            startAnchorAnimation();
        }
    }

    @Override
    public void drawTouchState(TouchState s) {
        calculatePath(s);
        mLastDownX = s.xDown;
        mLastDownY = s.yDown;
        invalidate();
    }

    private void startAnchorAnimation() {
        mAnchorAnimator.removeAllUpdateListeners();
        mAnchorAnimator.cancel();
        mAnchorAnimator.addUpdateListener(mAnchorAnimationUpdateListener);
        mAnchorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnchorAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnchorAnimator.setInterpolator(mDecelerateInterpolator);
        mAnchorAnimator.setDuration(ANIM_DURATION);
        mAnchorAnimator.start();
    }

    private final ValueAnimator.AnimatorUpdateListener mAnchorAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            final float fraction = animation.getAnimatedFraction();
            mAnchorRadius = ANCHOR_SIZE_MIN + ((ANCHOR_SIZE_MAX - ANCHOR_SIZE_MIN) * fraction);
            final int color = (int)mArgbEvaluator.evaluate(fraction, mAnchorColorStart, mAnchorColorEnd);
            mPaint.setColor(color);
            invalidate();
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(a1.x, a1.y, mAnchorRadius, mPaint);
        canvas.drawCircle(a2.x, a2.y, mAnchorRadius, mPaint);
    }
}
