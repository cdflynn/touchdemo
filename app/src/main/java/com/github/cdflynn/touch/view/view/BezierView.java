package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.github.cdflynn.touch.R;
import com.github.cdflynn.touch.processing.OnTouchElevator;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.interfaces.MotionEventStream;

public class BezierView extends View implements MotionEventStream {

    private static final int ADD_RADIUS = 100;
    /**
     * Container for holding relevant details about any in-progress motion events.
     */
    protected static class TouchState {
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
    private Paint mPaint;
    private Path mPath;
    private Path mPathMirror;
    private int mScaledTouchSlop;
    private boolean mDrawControlPoints = true;
    protected TouchState mState;

    public BezierView(Context context) {
        super(context);
        init(context);
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BezierView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mOnTouchElevator = new OnTouchElevator();
        mState = new TouchState();
        mPaint = createPaint();
        mPath = new Path();
        mPathMirror = new Path();
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() + ADD_RADIUS;
    }

    /**
     * Turn on/off drawing the control points.  Default is {@code true}, which will draw them.
     */
    protected final void drawControlPoints(boolean shouldDraw) {
        mDrawControlPoints = shouldDraw;
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
                if (mState.distance > mScaledTouchSlop) {
                    mListener.onMotionEvent(event);
                }
                break;
        }
        calculatePath();
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLastDownX != TouchState.NONE && mLastDownY != TouchState.NONE) {
            canvas.drawCircle(mLastDownX, mLastDownY, mScaledTouchSlop,mPaint);
        }

        final float angle = angle(mState);
        final float sweep = sweep(mState);
        canvas.save();
        canvas.rotate((angle - sweep),
                mState.xCurrent, mState.yCurrent);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate((angle + sweep),
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

    /**
     * The change in the x value that is required to move from the current touch point to
     * the tangent.
     */
    private float x(TouchState s) {
        final float currToTan = (float)Math.sqrt((s.distance * s.distance) - (mScaledTouchSlop * mScaledTouchSlop));
        return currToTan * (currToTan/s.distance);
    }

    /**
     * The change in the y value that is required to move from the current touch point to
     * the tangent.
     */
    private float y(TouchState s) {
        final float currToTan = (float)Math.sqrt((s.distance * s.distance) - (mScaledTouchSlop * mScaledTouchSlop));
        return  currToTan * (mScaledTouchSlop/s.distance);
    }

    /**
     * Angle between the current touch coordinates and the down coordinates
     */
    private float angle(TouchState s) {
        return (float) Math.toDegrees(Math.atan2(s.yDown - s.yCurrent, s.xDown - s.xCurrent));
    }

    /**
     * Angle between the down event and the tangent point
     */
    private float sweep(TouchState s) {
        return (float) Math.toDegrees(Math.asin(mScaledTouchSlop/s.distance));
    }

    protected void calculatePath() {
        mPath.reset();
        mPathMirror.reset();
        if (mState.yCurrent == TouchState.NONE || mState.xCurrent == TouchState.NONE || mState.distance == TouchState.NONE) {
            return;
        }
        mPath.moveTo(mState.xCurrent, mState.yCurrent);
        final float xMod = x(mState);
        final float yMod = y(mState);
        final float controlPointX = mState.xCurrent + mState.distance * .66f;
        final float controlPointY = mState.yCurrent + yMod/3;
        mPath.quadTo(controlPointX, controlPointY, mState.xCurrent + xMod, mState.yCurrent);
        if (mDrawControlPoints) {
            mPath.addCircle(controlPointX, controlPointY, 10f, Path.Direction.CW);
        }

        final float controlPointXMirror = mState.xCurrent + mState.distance * .66f;
        final float controlPointYMirror = mState.yCurrent - yMod/3;
        mPathMirror.moveTo(mState.xCurrent, mState.yCurrent);
        mPathMirror.quadTo(controlPointXMirror, controlPointYMirror, mState.xCurrent + xMod, mState.yCurrent);
        if (mDrawControlPoints) {
            mPathMirror.addCircle(controlPointXMirror, controlPointYMirror, 10f, Path.Direction.CW);
        }
    }
}
