package com.bs.manage.service.notify.impl;

import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.notify.NotifyKpiMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.notify.NotifyKpi;
import com.bs.manage.model.json.Page;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.notify.NotifyKpiService;
import org.springframework.stereotype.Service;

/**
 * 2020/6/16 19:20
 * fzj
 */
@Service
public class NotifyKpiServiceImpl extends CommonServiceImpl<NotifyKpi> implements NotifyKpiService {

    private final NotifyKpiMapper notifyKpiMapper;

    public NotifyKpiServiceImpl(NotifyKpiMapper notifyKpiMapper) {
        this.notifyKpiMapper = notifyKpiMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(notifyKpiMapper);
    }

    /**
     * 查询未读消息数
     *
     * @return 未读消息数
     */
    @Override
    public Integer countByUnRead() {
        User user = UserToken.getContext();
        return notifyKpiMapper.countByUnRead(user.getId());
    }

    /**
     * 考核通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 考核通知消息列表
     */
    @Override
    public Page<NotifyKpi> getByPage(Integer limit, Integer offset) {
        Page<NotifyKpi> page = new Page<>();
        User user = UserToken.getContext();
        int count = notifyKpiMapper.countByPage(user.getId());
        page.setTotal(count);
        if (count > 0) {
            page.setItems(notifyKpiMapper.getByPage(user.getId(), limit, offset));
        }
        return page;
    }
}
