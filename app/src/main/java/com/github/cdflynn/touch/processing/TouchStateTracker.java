package com.github.cdflynn.touch.processing;

import android.view.MotionEvent;
import android.view.View;

import com.github.cdflynn.touch.util.Geometry;

public class TouchStateTracker implements TouchProcessor {

    private TouchState mState;

    /**
     * Create a new {@link TouchStateTracker} that will write state to the given
     * {@code state} object.
     */
    public TouchStateTracker(TouchState state) {
        mState = state;
    }

    @Override
    public void onTouchEvent(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mState.reset();
                break;
            case MotionEvent.ACTION_DOWN:
                mState.xDown = event.getX();
                mState.yDown = event.getY();
                mState.xDownRaw = event.getRawX();
                mState.yDownRaw = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mState.xCurrent = event.getX();
                mState.yCurrent = event.getY();
                mState.xCurrentRaw = event.getRawX();
                mState.yCurrentRaw = event.getRawY();
                mState.distance = Geometry.distance(mState.xDown, mState.yDown, mState.xCurrent, mState.yCurrent);
                break;
        }
    }

}
