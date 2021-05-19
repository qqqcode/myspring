package com.qqqspring.tools;

/**
 * @author Johnson
 * 2021/5/18
 */

public abstract class StringUtils {

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static boolean hasText(String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();

        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }
}
