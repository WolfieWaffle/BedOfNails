package com.github.wolfiewaffle.bon.tools;

public class BONMath {

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float signMin(float val, float minAbs) {
        if (Math.signum(val) > 0) {
            return Math.max(val, minAbs);
        } else if (Math.signum(val) < 0) {
            return Math.min(-minAbs, val);
        } else {
            return 0;
        }
    }
}
