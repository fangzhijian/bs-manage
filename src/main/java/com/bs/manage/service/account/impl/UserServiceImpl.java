package com.bs.manage.service.account.impl;

import com.bs.manage.code.CodeCaption;
import com.bs.manage.code.PubCode;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.exception.BusinessException;
import com.bs.manage.exception.MyRunException;
import com.bs.manage.mapper.account.UserRoleMapper;
import com.bs.manage.model.bean.account.UserRole;
import com.bs.manage.model.bean.common.CommonModel;
import com.bs.manage.model.bean.customer.CustomerRelease;
import com.bs.manage.model.bean.notify.NotifyCustomerRelease;
import com.bs.manage.model.json.Page;
import com.bs.manage.model.param.account.UserRoleResult;
import com.bs.manage.service.common.DingDingService;
import com.bs.manage.service.customer.CustomerReleaseService;
import com.bs.manage.service.customer.CustomerService;
import com.bs.manage.service.notify.NotifyCustomerReleaseService;
import com.bs.manage.until.CodeUtil;
import com.bs.manage.until.Md5Util;
import com.bs.manage.intercepter.UserToken;
import com.bs.manage.mapper.account.UserMapper;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.account.UserInsertParam;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.until.NumberUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 2020/1/15 10:59
 * fzj
 * 账号列表服务
 */
@Service
@Slf4j
public class UserServiceImpl extends CommonServiceImpl<User> implements UserService {

    private final Gson gson;
    private final UserToken userToken;
    private final DingDingService dingDingService;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final CustomerService customerService;
    private final CustomerReleaseService customerReleaseService;
    private final NotifyCustomerReleaseService notifyCustomerReleaseService;

    public UserServiceImpl(Gson gson, UserToken userToken, DingDingService dingDingService, UserMapper userMapper, UserRoleMapper userRoleMapper,
                           @Lazy CustomerService customerService, CustomerReleaseService customerReleaseService, NotifyCustomerReleaseService notifyCustomerReleaseService) {
        this.gson = gson;
        this.userToken = userToken;
        this.dingDingService = dingDingService;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.customerService = customerService;
        this.customerReleaseService = customerReleaseService;
        this.notifyCustomerReleaseService = notifyCustomerReleaseService;
    }


    @Override
    public void afterPropertiesSet() {
        setCommonMapper(userMapper);
        UserRole.userRoles = userRoleMapper.getAll();
    }

    /**
     * 团队在账号中是否存
     *
     * @param team_id 团IDid
     * @return true或false
     */
    @Override
    public Boolean existUserInTeam(Long team_id) {
        return userMapper.existUserInTeam(team_id);
    }

    /**
     * 创建账号
     */
    @Override
    @Transactional
    public ResponseJson insertUser(UserInsertParam param) throws BusinessException {
        //入参校验
        User user = checkUserParam(param, null);
        String password = StringUtils.hasText(param.getPassword()) ? Md5Util.md5(param.getPassword()) : Md5Util.DEFAULT_PASSWORD;
        String apiToken = Md5Util.md5(String.format("%s%s", param.getEmail(), param.getName()));
        user.setPassword(password);
        user.setApi_token(apiToken);
        super.insert(user);
        user.setApi_token(null);
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
        super.update(user);
        //当前账户若登陆更新其缓存
        user = super.getById(id);
        userToken.refreshCache(user);
        user.setApi_token(null);
        return ResponseJson.success(user);
    }

    /**
     * 账号状态关系变更
     *
     * @param id       账号id
     * @param type     操作类型 1-正常 2-停用 3-离职 4-绑定 5-解散 6-接管
     * @param other_id 另外一个账号id,绑定的是上级id,接管的是接管者的id
     * @return 是否变更成功
     */
    @Override
    @Transactional
    public ResponseJson changeRelation(Long id, Integer type, Long other_id) {
        User user = super.getById(id);
        if (user == null) {
            return ResponseJson.fail("账号不存在");
        }
        if (type == 5 || type == 6) {
            return ResponseJson.fail("不支持的操作");
        }

        //正常、停用、离职修改下状态就可以
        if (type == 1 || type == 2 || type == 3) {
            user.setStatus(type);
            super.update(user);
            //离职释放销售专员所有客户
            if (type == 3 && CodeCaption.ROLE_SALE == user.getRole()) {
                customerService.releaseAllByUserId(id);
            }

            //绑定上级
        } else if (type == 4) {
            User parent = super.getById(other_id);
            checkUserStatus(parent, true);
            //设置团队id
            if (CodeCaption.ROLE_SALE == user.getRole()) {
                if (CodeCaption.ROLE_SALE == parent.getRole() && NumberUtil.isBlank(parent.getTeam_id())) {
                    return ResponseJson.fail("上级销售还未加入一个团队");
                }
                if (NumberUtil.isNotBlank(parent.getTeam_id())) {
                    user.setTeam_id(parent.getTeam_id());
                }
            }
            user.setParent_id(other_id);
            super.update(user);
        }
        userToken.refreshCache(user);
        return ResponseJson.success();


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
        super.update(user);

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
        super.update(user);

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
        User user = super.getOneBySelectKey(User.builder().email(username).build());
        checkUserStatus(user, true);
        if (!Md5Util.verify(password, user.getPassword())) {
            return ResponseJson.fail("密码错误");
        }

        //放入缓存
        String apiToken = user.getApi_token();
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
            roleIds.addAll(UserRole.userRoles.stream().filter(x -> x.getId() != 19).map(UserRole::getId).collect(Collectors.toList()));
            roleIds.add(9);     //客户授权
            roleIds.add(1000);  //1000表示有下级
            context.setResources(gson.toJson(roleIds));
        } else {
            if (context.getHas_child() == CodeCaption.TRUE) {
                roleIds.add(1000);
                context.setResources(gson.toJson(roleIds));
            }
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
            userToken.putCache(user.getApi_token(), null);
        }
        return ResponseJson.success();
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
    @Override
    public Page<User> getByPage(Integer limit, Integer offset, Long team_id, String keyword, Long parentId) {
        Page<User> page = new Page<>();
        int total = userMapper.countByPage(team_id, keyword, parentId);
        page.setTotal(total);
        if (total > 0) {
            List<User> userList = userMapper.getByPage(limit, offset, team_id, keyword, parentId);
            for (User user : userList) {
                user.setRole_caption(CodeUtil.getCaption(CodeCaption.ROLE, user.getRole()));
                user.setStatus_caption(CodeUtil.getCaption(CodeCaption.USER_STATUS, user.getStatus()));
            }
            page.setItems(userList);
        }
        return page;
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

    @Override
    @Cacheable(cacheNames = RedisConstants.USER_SUBORDINATE_ALL, key = "#user.getId()")
    public List<User> subordinateForAll(User user) {
        List<User> users = super.getAllBySelectKey(User.builder().status(CodeCaption.STATUS_OK).build());
        List<User> sonUsers = new ArrayList<>();
        getSonUsers(users, sonUsers, user.getId());
        return sonUsers;
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.USER_SUBORDINATE_SALE, key = "#user.getId()")
    public List<User> subordinateForSale(User user) {
        List<User> sonSales = new ArrayList<>();
        if (CodeCaption.ROLE_SALE == user.getRole()) {
            if (user.getTeam_id() == null) {
                throw new MyRunException("你还未入加入一个团队,请联系你的经理或者管理员");
            }
            List<User> sales = super.getAllBySelectKey(User.builder().team_id(user.getTeam_id()).role(CodeCaption.ROLE_SALE).status(CodeCaption.STATUS_OK).build());
            getSonUsers(sales, sonSales, user.getId());
            //所有下级并加上自己
            sonSales.add(user);

        } else if (CodeCaption.ROLE_ADMIN == user.getRole()) {
            sonSales = super.getAllBySelectKey(User.builder().role(CodeCaption.ROLE_SALE).status(CodeCaption.STATUS_OK).build());
        }
        return sonSales;

    }

    /**
     * 新增客户释放提醒,每天重新写入数据
     */
    @Transactional
    public void insertToNotifyCustomerRelease() {
        List<User> users = this.getAllBySelectKey(User.builder().role(CodeCaption.ROLE_SALE).status(CodeCaption.STATUS_OK).build());
        List<NotifyCustomerRelease> notifyCustomerReleaseList = new ArrayList<>();
        for (User user : users) {
            try {
                Thread.sleep(600);
                log.error("开始写入{}的客户释放通知", user.getName());
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            List<Long> process_user_ids = new ArrayList<>();
            if (CodeCaption.TRUE == user.getHas_child() && user.getTeam_id() != null) {
                List<User> sonUsers = new ArrayList<>();
                getSonUsers(users, sonUsers, user.getId());
                process_user_ids = sonUsers.stream().map(CommonModel::getId).collect(Collectors.toList());
            }
            process_user_ids.add(user.getId());

            List<CustomerRelease> customerReleaseList = customerReleaseService.getAllBySelectKey(CustomerRelease.builder().process_user_ids(process_user_ids).build());
            for (CustomerRelease customerRelease : customerReleaseList) {
                NotifyCustomerRelease notifyCustomerRelease = new NotifyCustomerRelease();
                BeanUtils.copyProperties(customerRelease, notifyCustomerRelease);
                notifyCustomerRelease.setCustomer_id(customerRelease.getId());
                notifyCustomerRelease.setCustomer_name(customerRelease.getName());
                notifyCustomerRelease.setNotify_user(user.getId());
                notifyCustomerRelease.setHas_read(CodeCaption.FALSE);
                notifyCustomerReleaseList.add(notifyCustomerRelease);
            }
        }
        notifyCustomerReleaseService.truncate();
        if (notifyCustomerReleaseList.size() > 0) {
            notifyCustomerReleaseService.insertBatch(notifyCustomerReleaseList);
        }

    }

    @Override
    public List<User> getByIds(List<Long> list) {
        return userMapper.getByIds(list);
    }

    @Override
    public void insertTest() {
        userMapper.insert(User.builder().name("李四").email("78").password("23").build());
    }

    /**
     * @param user      账号信息
     * @param checkNull 是否检查账号为空
     */
    public static void checkUserStatus(User user, boolean checkNull) {
        if (checkNull && user == null) {
            throw new MyRunException("账号不存在");
        }
        if (CodeCaption.STATUS_OK != user.getStatus()) {
            throw new MyRunException(PubCode.USER_STATUS.code(), String.format(PubCode.USER_STATUS.message(), user.getId()));
        }
    }

    /**
     * @param param 入参校验
     */
    private User checkUserParam(UserInsertParam param, Long id) throws BusinessException {
        User getUser = super.getOneBySelectKey(User.builder().email(param.getEmail()).build());
        String oldEmail = null;
        boolean getDingDingUserId = true;
        if (id != null) {
            User user = super.getById(id);
            if (user == null) {
                throw new BusinessException("账号不存在");
            }
            oldEmail = user.getEmail();
            if (param.getPhone().equals(user.getPhone())) {
                getDingDingUserId = false;
            }
        }
        if (getUser != null && !param.getEmail().equals(oldEmail)) {
            throw new BusinessException("邮箱已被注册");
        }

        //获取钉钉userId
        String ddUserId = null;
        if (getDingDingUserId) {
            ResponseJson json = dingDingService.getUserId(param.getPhone());
            if (PubCode.SUCCESS_CODE != json.getCode()) {
                throw new BusinessException(json.getCode(), json.getMsg());
            }
            ddUserId = (String) json.getData();
        }


        return User.builder().name(param.getName()).email(param.getEmail()).phone(param.getPhone()).dd_user_id(ddUserId).role(param.getRole())
                .resources(gson.toJson(param.getResources())).customer_limit(param.getCustomer_limit())
                .position(param.getPosition()).build();
    }


    private void getSonUsers(List<User> users, List<User> sonUsers, Long parentId) {
        for (User user : users) {
            if (parentId.equals(user.getParent_id())) {
                sonUsers.add(user);
                getSonUsers(users, sonUsers, user.getId());
            }
        }
    }

}
