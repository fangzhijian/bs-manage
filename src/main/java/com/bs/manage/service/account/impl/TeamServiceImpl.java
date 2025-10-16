package com.bs.manage.service.account.impl;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.mapper.account.TeamMapper;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.bean.account.User;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.account.TeamService;
import com.bs.manage.service.account.UserService;
import com.bs.manage.service.common.impl.CommonServiceImpl;
import com.bs.manage.until.NumberUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2020/1/20 16:53
 * fzj
 * 团队管理服务
 */
@Service
public class TeamServiceImpl extends CommonServiceImpl<Team> implements TeamService {

    private final TeamMapper teamMapper;
    private final UserService userService;

    public TeamServiceImpl(TeamMapper teamMapper, UserService userService) {
        this.teamMapper = teamMapper;
        this.userService = userService;
    }


    @Override
    @Transactional
    public ResponseJson delete(Long id) {
        if (userService.existUserInTeam(id)) {
            return ResponseJson.fail("团队已有成员存在,无法删除");
        }
        return super.delete(id);
    }

    @Override
    @Cacheable(cacheNames = RedisConstants.TEAM)
    public List<Team> getAll() {
        return super.getAll();
    }

    @Override
    public void afterPropertiesSet() {
        setCommonMapper(teamMapper);
    }

    /**
     * @param id      团队id
     * @param user_id 账号id
     * @return 绑定leader结果
     */
    @Override
    @Transactional
    public ResponseJson bindLeader(Long id, Long user_id) {
        Team team = super.getById(id);
        if (team == null) {
            return ResponseJson.fail("团队不存在");
        }
        if (NumberUtil.isNotBlank(team.getLeader_id())) {
            return ResponseJson.fail("leader已经存在");
        }
        //判断账号有无资格成为leader
        User user = userService.getById(user_id);
        UserServiceImpl.checkUserStatus(user, true);
        //设置leader_id
        team.setLeader_id(user_id);
        super.update(team);

        //设置leader上下级关系属性
        user.setTeam_id(id);
        userService.update(user);
        return ResponseJson.success();
    }
}
