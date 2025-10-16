package com.bs.manage.service.token.impl;

import com.bs.manage.code.PubCode;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.constant.TokenConstants;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.model.json.token.TokenInfo;
import com.bs.manage.service.token.TokenService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 2020/6/10 15:49
 * fzj
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${appKeyDingDing}")
    private String appKeyDingDing;
    @Value("${appSecretDingDing}")
    private String appSecretDingDing;

    private final RedisTemplate<String, Object> redisTemplate;

    public TokenServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public String getToken(String type) {
        String tokenRedisKey = RedisConstants.TOKEN.concat(type);
        Long expire = redisTemplate.getExpire(tokenRedisKey);
        if (expire == null || expire < 60){
            TokenInfo tokenInfo;
            if (TokenConstants.DING_DING.equals(type)){
                tokenInfo = getTokenForDingDing();
            } else {
                throw new MyRunException("不支持获取的token类型,类型参考TokenConstants常量类");
            }
            redisTemplate.opsForValue().set(tokenRedisKey,tokenInfo.getToken(),tokenInfo.getExpire_time(), TimeUnit.SECONDS);
            return tokenInfo.getToken();
        }else {
            return (String) redisTemplate.opsForValue().get(tokenRedisKey);
        }
    }

    /**
     * 获取钉钉应用的token
     * @return token及过期时间
     */
    private TokenInfo getTokenForDingDing() {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(appKeyDingDing);
        request.setAppsecret(appSecretDingDing);
        request.setHttpMethod("GET");
        try {
            OapiGettokenResponse response = client.execute(request);
            if (response.getErrcode() != PubCode.SUCCESS_CODE) {
                throw new MyRunException(response.getErrmsg());
            }
            return TokenInfo.builder().token(response.getAccessToken()).expire_time(response.getExpiresIn() ).build();
        } catch (ApiException e) {
            log.error(e.getErrMsg(), e);
            throw new MyRunException("钉钉调用异常");
        }
    }

}
