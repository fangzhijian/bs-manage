

package com.springboot.demo.aspect;

import com.springboot.demo.annotation.RedisSubmit;
import com.springboot.demo.exception.BusinessException;
import com.springboot.demo.until.SpeLUtil;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 2025/11/03
 */
@Aspect
@Component
@Order(98)
public class RedisSubmitAspect {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_SUBMIT_PREFIX = "redis_submit:";

    @Around("@annotation(redisSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, RedisSubmit redisSubmit) throws Throwable {
        String speL = redisSubmit.key();
        String lockName = redisSubmit.lockName();
        String redisKey = getRedisKey(joinPoint, lockName, speL);
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(redisKey, 1, redisSubmit.expire(), redisSubmit.timeUnit());
        if (lock != null && !lock) {
            throw new BusinessException(redisSubmit.message());
        }
        return joinPoint.proceed();
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
            return REDIS_SUBMIT_PREFIX + lockName + ":" + userId;
        }
        return REDIS_SUBMIT_PREFIX + lockName + ":" + SpeLUtil.parse(joinPoint, speL);
    }
}
