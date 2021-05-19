package com.qqqspring.tools;

/**
 * @author Johnson
 * 2021/5/18
 */
public abstract class Assert {
    public Assert() {
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText(String text,String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }


}
