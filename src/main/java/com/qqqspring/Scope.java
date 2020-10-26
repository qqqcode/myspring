package com.qqqspring;

public @interface Scope {
    String value() default "singleton";
}
