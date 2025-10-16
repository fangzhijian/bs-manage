package com.bs.manage.service.account.impl;

import com.bs.manage.code.PubCode;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.account.UserTagsMapper;
import com.bs.manage.model.bean.account.UserTags;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.account.UserTagsService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.service.customer.CustomerTagRelationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2020/3/2 17:16
 * fzj
 */
@Service
public class UserTagsServiceImpl extends CommonServiceImpl<UserTags> implements UserTagsService {

    private final UserTagsMapper userTagsMapper;
    private final CustomerTagRelationService customerTagRelationService;

    public UserTagsServiceImpl(UserTagsMapper userTagsMapper, @Lazy CustomerTagRelationService customerTagRelationService) {
        this.userTagsMapper = userTagsMapper;
        this.customerTagRelationService = customerTagRelationService;
    }


    @Override
    public void afterPropertiesSet() {
        setCommonMapper(userTagsMapper);
    }


    @Override
    @Cacheable(cacheNames = RedisConstants.USER_TAG, key = "#bean.getUser_id()")
    public List<UserTags> getAllBySelectKey(UserTags bean) {
        return super.getAllBySelectKey(bean);
    }

    @Override
    @CacheEvict(cacheNames = RedisConstants.USER_TAG, key = "#userId")
    public ResponseJson insertByUser(Long userId, UserTags userTags) {
        super.insert(userTags);
        return ResponseJson.success(userTags);
    }

    @Override
    @CacheEvict(cacheNames = RedisConstants.USER_TAG, key = "#userId", condition = "#result.code == " + PubCode.SUCCESS_CODE)
    public ResponseJson deleteByUser(Long userId, Long tagId) {
        if (customerTagRelationService.existForTag(tagId)) {
            return ResponseJson.fail("标签已被使用,请先取消对应客户中的标签！");
        }
        return super.delete(tagId);
    }

    @Override
    @CacheEvict(cacheNames = RedisConstants.USER_TAG, key = "#userId")
    public ResponseJson updateByUser(Long userId, UserTags userTags) {
        return super.update(userTags);
    }
}
