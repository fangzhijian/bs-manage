package com.bs.manage.service.customer.impl;

import com.bs.manage.mapper.customer.CustomerShopMapper;
import com.bs.manage.model.bean.customer.CustomerShop;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerShopService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2020/7/24 10:30
 * fzj
 */
@Service
public class CustomerShopServiceImpl extends CommonServiceImpl<CustomerShop> implements CustomerShopService {

    private final CustomerShopMapper customerShopMapper;

    public CustomerShopServiceImpl(CustomerShopMapper customerShopMapper) {
        this.customerShopMapper = customerShopMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerShopMapper);
    }

    @Override
    public CustomerShop getShopDetail(Long customer_shop_id) {
        return customerShopMapper.getShopDetail(customer_shop_id);
    }

    @Override
    public void deleteByParentUniqueId(String parentUniqueId) {
        customerShopMapper.deleteByParentUniqueId(parentUniqueId);
    }

    @Override
    public List<CustomerShop> getAllShop(String parentUniqueId) {
        return customerShopMapper.getAllShop(parentUniqueId);
    }

    @Override
    public List<CustomerShop> judgeRepeat(String keyword) {
        return customerShopMapper.judgeRepeat(keyword);
    }
}
