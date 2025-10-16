package com.bs.manage.mapper.notify;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.notify.NotifyCustomerRelease;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/4/13 11:49
 * fzj
 */
@Repository
public interface NotifyCustomerReleaseMapper extends CommonMapper<NotifyCustomerRelease> {

    /**
     * 清空表
     */
    void truncate();

    /**
     * 查询未读消息数
     *
     * @param notifyUser 通知的账号id
     * @return 未读消息数
     */
    Integer countByUnRead(@Param("notifyUser") Long notifyUser);

    Integer countByPage(@Param("notifyUser") Long notifyUser);

    List<NotifyCustomerRelease> getByPage(@Param("notifyUser") Long notifyUser, @Param("limit") Integer limit, @Param("offset") Integer offset);

}
