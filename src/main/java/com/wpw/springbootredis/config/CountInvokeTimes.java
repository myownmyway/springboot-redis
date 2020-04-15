package com.wpw.springbootredis.config;

import java.lang.annotation.*;

/**
 * @author wpw
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CountInvokeTimes {
}
