package com.bs.manage.service.notify;

import com.bs.manage.model.bean.notify.NotifyKpi;
import com.bs.manage.model.json.Page;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/6/16 19:19
 * fzj
 */
public interface NotifyKpiService extends CommonService<NotifyKpi> {

    /**
     * 查询未读消息数
     *
     * @return 未读消息数
     */
    Integer countByUnRead();

    /**
     * 考核通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 考核通知消息列表
     */
    Page<NotifyKpi> getByPage(Integer limit, Integer offset);

}
