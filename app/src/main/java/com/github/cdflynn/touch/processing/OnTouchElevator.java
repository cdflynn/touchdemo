package com.github.cdflynn.touch.processing;

import android.view.MotionEvent;
import android.view.View;

/**
 * Apply an elevation to the given view based on the input touch events.
 */
public class OnTouchElevator implements TouchProcessor {

    private static final long DURATION_MS = 300;
    private static final float Z_MIN = 3f;
    private static final float Z_MAX = 20f;

    @Override
    public void onTouchEvent(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.animate().translationZ(Z_MIN)
                        .setDuration(DURATION_MS)
                        .start();
                return;
            case MotionEvent.ACTION_DOWN:
                v.animate().translationZ(Z_MAX)
                        .setDuration(DURATION_MS)
                        .start();
                return;
            default:
                return;
        }
    }
}
