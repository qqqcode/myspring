package com.qqqspring;

import java.lang.annotation.*;

/**
 * @author Johnson
 * 2021/5/17
 */

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nullable {
    String value() default "";
}
