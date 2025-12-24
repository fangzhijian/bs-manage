

package com.springboot.demo.aspect;

import cn.hutool.core.util.StrUtil;
import com.springboot.demo.annotation.RedisSubmit;
import com.springboot.demo.exception.BusinessException;
import com.springboot.demo.intercepter.UserToken;
import com.springboot.demo.model.bean.account.User;
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
        String redisKey = getRedisKey(joinPoint, redisSubmit);
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
    private String getRedisKey(ProceedingJoinPoint joinPoint, RedisSubmit redisSubmit) {
        String spel = redisSubmit.key();
        String lockName = redisSubmit.lockName();
        //编辑接口根据id是否传入，使用id或者userId来判断重复提交
        if (redisSubmit.edit()) {
            String id = SpeLUtil.parse(joinPoint, spel);
            if (StrUtil.isNotBlank(id)) {
                return REDIS_SUBMIT_PREFIX + lockName  +":update:"+ id;
            } else {
                spel = "#userId";
            }
        }
        if ("#userId".equals(spel)) {
            //从缓存中获取userId
            User user = UserToken.getContext();
            if (user == null) {
                throw new BusinessException("接口未登录，请使用其他@RedisSubmit.key");
            }
            return REDIS_SUBMIT_PREFIX + lockName + ":" + user.getId();
        }
        return REDIS_SUBMIT_PREFIX + lockName + ":" + SpeLUtil.parse(joinPoint, spel);
    }
}
