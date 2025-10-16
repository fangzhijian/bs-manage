package com.bs.manage.service.account;

import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.CommonService;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 2020/1/20 16:51
 * fzj
 * 团队管理接口
 */
public interface TeamService extends CommonService<Team> {

    /**
     *
     * @param id        团队id
     * @param user_id   账号id
     * @return          绑定leader结果
     */
    ResponseJson bindLeader(Long id, Long user_id);
}
