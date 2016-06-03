package com.github.cdflynn.touch.processing;

import android.view.MotionEvent;
import android.view.View;

import com.github.cdflynn.touch.util.Geometry;

public class TouchStateTracker implements TouchProcessor {

    private TouchState mState;

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
                break;
            case MotionEvent.ACTION_MOVE:
                mState.xCurrent = event.getX();
                mState.yCurrent = event.getY();
                mState.distance = Geometry.distance(mState.xDown, mState.yDown, mState.xCurrent, mState.yCurrent);
                break;
        }
    }

}
