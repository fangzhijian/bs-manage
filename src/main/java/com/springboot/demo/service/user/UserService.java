package com.springboot.demo.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.demo.exception.BusinessException;
import com.springboot.demo.model.bean.account.User;
import com.springboot.demo.model.json.ResponseJson;
import com.springboot.demo.model.param.user.UserInsertParam;

/**
 * 2020/1/15 10:57
 * fzj
 * 账号列表接口
 */
public interface UserService extends IService<User> {


    /**
     * 创建账号
     */
    ResponseJson insertUser(UserInsertParam param) throws BusinessException;

    /**
     * 修改账号
     *
     * @param id 账号id
     */
    ResponseJson updateUser(Long id, UserInsertParam param) throws BusinessException;

    /**
     * 重置账号密码
     *
     * @param id 账号id
     * @return 成功或失败
     */
    ResponseJson resetPassword(Long id);

    /**
     * 修改密码
     *
     * @param old_pass 旧密码
     * @param new_pass 新密码
     * @return 是否修改成功
     */
    ResponseJson changePassword(String old_pass, String new_pass);

    /**
     * 账号登录
     *
     * @param username 登录名 这里用邮箱
     * @param password 密码
     * @return 返回token
     */
    ResponseJson login(String username, String password);

    /**
     * @return 获取账号信息
     */
    ResponseJson getUserInfo();


    /**
     * 账号登出
     *
     * @param api_token 带时间加密的token
     * @return 返回是否登出
     */
    ResponseJson logout(String api_token);

    /**
     * 获取账号权限表信息
     *
     * @return 所有账号权限
     */
    ResponseJson getUserRoles();

    /**
     * 测试
     */
    void test();
}
