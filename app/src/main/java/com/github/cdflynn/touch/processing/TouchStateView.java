package com.github.cdflynn.touch.processing;

/**
 * Any class that knows how to draw a touch state.
 */
public interface TouchStateView {
    /**
     * Tell this view to draw the given state.
     */
    void drawTouchState(TouchState s);
}
