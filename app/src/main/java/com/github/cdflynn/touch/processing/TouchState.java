package com.github.cdflynn.touch.processing;

/**
 * Container for holding relevant details about any in-progress motion events.
 */
public class TouchState {
    public static final float NONE = -1f;
    public float xDown = NONE;
    public float yDown = NONE;
    public float xCurrent = NONE;
    public float yCurrent = NONE;
    public float distance = NONE;

    public void reset() {
        xDown = NONE;
        yDown = NONE;
        xCurrent = NONE;
        yCurrent = NONE;
        distance = NONE;
    }
}
