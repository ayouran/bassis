package com.bassis.bean.annotation;

import java.lang.annotation.*;

/**
 * 自定义事件监听注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface Listener {
    /**
     * bean对象的别名
     */
    String name() default "";
}
