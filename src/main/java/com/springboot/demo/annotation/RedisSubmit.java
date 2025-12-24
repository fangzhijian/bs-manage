package com.springboot.demo.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * {@code @description}
 * 防重复提交
 *
 * @author fangzhijian
 * @since 2025-11-07 19:19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisSubmit {

    /**
     * redis锁 名字
     */
    String lockName() default "";

    /**
     * redis锁key支持speL表达式，未登录接口修改为其他字段,例如#param.mobile
     */
    String key() default "#userId";

    /**
     * 用于编辑接口, key示例值 "#param.id"
     * 当新增时id为空，使用默认#userId判重复
     * 当修改时id不为空，使用spel表达式获取id判重复
     */
    boolean edit() default false;

    /**
     * 防重复提交提示词
     */
    String message() default "正在操作中，请稍后尝试";

    /**
     * 过期秒数,默认为600毫秒
     *
     */
    int expire() default 600;

    /**
     * 超时时间单位
     *
     * @return 毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
