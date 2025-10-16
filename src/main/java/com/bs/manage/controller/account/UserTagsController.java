package com.bs.manage.controller.account;

import com.bs.manage.intercepter.UserToken;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.bean.account.UserTags;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.account.UserTagsService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 2020/3/2 17:07
 * fzj
 */
@RestController
@RequestMapping("admin/customer/tag")
@Validated
public class UserTagsController {

    private final UserTagsService userTagsService;

    public UserTagsController(UserTagsService userTagsService) {
        this.userTagsService = userTagsService;
    }

    @PostMapping
    public ResponseJson insert(@NotBlank String name) {
        User user = UserToken.getContext();
        UserTags userTags = UserTags.builder().user_id(user.getId()).name(name).build();
        return userTagsService.insertByUser(user.getId(), userTags);
    }

    @DeleteMapping("{id}")
    public ResponseJson delete(@PathVariable("id") Long id) {
        User user = UserToken.getContext();
        return userTagsService.deleteByUser(user.getId(), id);
    }

    @PutMapping("{id}")
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        User user = UserToken.getContext();
        UserTags userTags = UserTags.builder().id(id).name(name).build();
        return userTagsService.updateByUser(user.getId(), userTags);
    }
}
