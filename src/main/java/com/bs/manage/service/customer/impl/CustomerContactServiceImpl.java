package com.bs.manage.service.customer.impl;

import com.bs.manage.mapper.customer.CustomerContactMapper;
import com.bs.manage.model.bean.customer.CustomerContact;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerContactService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2020/3/13 17:05
 * fzj
 */
@Service
public class CustomerContactServiceImpl extends CommonServiceImpl<CustomerContact> implements CustomerContactService {

    private final CustomerContactMapper customerContactMapper;

    public CustomerContactServiceImpl(CustomerContactMapper customerContactMapper) {
        this.customerContactMapper = customerContactMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerContactMapper);
    }

    /**
     * 根据客户批量插入联系人
     *
     * @param list        联系人信息
     * @param customer_id 客户id
     */
    @Override
    public void insertBatchAsCustomer(List<CustomerContact> list, Long customer_id) {
        customerContactMapper.insertBatchAsCustomer(list, customer_id);
    }
}
