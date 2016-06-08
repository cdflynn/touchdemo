package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.OnTouchElevator;
import com.github.cdflynn.touch.processing.TouchState;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.interfaces.MotionEventStream;

/**
 * A {@link MotionEventStream} that filters out motions events that fall within
 * {@link ViewConfiguration#getScaledTouchSlop()}.
 */
public class TouchSlopMotionEventView extends View implements MotionEventStream {

    private float mLastDownX = TouchState.NONE;
    private float mLastDownY = TouchState.NONE;
    private MotionEventListener mListener;
    private OnTouchElevator mOnTouchElevator;
    private TouchState mState;
    private Paint mPaint;
    private int mScaledTouchSlop;
    private int mAdditionalTouchSlop;

    public TouchSlopMotionEventView(Context context) {
        super(context);
        init(context);
    }

    public TouchSlopMotionEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchSlopMotionEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TouchSlopMotionEventView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mOnTouchElevator = new OnTouchElevator();
        mState = new TouchState();
        mPaint = createPaint();
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public void setMotionEventListener(MotionEventListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mOnTouchElevator.onTouchEvent(this, event);

        if (mListener == null) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mState.reset();
                mListener.onMotionEvent(event);
                break;
            case MotionEvent.ACTION_DOWN:
                mState.xDown = event.getRawX();
                mState.yDown = event.getRawY();
                mLastDownX = event.getX();
                mLastDownY = event.getY();
                mListener.onMotionEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                final float distance = distance(mState.xDown, mState.yDown, event.getRawX(), event.getRawY());
                final float slop = mScaledTouchSlop + mAdditionalTouchSlop;
                if (distance > slop) {
                    mListener.onMotionEvent(event);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * For demonstration purposes, add some extra padding to the {@link ViewConfiguration#getScaledTouchSlop()}
     */
    public void setAdditionalTouchSlop(@IntRange(from = 0, to = 100) int additionalSlop) {
        if (additionalSlop < 0) {
            mAdditionalTouchSlop = 0;
        } else if (additionalSlop > 100) {
            mAdditionalTouchSlop = 100;
        } else {
            mAdditionalTouchSlop = additionalSlop;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLastDownX != TouchState.NONE && mLastDownY != TouchState.NONE) {
            canvas.drawCircle(mLastDownX, mLastDownY, (mScaledTouchSlop + mAdditionalTouchSlop), mPaint);
        }
    }

    private static float distance(float xDown, float yDown, float xCurrent, float yCurrent) {
        final float xAbs = Math.abs(xDown - xCurrent);
        final float yAbs = Math.abs(yDown - yCurrent);
        return (float) Math.sqrt((yAbs * yAbs) + (xAbs * xAbs));
    }

    private Paint createPaint() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        p.setStrokeWidth(3f);
        return p;
    }
}
