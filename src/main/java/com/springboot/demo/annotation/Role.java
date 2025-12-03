package com.springboot.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2020/2/21 11:01
 * fzj
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {

    String value() default "";

    /**
     * 是否只有管理员才能操作
     */
    boolean isAdmin() default false;

    /**
     * 默认使用api去匹配
     * 指定roleId时,直接使用id匹配
     */
    int roleId() default 0;

    /**
     * 不受权限限制,只适用方法
     */
    boolean except() default false;
}
