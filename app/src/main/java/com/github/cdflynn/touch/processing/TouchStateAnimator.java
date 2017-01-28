package com.github.cdflynn.touch.processing;

import android.view.animation.Interpolator;

/**
 * Any class that knows how to incrementally change a touch state for animation.
 */
public interface TouchStateAnimator {
    void cancel();

    void setDuration(long durationMs);

    void setInterpolator(Interpolator i);

    void start(TouchState s, TouchStateView outlet);
}
