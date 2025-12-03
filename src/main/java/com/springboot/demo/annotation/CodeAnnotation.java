package com.springboot.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2020/2/18 16:34
 * fzj
 * 用于code对应中文说明的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeAnnotation {

    /**
     * code对应的类别
     */
    String category();

    /**
     * code对应的中文说明
     */
    String value();

}
