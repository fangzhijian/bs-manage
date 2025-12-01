package com.bs.manage.service.account;


import com.bs.manage.exception.BusinessException;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.account.UserInsertParam;
import com.bs.manage.service.common.CommonService;

import java.util.List;


/**
 * 2020/1/15 10:57
 * fzj
 * 账号列表接口
 */
public interface UserService extends CommonService<User> {

    /**
     * 团队在账号中是否存
     *
     * @param team_id 团IDid
     * @return true或false
     */
    Boolean existUserInTeam(Long team_id);

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


    ResponseJson changeRelation(Long id, Integer type, Long other_id);

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
     * 账号列表
     *
     * @param limit    每页条数
     * @param offset   第几条开始
     * @param team_id  渠道id
     * @param keyword  模糊查询登陆邮箱或者姓名
     * @param parentId 上级id
     * @return 分页所得账号列表
     */
    Page<User> getByPage(Integer limit, Integer offset, Long team_id, String keyword, Long parentId);

    /**
     * 获取账号权限表信息
     *
     * @return 所有账号权限
     */
    ResponseJson getUserRoles();

    /**
     * @return 所有下属成员
     */
    List<User> subordinateForAll(User user);

    /**
     * @return 所有下属销售成员
     */
    List<User> subordinateForSale(User user);


    /**
     * 新增客户释放提醒,每天重新写入数据
     */
    void insertToNotifyCustomerRelease();

    List<User> getByIds(List<Long> list);

    void insertTest();
}
