package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

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
    private static class MotionEventState {
        float xDown = -1f;
        float yDown = -1f;

        public void reset() {
            xDown = -1;
            yDown = -1;
        }
    }

    private MotionEventListener mListener;
    private OnTouchElevator mOnTouchElevator;
    private MotionEventState mState;
    private int mScaledTouchSlop;


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
        mState = new MotionEventState();
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
                mState.xDown = event.getRawX();
                mState.yDown = event.getRawY();
                mListener.onMotionEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                final float distance = distance(mState.xDown, mState.yDown, event.getRawX(), event.getRawX());
                if (distance > mScaledTouchSlop) {
                    mListener.onMotionEvent(event);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private static float distance(float xDown, float yDown, float xCurrent, float yCurrent) {
        final float yAbs = Math.abs(yDown - yCurrent);
        final float xAbs = Math.abs(xDown - xCurrent);
        return (float)Math.sqrt((yAbs*yAbs) + (xAbs * xAbs));
    }
}
