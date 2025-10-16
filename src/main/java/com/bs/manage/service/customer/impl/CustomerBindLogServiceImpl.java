package com.bs.manage.service.customer.impl;

import com.bs.manage.mapper.customer.CustomerBindLogMapper;
import com.bs.manage.model.bean.customer.CustomerBindLog;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerBindLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2020/5/29 16:03
 * fzj
 */
@Service
public class CustomerBindLogServiceImpl extends CommonServiceImpl<CustomerBindLog> implements CustomerBindLogService {

    private final CustomerBindLogMapper customerBindLogMapper;

    public CustomerBindLogServiceImpl(CustomerBindLogMapper customerBindLogMapper) {
        this.customerBindLogMapper = customerBindLogMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerBindLogMapper);
    }

    @Override
    public List<CustomerBindLog> getNearBindForCustomerIds(List<Long> list) {
        return customerBindLogMapper.getNearBindForCustomerIds(list);
    }
}
