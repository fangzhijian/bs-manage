package com.bs.manage.mapper.notify;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.notify.NotifyCustomerAuth;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/4/13 11:55
 * fzj
 */
@Repository
public interface NotifyCustomerAuthMapper extends CommonMapper<NotifyCustomerAuth> {

    /**
     * 查询未读消息数
     *
     * @param notifyUser 通知的账号id
     * @return 未读消息数
     */
    Integer countByUnRead(@Param("notifyUser") Long notifyUser);

    Integer countByPage(@Param("notifyUser") Long notifyUser);

    List<NotifyCustomerAuth> getByPage(@Param("notifyUser") Long notifyUser, @Param("limit") Integer limit, @Param("offset") Integer offset);
}
