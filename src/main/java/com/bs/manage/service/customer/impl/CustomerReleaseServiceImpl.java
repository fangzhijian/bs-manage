package com.bs.manage.service.customer.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.customer.CustomerReleaseMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.param.customer.CustomerReleaseSearchParam;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerReleaseService;
import com.bs.manage.until.CodeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 2020/4/7 9:41
 * fzj
 */
@Service
public class CustomerReleaseServiceImpl extends CommonServiceImpl<CustomerRelease> implements CustomerReleaseService {

    private final CustomerReleaseMapper customerReleaseMapper;
    private final UserService userService;
    private final Gson gson;

    public CustomerReleaseServiceImpl(CustomerReleaseMapper customerReleaseMapper, @Lazy UserService userService, Gson gson) {
        this.customerReleaseMapper = customerReleaseMapper;
        this.userService = userService;
        this.gson = gson;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerReleaseMapper);
    }

    @Override
    public Page<CustomerRelease> getByPage(CustomerReleaseSearchParam param) {
        User user = UserToken.getContext();
        if (CodeCaption.ROLE_SALE == user.getRole()) {
            List<Long> process_user_ids = userService.subordinateForSale(user).stream().map(User::getId).collect(Collectors.toList());
            param.setProcess_user_ids(process_user_ids);
        }
        Page<CustomerRelease> page = new Page<>();
        Integer count = customerReleaseMapper.countByPage(param);
        page.setTotal(count);

        if (count > 0) {
            boolean hasRole = user.getRoleIds().contains(19);
            List<CustomerRelease> list = customerReleaseMapper.getByPage(param);
            for (CustomerRelease customerRelease : list) {
                customerRelease.setRelease_type_label(CodeUtil.getCaption(CodeCaption.RELEASE_TYPE, customerRelease.getRelease_type()));
                if (!hasRole) {
                    customerRelease.setAllocate_again(null);
                }
            }
            page.setItems(list);
        }
        return page;
    }

    /**
     * 清空表
     */
    @Override
    public void truncate() {
        customerReleaseMapper.truncate();
    }
}
