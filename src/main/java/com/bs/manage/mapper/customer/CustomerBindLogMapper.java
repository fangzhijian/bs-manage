package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.CustomerBindLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/5/29 16:00
 * fzj
 */
@Repository
public interface CustomerBindLogMapper extends CommonMapper<CustomerBindLog> {

    /**
     * 查询最近的绑定者
     *
     * @param list 客户id列表
     * @return 最新的绑定列表
     */
    List<CustomerBindLog> getNearBindForCustomerIds(List<Long> list);

}
