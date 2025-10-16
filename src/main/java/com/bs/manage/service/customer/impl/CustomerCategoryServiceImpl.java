package com.bs.manage.service.customer.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.customer.CustomerCategoryMapper;
import com.bs.manage.model.bean.customer.CustomerCategory;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerCategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2020/2/26 11:43
 * fzj
 */
@Service
public class CustomerCategoryServiceImpl extends CommonServiceImpl<CustomerCategory> implements CustomerCategoryService {

    private final CustomerCategoryMapper customerCategoryMapper;

    public CustomerCategoryServiceImpl(CustomerCategoryMapper customerCategoryMapper) {
        this.customerCategoryMapper = customerCategoryMapper;
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.CUSTOMER_CATEGORY, key = "#bean.getParent_id()")
    public List<CustomerCategory> getAllBySelectKey(CustomerCategory bean) {
        return super.getAllBySelectKey(bean);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.CUSTOMER_CATEGORY, key = "-1")
    public List<CustomerCategory> getAll() {
        return super.getAll();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerCategoryMapper);
    }
}
