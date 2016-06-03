package com.github.cdflynn.touch.view.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.InterpolatedTensionProcessor;
import com.github.cdflynn.touch.processing.TouchState;
import com.github.cdflynn.touch.processing.TouchStateTracker;

public class InterpolatedTensionView extends AnimatedBezierView {

    @ColorRes
    private static final int TENSION_NONE = R.color.colorAccent;
    @ColorRes
    private static final int TENSION_START = R.color.textColorAccent;
    @ColorRes
    private static final int TENSION_END = R.color.textColorAccentError;

    private InterpolatedTensionProcessor mTensionProcessor;

    private float mLastDownX = TouchState.NONE;
    private float mLastDownY = TouchState.NONE;
    private int mRadiusMin = 0;
    private int mRadiusMax = 0;
    private Paint mMinRadiusPaint = new Paint();
    private Paint mMaxRadiusPaint = new Paint();
    private ArgbEvaluator mArgbEvaluator;
    @ColorInt
    private int mNoTensionColor;
    @ColorInt
    private int mStartTensionColor;
    @ColorInt
    private int mEndTensionColor;


    public InterpolatedTensionView(Context context) {
        super(context);
        init(context);
    }

    public InterpolatedTensionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InterpolatedTensionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public InterpolatedTensionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Paint createRadiusPaint(@ColorInt int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2f);
        p.setAntiAlias(true);
        p.setColor(color);
        return p;
    }

    private void init(Context context) {
        mNoTensionColor = ContextCompat.getColor(context, TENSION_NONE);
        mStartTensionColor = ContextCompat.getColor(context, TENSION_START);
        mEndTensionColor = ContextCompat.getColor(context, TENSION_END);
        mMinRadiusPaint = createRadiusPaint(mStartTensionColor);
        mMaxRadiusPaint = createRadiusPaint(mEndTensionColor);
        mArgbEvaluator = new ArgbEvaluator();
        mTensionProcessor = new InterpolatedTensionProcessor(new TouchStateTracker(mState), mState);
        setTouchProcessor(mTensionProcessor);
    }

    public void setRadii(int min, int max) {
        mRadiusMin = min;
        mRadiusMax = max;
        mTensionProcessor.setRadii(min, max);
        invalidate();
    }

    public void setTension(float tension) {
        mTensionProcessor.setTension(tension);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastDownX = event.getX();
            mLastDownY = event.getY();
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            mLastDownX = TouchState.NONE;
            mLastDownY = TouchState.NONE;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setColor(getInterpolatedColor(mState));
        super.onDraw(canvas);
        if (mLastDownX != TouchState.NONE && mLastDownY != TouchState.NONE) {
            canvas.drawCircle(mLastDownX, mLastDownY, mRadiusMin, mMinRadiusPaint);
            canvas.drawCircle(mLastDownX, mLastDownY, mRadiusMax, mMaxRadiusPaint);
        }
    }

    @ColorInt
    private int getInterpolatedColor(TouchState s) {
        if (s.distance == TouchState.NONE) {
            Log.d("collin", "NONE");
            return mNoTensionColor;
        }

        if (mRadiusMin == mRadiusMax) {
            Log.d("collin", "Max Tension");
            return (int) mArgbEvaluator.evaluate((s.distance/mRadiusMax), mNoTensionColor, mEndTensionColor);
        }

        if (s.distance <= mRadiusMin) {
            Log.d("collin", "No Tension");
            return (int) mArgbEvaluator.evaluate((s.distance/mRadiusMin), mNoTensionColor, mStartTensionColor);
        }

        Log.d("collin", "Interpolated tension");
        final float fractionalDistance = (s.distance - mRadiusMin)/(mRadiusMax - mRadiusMin);
        return (int)mArgbEvaluator.evaluate(fractionalDistance, mStartTensionColor, mEndTensionColor);

    }
}
