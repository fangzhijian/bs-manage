package com.springboot.demo.service.user.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.demo.code.CodeCaption;
import com.springboot.demo.code.PubCode;
import com.springboot.demo.exception.BusinessException;
import com.springboot.demo.model.bean.account.UserRole;
import com.springboot.demo.model.param.user.UserRoleResult;
import com.springboot.demo.until.Md5Util;
import com.springboot.demo.intercepter.UserToken;
import com.springboot.demo.mapper.user.UserMapper;
import com.springboot.demo.model.bean.account.User;
import com.springboot.demo.model.json.ResponseJson;
import com.springboot.demo.model.param.user.UserInsertParam;
import com.springboot.demo.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 2020/1/15 10:59
 * fzj
 * 账号列表服务
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserToken userToken;

    /**
     * 创建账号
     */
    @Override
    @Transactional
    public ResponseJson insertUser(UserInsertParam param) throws BusinessException {
        //入参校验
        User user = checkUserParam(param, null);
        String password = StringUtils.hasText(param.getPassword()) ? Md5Util.md5(param.getPassword()) : Md5Util.DEFAULT_PASSWORD;
        String apiToken = Md5Util.md5(param.getName());
        user.setPassword(password);
        user.setApiToken(apiToken);
        this.save(user);
        user.setApiToken(null);
        return ResponseJson.success(user);
    }

    /**
     * 修改账号
     * 角色role不能修改,Mapper中删除
     *
     * @param id 账号id
     */
    @Override
    @Transactional
    public ResponseJson updateUser(Long id, UserInsertParam param) throws BusinessException {
        //入参校验
        User user = checkUserParam(param, id);
        user.setId(id);
        this.updateById(user);
        //当前账户若登陆更新其缓存
        user = super.getById(id);
        userToken.refreshCache(user);
        user.setApiToken(null);
        return ResponseJson.success(user);
    }

    /**
     * 重置账号密码
     *
     * @param id 账号id
     * @return 成功或失败
     */
    @Override
    @Transactional
    public ResponseJson resetPassword(Long id) {
        User user = super.getById(id);
        if (user == null) {
            return ResponseJson.fail(String.format("账号%s不存在", id));
        }
        user.setPassword(Md5Util.DEFAULT_PASSWORD);
        this.updateById(user);

        //当前账户若登陆更新其缓存
        userToken.refreshCache(user);
        return ResponseJson.success();
    }

    /**
     * 修改密码
     *
     * @param old_pass 旧密码
     * @param new_pass 新密码
     * @return 是否修改成功
     */
    @Override
    @Transactional
    public ResponseJson changePassword(String old_pass, String new_pass) {
        User user = UserToken.getContext();
        if (!Md5Util.verify(old_pass, user.getPassword())) {
            return ResponseJson.fail("原密码输入错误");
        }
        user.setPassword(Md5Util.md5(new_pass));
        this.updateById(user);

        //更新缓存
        UserToken.setContext(user);
        userToken.refreshCache(user);
        return ResponseJson.success();
    }

    /**
     * 账号登录
     *
     * @param username 登录名 这里用邮箱
     * @param password 密码
     * @return 返回token
     */
    @Override
    public ResponseJson login(String username, String password) {
        User user = this.lambdaQuery().eq(User::getName, username).one();
        checkUserStatus(user, true);
        if (!Md5Util.verify(password, user.getPassword())) {
            return ResponseJson.fail("密码错误");
        }

        //放入缓存
        String apiToken = user.getApiToken();
        userToken.putCache(apiToken, user);

        //给前端发送的token加上时间
        String encode64Token = Md5Util.encode64TokenForDateTime(apiToken);
        return ResponseJson.success(encode64Token);
    }

    /**
     * @return 获取账号信息
     */
    @Override
    public ResponseJson getUserInfo() {
        User context = UserToken.getContext();
        List<Integer> roleIds = context.getRoleIds();
        //设置权限id
        if (CodeCaption.ROLE_ADMIN == context.getRole()) {
            roleIds.addAll(UserRole.userRoles.stream().map(UserRole::getId).filter(id -> id != 19).toList());
            context.setResources(JSON.toJSONString(roleIds));
        } else {
            context.setResources(JSON.toJSONString(roleIds));
        }
        context.setPassword(null);
        return ResponseJson.success(context);
    }

    /**
     * 账号登出
     *
     * @param api_token 带时间加密的token
     * @return 返回是否登出
     */
    @Override
    public ResponseJson logout(String api_token) {
        String originToken = Md5Util.getOriginToken(api_token);
        User user = userToken.getCache(originToken);
        if (user != null) {
            userToken.putCache(user.getApiToken(), null);
        }
        return ResponseJson.success();
    }

    /**
     * 获取账号权限表信息
     *
     * @return 所有账号权限
     */
    @Override
    public ResponseJson getUserRoles() {
        //UserRole.userRoles按照parent_id排序,所有当parent_id不相等时直接退出循环
        List<UserRoleResult> roleResultList = new ArrayList<>();
        for (UserRole userRole : UserRole.userRoles) {
            if (userRole.getParent_id() == 0) {
                UserRoleResult roleResult = new UserRoleResult();
                roleResult.setParent_id(userRole.getId());
                roleResult.setLabel(userRole.getLabel());
                roleResultList.add(roleResult);
            } else {
                break;
            }
        }
        for (UserRoleResult userRoleResult : roleResultList) {
            boolean hasSetChild = false;
            for (UserRole userRole : UserRole.userRoles) {
                if (userRoleResult.getParent_id().equals(userRole.getParent_id())) {
                    userRoleResult.getChild_role().add(userRole);
                    hasSetChild = true;
                } else if (hasSetChild) {
                    break;
                }
            }
        }
        return ResponseJson.success(roleResultList);
    }


    /**
     * @param user      账号信息
     * @param checkNull 是否检查账号为空
     */
    public static void checkUserStatus(User user, boolean checkNull) {
        if (checkNull && user == null) {
            throw new BusinessException("账号不存在");
        }
        if (CodeCaption.STATUS_OK != user.getStatus()) {
            throw new BusinessException(PubCode.USER_STATUS.code, String.format(PubCode.USER_STATUS.message, user.getId()));
        }
    }

    /**
     * @param param 入参校验
     */
    private User checkUserParam(UserInsertParam param, Long id) throws BusinessException {
        User getUser = this.lambdaQuery().eq(User::getName, param.getName()).one();
        String oldName = null;
        if (id != null) {
            User user = super.getById(id);
            if (user == null) {
                throw new BusinessException("账号不存在");
            }
            oldName = user.getName();
        }
        if (getUser != null && !param.getName().equals(oldName)) {
            throw new BusinessException("姓名已被注册");
        }
        return User.builder().name(param.getName()).phone(param.getPhone()).role(param.getRole())
                .resources(JSON.toJSONString(param.getResources())).build();
    }

}
