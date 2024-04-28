package com.ge.apm.service.utils;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18n {
    String value() default "";
}
