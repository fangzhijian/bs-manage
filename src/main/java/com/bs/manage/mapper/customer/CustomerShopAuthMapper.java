package com.bs.manage.mapper.customer;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.customer.CustomerShopAuth;
import com.bs.manage.model.bean.notify.NotifyCustomerAuth;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 2020/4/13 9:54
 * fzj
 */
@Repository
public interface CustomerShopAuthMapper extends CommonMapper<CustomerShopAuth> {

    /**
     * 获取即将过期的授权信息
     *
     * @param notifyDate 过期时间
     * @param userId     客户无跟进人时指定通知的账号id
     * @return 过期的授权信息
     */
    List<NotifyCustomerAuth> getNearExpireAuth(@Param("notifyDate") String notifyDate, @Param("userId") Integer userId);

}
