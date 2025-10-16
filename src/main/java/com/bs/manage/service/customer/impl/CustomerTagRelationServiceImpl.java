package com.bs.manage.service.customer.impl;

import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.customer.CustomerTagRelationMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.account.UserTags;
import com.bs.manage.model.bean.customer.Customer;
import com.bs.manage.model.bean.customer.CustomerTagRelation;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.customer.BatchTagParam;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.account.UserTagsService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerService;
import com.bs.manage.service.customer.CustomerTagRelationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 2020/3/2 17:17
 * fzj
 */
@Service
public class CustomerTagRelationServiceImpl extends CommonServiceImpl<CustomerTagRelation> implements CustomerTagRelationService {

    private final CustomerTagRelationMapper customerTagRelationMapper;
    private final UserTagsService userTagsService;
    private final UserService userService;
    private final CustomerService customerService;

    public CustomerTagRelationServiceImpl(CustomerTagRelationMapper customerTagRelationMapper, @Lazy UserTagsService userTagsService, UserService userService, @Lazy CustomerService customerService) {
        this.customerTagRelationMapper = customerTagRelationMapper;
        this.userTagsService = userTagsService;
        this.userService = userService;
        this.customerService = customerService;
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(customerTagRelationMapper);
    }


    /**
     * 批量标签管理(批量打标)
     * 多个用户打相同的多个标签
     * <p>
     * ids  客户id数组
     * tags 标签id数组
     *
     * @return 是否打标签成功
     */
    @Override
    @Transactional
    public ResponseJson batchAttachTags(BatchTagParam param) {
        List<CustomerTagRelation> relationList = new ArrayList<>();
        for (Long customer_id : param.getIds()) {
            for (Long tag_id : param.getTags()) {
                relationList.add(CustomerTagRelation.builder().customer_id(customer_id).user_tag_id(tag_id).build());
            }
        }
        if (relationList.size() > 0) {
            customerTagRelationMapper.batchAttachTags(relationList);
        }
        return ResponseJson.success();
    }

    /**
     * 单个标签管理
     * 单个用户分离标签(即删除一个标签)
     *
     * @param customer_id 客户id
     * @param tag_id      标签id
     * @return 是否分离标签成功
     */
    @Override
    @Transactional
    public ResponseJson detachTag(Long customer_id, Long tag_id) {
        UserTags userTags = userTagsService.getById(tag_id);
        if (userTags == null) {
            return ResponseJson.success();
        }
        User user = UserToken.getContext();
        if (!user.getId().equals(userTags.getUser_id())) {
            Customer customer = customerService.getById(customer_id);
            if (customer == null) {
                return ResponseJson.fail("客户已经不存在");
            }
            if (!customer.getProcess_user_id().equals(user.getId())) {
                User userForTag = userService.getById(userTags.getUser_id());
                return ResponseJson.fail(String.format("这是属于%s的专属标签", userForTag.getName()));
            }
        }
        customerTagRelationMapper.detachTag(customer_id, tag_id);
        return ResponseJson.success();
    }

    /**
     * 根据客户id匹配标签列表
     *
     * @param list 客户id
     * @return 标签列表
     */
    @Override
    public List<CustomerTagRelation> getCustomerTags(List<Long> list) {
        return customerTagRelationMapper.getCustomerTags(list);
    }

    /**
     * 判断标签是否被使用
     *
     * @param tagId 标签id
     * @return true or false
     */
    @Override
    public Boolean existForTag(Long tagId) {
        return customerTagRelationMapper.existForTag(tagId);
    }
}
