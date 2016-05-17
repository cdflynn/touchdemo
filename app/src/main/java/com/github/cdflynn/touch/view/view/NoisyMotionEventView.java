package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.cdflynn.touch.processing.OnTouchElevator;
import com.github.cdflynn.touch.view.interfaces.MotionEventListener;
import com.github.cdflynn.touch.view.interfaces.MotionEventStream;

/**
 * A {@link MotionEventStream} that reports all motion events.
 */
public class NoisyMotionEventView extends View implements MotionEventStream {

    private OnTouchElevator mOnTouchElevator;
    private MotionEventListener mListener;

    public NoisyMotionEventView(Context context) {
        super(context);
    }

    public NoisyMotionEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoisyMotionEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoisyMotionEventView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setMotionEventListener(MotionEventListener listener) {
        mListener = listener;
        mOnTouchElevator = new OnTouchElevator();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mListener != null) {
            mListener.onMotionEvent(event);
        }
        mOnTouchElevator.onTouchEvent(this, event);
        return super.onTouchEvent(event);
    }
}
