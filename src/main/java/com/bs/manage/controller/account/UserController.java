package com.bs.manage.controller.account;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.exception.BusinessException;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.account.UserInsertParam;
import com.bs.manage.service.account.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 2020/1/15 10:52
 * fzj
 * 账号列表控制器
 */
@RestController
@RequestMapping("admin")
@Validated
@Role(isAdmin = true)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 创建账号
     * resources  权限数组
     *
     * @return 新增账号的信息
     */
    @PostMapping("users")
    public ResponseJson insert(@Validated @RequestBody UserInsertParam param) throws BusinessException {
        return userService.insertUser(param);
    }


    /**
     * 修改账号
     *
     * @param id 账号id
     * @return 修改账号后的信息
     */
    @PutMapping("users/{id}")
    public ResponseJson update(@PathVariable("id") Long id, @Validated @RequestBody UserInsertParam param) throws BusinessException {
        return userService.updateUser(id, param);
    }

    /**
     * 账号状态关系变更
     *
     * @param id       账号id
     * @param type     操作类型 1-正常 2-停用 3-离职 4-绑定 5-解散 6-接管
     * @param other_id 另外一个账号id,绑定的是上级id,接管的是接管者的id
     * @return 是否变更成功
     */
    @PutMapping("users/change_relation/{id}")
    @Caching(evict = {@CacheEvict(cacheNames = RedisConstants.USER_SUBORDINATE_ALL, allEntries = true),
            @CacheEvict(cacheNames = RedisConstants.USER_SUBORDINATE_SALE, allEntries = true)})
    public ResponseJson changeRelation(@PathVariable("id") Long id, @NotNull @Range(min = 1, max = 6) Integer type,
                                       Long other_id) {
        return userService.changeRelation(id, type, other_id);
    }

    /**
     * 账号列表
     *
     * @param limit    每页条数
     * @param offset   第几条开始
     * @param team_id  渠道id
     * @param keyword  模糊查询登陆邮箱或者姓名
     * @param parentId 上级id
     * @return 分页所得账号列表
     */
    @GetMapping("users")
    public ResponseJson getByPage(@NotNull Integer limit, @NotNull Integer offset, Long team_id, String keyword, Long parentId) {
        return ResponseJson.success(userService.getByPage(limit, offset, team_id, keyword, parentId));
    }

    /**
     * 重置账号密码
     *
     * @param id 账号id
     * @return 成功或失败
     */
    @PostMapping("user/{id}/reset_password")
    public ResponseJson resetPassword(@PathVariable("id") Long id) {
        return userService.resetPassword(id);
    }

    /**
     * 修改密码
     *
     * @param old_pass   旧密码
     * @param new_pass   新密码
     * @param check_pass 确认密码
     * @return 是否修改成功
     */
    @PostMapping("user/change_password")
    @Role(except = true)
    public ResponseJson changePassword(@NotBlank String old_pass, @NotBlank String new_pass, @NotBlank String check_pass) {
        if (!new_pass.equals(check_pass)) {
            return ResponseJson.fail("新密码两次确认不一致");
        }
        return userService.changePassword(old_pass, new_pass);
    }

    /**
     * 账号登录
     *
     * @param username 登录名 这里用邮箱
     * @param password 密码
     * @return 返回token
     */
    @PostMapping("login")
    public ResponseJson login(@NotBlank String username, @NotBlank String password) {
        return userService.login(username, password);
    }

    /**
     * @return 获取账号信息
     */
    @GetMapping("get_user")
    @Role(except = true)
    public ResponseJson getUserInfo() {
        return ResponseJson.success(userService.getUserInfo());
    }

    /**
     * 账号登出
     *
     * @param api_token 带时间加密的token
     * @return 返回是否登出
     */
    @PostMapping("logout")
    public ResponseJson logout(@NotBlank String api_token) {
        return userService.logout(api_token);
    }


    /**
     * 获取账号权限表信息
     *
     * @return 所有账号权限
     */
    @GetMapping("user_roles")
    public ResponseJson getUserRoles() {
        return userService.getUserRoles();
    }

    /**
     * @return 所有下属销售成员
     */
    @GetMapping("user/subordinate")
    @Role(except = true)
    public ResponseJson subordinateForSale() {
        return ResponseJson.success(userService.subordinateForSale(UserToken.getContext()));
    }
}
