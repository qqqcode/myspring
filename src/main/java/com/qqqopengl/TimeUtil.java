package com.qqqopengl;

public class TimeUtil {
    public static float timeStarted = System.nanoTime();
    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }
}
