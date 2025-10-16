package com.bs.manage.service.customer.impl;

import com.bs.manage.constant.Constants;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.customer.CustomerLevelMapper;
import com.bs.manage.model.bean.customer.CustomerLevel;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.configures.ConfiguresService;
import com.bs.manage.service.customer.CustomerLevelService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2020/2/26 10:47
 * fzj
 */
@Service
public class CustomerLevelServiceImpl extends CommonServiceImpl<CustomerLevel> implements CustomerLevelService {

    private final CustomerLevelMapper customerLevelMapper;
    private final ConfiguresService configuresService;

    public CustomerLevelServiceImpl(CustomerLevelMapper customerLevelMapper, ConfiguresService configuresService) {
        this.customerLevelMapper = customerLevelMapper;
        this.configuresService = configuresService;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerLevelMapper);
    }


    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        int codeValue = configuresService.getCodeValue(Constants.CONFIG_CUSTOMER_LEVEL);
        if (codeValue == 0) {
            return ResponseJson.fail("淘宝客户等级已被锁定无法修改和删除");
        }
        return super.delete(id);
    }

    @Override
    @Transactional
    public ResponseJson update(CustomerLevel bean) {
        int codeValue = configuresService.getCodeValue(Constants.CONFIG_CUSTOMER_LEVEL);
        if (codeValue == 0) {
            return ResponseJson.fail("淘宝客户等级已被锁定无法修改和删除");
        }
        return super.update(bean);
    }

    @Cacheable(cacheNames = RedisConstants.CUSTOMER_LEVEL)
    @Override
    public List<CustomerLevel> getAll() {
        return super.getAll();
    }
}
