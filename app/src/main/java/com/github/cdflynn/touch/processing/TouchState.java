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
    public float xDownRaw = NONE;
    /**
     * The relative y coordinate where the motion event started.
     */
    public float yDown = NONE;
    public float yDownRaw = NONE;
    /**
     * The current x coordinate
     */
    public float xCurrent = NONE;
    public float xCurrentRaw = NONE;
    /**
     * The current y coordinate
     */
    public float yCurrent = NONE;
    public float yCurrentRaw = NONE;
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

    @Override
    public String toString() {
        return "TouchState{" +
                "xDown=" + xDown +
                ", xDownRaw=" + xDownRaw +
                ", yDown=" + yDown +
                ", yDownRaw=" + yDownRaw +
                ", xCurrent=" + xCurrent +
                ", xCurrentRaw=" + xCurrentRaw +
                ", yCurrent=" + yCurrent +
                ", yCurrentRaw=" + yCurrentRaw +
                ", distance=" + distance +
                '}';
    }
}
