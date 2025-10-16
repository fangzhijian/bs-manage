package com.bs.manage.service.notify.impl;

import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.notify.NotifyCustomerAuthMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.notify.NotifyCustomerAuth;
import com.bs.manage.model.json.Page;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.notify.NotifyCustomerAuthService;
import org.springframework.stereotype.Repository;

/**
 * 2020/4/13 11:56
 * fzj
 */
@Repository
public class NotifyCustomerAuthServiceImpl extends CommonServiceImpl<NotifyCustomerAuth> implements NotifyCustomerAuthService {

    private final NotifyCustomerAuthMapper notifyCustomerAuthMapper;

    public NotifyCustomerAuthServiceImpl(NotifyCustomerAuthMapper notifyCustomerAuthMapper) {
        this.notifyCustomerAuthMapper = notifyCustomerAuthMapper;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(notifyCustomerAuthMapper);
    }

    /**
     * 查询未读消息数
     *
     * @return 未读消息数
     */
    @Override
    public Integer countByUnRead() {
        User user = UserToken.getContext();
        return notifyCustomerAuthMapper.countByUnRead(user.getId());
    }

    /**
     * 分页查询客户授权过期通知
     *
     * @param limit  每页条数
     * @param offset 第几条开始
     * @return 客户授权过期通知列表
     */
    @Override
    public Page<NotifyCustomerAuth> getByPage(Integer limit, Integer offset) {
        Page<NotifyCustomerAuth> page = new Page<>();
        User user = UserToken.getContext();
        Integer count = notifyCustomerAuthMapper.countByPage(user.getId());
        page.setTotal(count);
        if (count > 0) {
            page.setItems(notifyCustomerAuthMapper.getByPage(user.getId(), limit, offset));
        }
        return page;
    }
}
