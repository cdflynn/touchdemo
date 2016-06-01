package com.github.cdflynn.touch.processing;

/**
 * Container for holding relevant details about any in-progress motion events.
 */
public class TouchState {
    /**
     * No-Value for state.  Typically the {@link TouchState} fields will hold this value
     * if no touch event is in progress.
     */
    public static final float NONE = -1f;
    /**
     * The relative x coordinate where the motion event started.
     */
    public float xDown = NONE;
    /**
     * The relative y coordinate where the motion event started.
     */
    public float yDown = NONE;
    /**
     * The current x coordinate
     */
    public float xCurrent = NONE;
    /**
     * The current y coordinate
     */
    public float yCurrent = NONE;
    /**
     * The distance between the down and current coordinates.
     */
    public float distance = NONE;

    /**
     * 
     */
    public void reset() {
        xDown = NONE;
        yDown = NONE;
        xCurrent = NONE;
        yCurrent = NONE;
        distance = NONE;
    }
}
