

package com.springboot.demo.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis进行分布式锁
 * 2025/11/03
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

	/**
	 * redis锁 名字
	 */
	String lockName() default "";

    /**
     * redis锁key支持speL表达式，未登录接口修改为其他字段,例如#param.mobile
     */
    String key() default "#userId";

	/**
	 * 过期秒数,默认为5毫秒
	 *
	 * @return 轮询锁的时间
	 */
	int expire() default 5000;

	/**
	 * 超时时间单位
	 *
	 * @return 毫秒
	 */
	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
