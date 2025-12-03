

package com.springboot.demo.aspect;

import com.springboot.demo.annotation.RedisLock;
import com.springboot.demo.until.SpeLUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 2025/11/03
 */
@Aspect
@Component
@Order(99)
public class RedisLockAspect {

    @Resource
    private RedissonClient redissonClient;

    private static final String REDISSON_LOCK_PREFIX = "redisson_lock:";

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String speL = redisLock.key();
        String lockName = redisLock.lockName();
        RLock rLock = redissonClient.getLock(getRedisKey(joinPoint, lockName, speL));

        rLock.lock(redisLock.expire(), redisLock.timeUnit());

        Object result;
        try {
            //执行方法
            result = joinPoint.proceed();

        } finally {
            rLock.unlock();
        }
        return result;
    }

    /**
     * 将speL表达式转换为字符串
     *
     * @param joinPoint 切点
     * @return redisKey
     */
    private String getRedisKey(ProceedingJoinPoint joinPoint, String lockName, String speL) {
        if ("#userId".equals(speL)) {
            //从缓存中获取userId
            Long userId = 1L;
            return REDISSON_LOCK_PREFIX + lockName + ":" + userId;
        }
        return REDISSON_LOCK_PREFIX  + lockName + ":" + SpeLUtil.parse(joinPoint, speL);
    }
}
