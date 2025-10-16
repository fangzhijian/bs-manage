package com.bs.manage.mapper.notify;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.notify.NotifyKpi;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/6/16 19:19
 * fzj
 */
@Repository
public interface NotifyKpiMapper extends CommonMapper<NotifyKpi> {


    /**
     * 查询未读消息数
     *
     * @param notifyUser 通知的账号id
     * @return 未读消息数
     */
    Integer countByUnRead(@Param("notifyUser") Long notifyUser);

    Integer countByPage(@Param("notifyUser") Long notifyUser);

    List<NotifyKpi> getByPage(@Param("notifyUser") Long notifyUser, @Param("limit") Integer limit, @Param("offset") Integer offset);
}
