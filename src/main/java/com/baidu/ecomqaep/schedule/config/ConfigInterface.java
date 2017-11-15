package com.baidu.ecomqaep.schedule.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigInterface {

    final static String DEFAULT_NAME = "#default#";
    final static String DEFAULT_VALUE = "";

    String name() default DEFAULT_NAME;

    String defaultValue() default DEFAULT_VALUE;
}
