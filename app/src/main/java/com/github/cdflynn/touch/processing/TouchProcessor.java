package com.github.cdflynn.touch.processing;

import android.view.MotionEvent;
import android.view.View;

/**
 * An interface for processing touch events, but without ever declaring them as handled.
 */
public interface TouchProcessor {
    void onTouchEvent(View v, MotionEvent event);
}
