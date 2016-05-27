package com.github.cdflynn.touch.util;

public class Geometry {

    public static float distance(float x1, float y1, float x2, float y2) {
        final float xAbs = Math.abs(x1 - x2);
        final float yAbs = Math.abs(y1 - y2);
        return (float)Math.sqrt((yAbs*yAbs) + (xAbs * xAbs));
    }
}
