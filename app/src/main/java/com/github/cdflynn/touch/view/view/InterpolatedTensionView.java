package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.InterpolatedTensionProcessor;
import com.github.cdflynn.touch.processing.TouchState;
import com.github.cdflynn.touch.processing.TouchStateTracker;

public class InterpolatedTensionView extends AnimatedBezierView {

    private InterpolatedTensionProcessor mTensionProcessor;

    private float mLastDownX = TouchState.NONE;
    private float mLastDownY = TouchState.NONE;
    private int mRadiusMin = 0;
    private int mRadiusMax = 0;
    private Paint mMinRadiusPaint = new Paint();
    private Paint mMaxRadiusPaint = new Paint();

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

    private void init(Context context) {
        mMaxRadiusPaint.setStyle(Paint.Style.STROKE);
        mMaxRadiusPaint.setStrokeWidth(2f);
        mMaxRadiusPaint.setAntiAlias(true);
        mMaxRadiusPaint.setColor(ContextCompat.getColor(context, R.color.textColorAccentError));

        mMinRadiusPaint.setStyle(Paint.Style.STROKE);
        mMinRadiusPaint.setStrokeWidth(2f);
        mMinRadiusPaint.setAntiAlias(true);
        mMinRadiusPaint.setColor(ContextCompat.getColor(context, R.color.textColorAccent));

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
        super.onDraw(canvas);
        if (mLastDownX != TouchState.NONE && mLastDownY != TouchState.NONE) {
            canvas.drawCircle(mLastDownX, mLastDownY, mRadiusMin, mMinRadiusPaint);
            canvas.drawCircle(mLastDownX, mLastDownY, mRadiusMax, mMaxRadiusPaint);
        }
    }
}
