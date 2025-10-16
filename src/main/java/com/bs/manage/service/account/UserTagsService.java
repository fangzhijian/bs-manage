package com.bs.manage.service.account;

import com.bs.manage.model.bean.account.UserTags;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/3/2 17:16
 * fzj
 */
public interface UserTagsService extends CommonService<UserTags> {

    ResponseJson insertByUser(Long userId, UserTags userTags);

    ResponseJson deleteByUser(Long userId, Long tagId);

    ResponseJson updateByUser(Long userId, UserTags userTags);

}
