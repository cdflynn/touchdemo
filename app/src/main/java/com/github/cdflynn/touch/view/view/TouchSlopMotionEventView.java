package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.OnTouchElevator;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.interfaces.MotionEventStream;

/**
 * A {@link MotionEventStream} that filters out motions events that fall within
 * {@link ViewConfiguration#getScaledTouchSlop()}.
 */
public class TouchSlopMotionEventView extends View implements MotionEventStream {

    /**
     * Container for holding relevant details about any in-progress motion events.
     */
    private static class TouchState {
        static final float NONE = -1f;
        float xDown = NONE;
        float yDown = NONE;
        float xCurrent = NONE;
        float yCurrent = NONE;
        float distance = NONE;

        public void reset() {
            xDown = NONE;
            yDown = NONE;
            xCurrent = NONE;
            yCurrent = NONE;
            distance = NONE;
        }
    }

    private float mLastDownX = TouchState.NONE;
    private float mLastDownY = TouchState.NONE;
    private MotionEventListener mListener;
    private OnTouchElevator mOnTouchElevator;
    private TouchState mState;
    private Paint mPaint;
    private Path mPath;
    private Path mPathMirror;
    private int mScaledTouchSlop;
    private int mAdditionalTouchSlop;
    private float mControlPointX;
    private float mControlPointY;
    private float mControlPointXMirror;
    private float mControlPointYMirror;

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
        mPath = new Path();
        mPathMirror = new Path();
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

        switch(event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mState.reset();
                mListener.onMotionEvent(event);
                break;
            case MotionEvent.ACTION_DOWN:
                mState.xDown = event.getX();
                mState.yDown = event.getY();
                mLastDownX = event.getX();
                mLastDownY = event.getY();
                mListener.onMotionEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mState.xCurrent = event.getX();
                mState.yCurrent = event.getY();
                mState.distance = distance(mState.xDown, mState.yDown, mState.xCurrent, mState.yCurrent);
                final float slop = mScaledTouchSlop + mAdditionalTouchSlop;
                if (mState.distance > slop) {
                    mListener.onMotionEvent(event);
                }
                break;
        }
        calculatePath();
        invalidate();
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
            canvas.drawCircle(mLastDownX, mLastDownY, (mScaledTouchSlop + mAdditionalTouchSlop),mPaint);
        }
        canvas.drawCircle(mControlPointX, mControlPointY, 10f, mPaint);
        canvas.drawCircle(mControlPointXMirror, mControlPointYMirror, 10f, mPaint);

        canvas.save();
        canvas.rotate(angle(mState), mState.xCurrent, mState.yCurrent);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate((float)(angle(mState) - 2*Math.toDegrees(Math.asin((mScaledTouchSlop + mAdditionalTouchSlop)/mState.distance))),
                mState.xCurrent, mState.yCurrent);
        canvas.drawPath(mPathMirror, mPaint);
        canvas.restore();
    }

    private static float distance(float xDown, float yDown, float xCurrent, float yCurrent) {
        final float xAbs = Math.abs(xDown - xCurrent);
        final float yAbs = Math.abs(yDown - yCurrent);
        return (float)Math.sqrt((yAbs*yAbs) + (xAbs * xAbs));
    }

    private Paint createPaint() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setAntiAlias(true);
        p.setStrokeWidth(3f);
        return p;
    }

    // d = Math.sqrt(distance(down, current)^2 - totalSlop^2)
    // new x = d * d/distance(down, current)
    // new y = d * r/distance(down, current)

    private float x(TouchState s) {
        final int totalSlop = mScaledTouchSlop + mAdditionalTouchSlop;
        final float currToTan = (float)Math.sqrt((s.distance * s.distance) - (totalSlop * totalSlop));
        return currToTan * (currToTan/s.distance);
    }

    private float y(TouchState s) {
        final int totalSlop = mScaledTouchSlop + mAdditionalTouchSlop;
        final float dwnToCurrent = distance(s.xDown, s.yDown, s.xCurrent, s.yCurrent);
        final float currToTan = (float)Math.sqrt((s.distance * s.distance) - (totalSlop * totalSlop));
        return  currToTan * (totalSlop/dwnToCurrent);
    }

    private float angle(TouchState s) {
        return (float) Math.toDegrees(Math.atan2(s.yDown - s.yCurrent, s.xDown - s.xCurrent));
    }

    private float controlPointX(TouchState s) {
        return s.xCurrent/5 + s.xCurrent;
    }

    private float controlPointY(TouchState s) {
        return s.yCurrent * .98f;
    }

    private float controlPointXInverse(TouchState s) {
        return s.xCurrent * .8f;
    }

    private float controlPointYInverse(TouchState s) {
        return s.yCurrent * 0.98f;
    }

    private void calculatePath() {
        mPath.reset();
        mPathMirror.reset();
        if (mState.yCurrent == TouchState.NONE || mState.xCurrent == TouchState.NONE || mState.distance == TouchState.NONE) {
            return;
        }
        mPath.moveTo(mState.xCurrent, mState.yCurrent);
        mControlPointX = controlPointX(mState);
        mControlPointY = controlPointY(mState);
        final float xMod = x(mState);
        final float yMod = y(mState);
        mPath.quadTo(mControlPointX, mControlPointY, mState.xCurrent + xMod,
                mState.yCurrent + yMod);

        mControlPointXMirror = controlPointXInverse(mState);
        mControlPointYMirror = controlPointYInverse(mState);
        mPathMirror.moveTo(mState.xCurrent, mState.yCurrent);
        mPathMirror.quadTo(mControlPointXMirror, mControlPointYMirror, mState.xCurrent + xMod,
                mState.yCurrent + yMod);
    }
}
