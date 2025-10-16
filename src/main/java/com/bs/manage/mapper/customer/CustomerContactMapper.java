package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.CustomerContact;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/2/27 13:25
 * fzj
 */
@Repository
public interface CustomerContactMapper extends CommonMapper<CustomerContact> {

    /**
     * 根据客户批量插入联系人
     *
     * @param list        联系人信息
     * @param customer_id 客户id
     * @return 批量插入结果
     */
    Integer insertBatchAsCustomer(@Param("list") List<CustomerContact> list, @Param("customer_id") Long customer_id);

}
