package com.github.cdflynn.touch.view.view;

import android.content.Context;
import android.util.AttributeSet;

import com.github.cdflynn.touch.processing.TensionProcessor;
import com.github.cdflynn.touch.processing.TouchStateTracker;

public class TensionView extends AnimatedBezierView {

    private TensionProcessor mTensionProcessor;

    public TensionView(Context context) {
        super(context);
        init();
    }

    public TensionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TensionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TensionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTensionProcessor = new TensionProcessor(new TouchStateTracker(mState), mState);
        setTouchProcessor(mTensionProcessor);
    }

    public void setTension(float tension) {
        mTensionProcessor.setTension(tension);
        invalidate();
    }
}
