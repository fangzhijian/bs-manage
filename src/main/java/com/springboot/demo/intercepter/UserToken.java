package com.springboot.demo.intercepter;

import com.springboot.demo.constant.RedisConstants;
import com.springboot.demo.model.bean.account.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 2020/1/17 11:26
 * fzj
 */
@Component
public class UserToken {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 最大登录过期时间
     */
    public static final long maxLoginSeconds = 3600L * 24 * 3;
    /**
     * 用于上下文中获取账号信息
     */
    private static final ThreadLocal<User> userContext = new ThreadLocal<>();

    public UserToken(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 放入账号缓存或删除
     *
     * @param apiToken 原生的token
     * @param user     账号信息
     */
    public void putCache(String apiToken, User user) {
        if (user == null) {
            redisTemplate.delete(String.format(RedisConstants.USER_CACHE, apiToken));
        } else {
            redisTemplate.opsForValue().set(String.format(RedisConstants.USER_CACHE, apiToken), user, maxLoginSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取缓存中的账号信息
     *
     * @param apiToken 原生的token
     * @return 账号信息
     */
    public User getCache(String apiToken) {
        return (User) redisTemplate.opsForValue().get(String.format(RedisConstants.USER_CACHE, apiToken));
    }

    /**
     * 刷新当前账号的缓存
     *
     * @param user 账号信息
     */
    public void refreshCache(User user) {
        User cache = this.getCache(user.getApiToken());
        if (cache != null) {
            redisTemplate.opsForValue().set(String.format(RedisConstants.USER_CACHE, user.getApiToken()), user, maxLoginSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置会话上下文中的账号信息
     *
     * @param user 账号信息
     */
    public static void setContext(User user) {
        userContext.set(user);
    }

    /**
     * 从会话上下文中获取账号信息
     *
     * @return 账号信息
     */
    public static User getContext() {
        return userContext.get();
    }

}
