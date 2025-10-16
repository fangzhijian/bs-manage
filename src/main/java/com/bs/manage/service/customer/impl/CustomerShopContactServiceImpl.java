package com.bs.manage.service.customer.impl;

import com.bs.manage.mapper.customer.CustomerShopContactMapper;
import com.bs.manage.model.bean.customer.CustomerShopContact;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerShopContactService;
import org.springframework.stereotype.Service;

/**
 * 2020/7/24 10:37
 * fzj
 */
@Service
public class CustomerShopContactServiceImpl extends CommonServiceImpl<CustomerShopContact> implements CustomerShopContactService {

    private final CustomerShopContactMapper customerShopContactMapper;

    public CustomerShopContactServiceImpl(CustomerShopContactMapper customerShopContactMapper) {
        this.customerShopContactMapper = customerShopContactMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerShopContactMapper);
    }
}
