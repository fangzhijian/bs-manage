package com.bs.manage.service.notify.impl;

import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.notify.NotifyCustomerReleaseMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.notify.NotifyCustomerRelease;
import com.bs.manage.model.json.Page;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.notify.NotifyCustomerReleaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2020/4/13 11:51
 * fzj
 */
@Service
public class NotifyCustomerReleaseServiceImpl extends CommonServiceImpl<NotifyCustomerRelease> implements NotifyCustomerReleaseService {

    private final NotifyCustomerReleaseMapper notifyCustomerReleaseMapper;

    public NotifyCustomerReleaseServiceImpl(NotifyCustomerReleaseMapper notifyCustomerReleaseMapper) {
        this.notifyCustomerReleaseMapper = notifyCustomerReleaseMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(notifyCustomerReleaseMapper);
    }

    /**
     * 清空表
     */
    @Override
    @Transactional
    public void truncate() {
        notifyCustomerReleaseMapper.truncate();
    }

    /**
     * 查询未读消息数
     *
     * @return 未读消息数
     */
    @Override
    public Integer countByUnRead() {
        User user = UserToken.getContext();
        return notifyCustomerReleaseMapper.countByUnRead(user.getId());
    }

    /**
     * 分页查询客户授权过期通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 客户授权过期通知列表
     */
    @Override
    public Page<NotifyCustomerRelease> getByPage(Integer limit, Integer offset) {
        Page<NotifyCustomerRelease> page = new Page<>();
        User user = UserToken.getContext();
        int count = notifyCustomerReleaseMapper.countByPage(user.getId());
        page.setTotal(count);
        if (count > 0) {
            page.setItems(notifyCustomerReleaseMapper.getByPage(user.getId(), limit, offset));
        }
        return page;
    }
}
