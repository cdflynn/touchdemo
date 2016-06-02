package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.cdflynn.touch.processing.InterpolatedTensionProcessor;
import com.github.cdflynn.touch.processing.TouchStateTracker;

public class InterpolatedTensionView extends AnimatedBezierView {

    private InterpolatedTensionProcessor mTensionProcessor;

    public InterpolatedTensionView(Context context) {
        super(context);
        init();
    }

    public InterpolatedTensionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InterpolatedTensionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public InterpolatedTensionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTensionProcessor = new InterpolatedTensionProcessor(new TouchStateTracker(mState), mState);
        setTouchProcessor(mTensionProcessor);
    }

    public void setRadii(int min, int max) {
        mTensionProcessor.setRadii(min, max);
        invalidate();
    }

    public void setTension(float tension) {
        mTensionProcessor.setTension(tension);
        invalidate();
    }
}
