package com.bs.manage.controller.account;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.account.Team;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.account.TeamService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 2020/1/15 15:26
 * fzj
 * 团队管理控制器
 */
@RestController
@RequestMapping("admin/team")
@Validated
@Role(isAdmin = true)
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.TEAM, allEntries = true)
    public ResponseJson insert(@NotBlank String name) {
        return teamService.insert(Team.builder().name(name).build());
    }

    @DeleteMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.TEAM, allEntries = true)
    public ResponseJson delete(@PathVariable("id") Long id) {
        return teamService.delete(id);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.TEAM, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @NotBlank String name) {
        return teamService.update(Team.builder().id(id).name(name).build());
    }

    /**
     * @param id      团队id
     * @param user_id 账号id
     * @return 绑定leader结果
     */
    @PutMapping("/bind_leader/{id}")
    @CacheEvict(cacheNames = RedisConstants.TEAM, allEntries = true)
    public ResponseJson bindLeader(@PathVariable("id") Long id, Long user_id) {
        return teamService.bindLeader(id, user_id);
    }

    @GetMapping
    public ResponseJson getAll() {
        return ResponseJson.success(teamService.getAll());
    }

}
