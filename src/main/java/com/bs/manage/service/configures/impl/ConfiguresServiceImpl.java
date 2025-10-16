package com.bs.manage.service.configures.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.mapper.configures.ConfiguresMapper;
import com.bs.manage.model.bean.configures.Configures;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.configures.ConfiguresService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 2020/3/12 10:05
 * fzj
 */
@Service
public class ConfiguresServiceImpl extends CommonServiceImpl<Configures> implements ConfiguresService {

    private final ConfiguresMapper configuresMapper;

    public ConfiguresServiceImpl(ConfiguresMapper configuresMapper) {
        this.configuresMapper = configuresMapper;
    }


    @Override
    @Transactional
    public ResponseJson insert(Configures bean) {
        if (super.getOneBySelectKey(Configures.builder().code(bean.getCode()).build()) != null) {
            return ResponseJson.fail("code不能重复");
        }
        return super.insert(bean);
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(configuresMapper);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.CONFIGURE, key = "-1")
    public List<Configures> getAll() {
        return super.getAll();
    }

    /**
     * 查询code对应的值
     *
     * @param code code
     * @return 值
     */
    @Override
    @Cacheable(cacheNames = RedisConstants.CONFIGURE, key = "#code")
    public int getCodeValue(Integer code) {
        Configures configures = configuresMapper.getOneBySelectKey(Configures.builder().code(code).build());
        if (configures == null || !StringUtils.hasText(configures.getCode_value())) {
            throw new MyRunException("请联系管理员到常用配置里面及时配置");
        }
        return Integer.parseInt(configures.getCode_value().trim());
    }
}
