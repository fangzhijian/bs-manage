package com.bs.manage.controller.actor;

import com.bs.manage.annotation.Role;
import com.bs.manage.model.bean.actor.Actor;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.actor.ActorSearchParam;
import com.bs.manage.service.actor.ActorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



/**
 * 2020/1/15 17:06
 * fzj
 * 达人列表控制器 (差excel导入导出接口)
 */
@RestController
@Validated
@RequestMapping("admin/actor")
@Role
public class ActorController {


    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }


    /**
     * 录入达人
     *
     * @param actor 详见Actor
     * @return 新增后的达人信息
     */
    @PostMapping
    public ResponseJson insert(@Validated @RequestBody Actor actor) {
        return actorService.insert(actor);
    }

    /**
     * 删除达人
     *
     * @param id 达人id
     * @return 删除状态信息
     */
    @DeleteMapping("{id}")
    public ResponseJson delete(@PathVariable("id") Long id) {
        return actorService.delete(id);
    }

    /**
     * 更新达人
     *
     * @param actor 详见Actor
     * @return 更新后的达人信息
     */
    @PutMapping("{id}")
    public ResponseJson update(@PathVariable("id") Long id, @Validated @RequestBody Actor actor) {
        actor.setId(id);
        return actorService.update(actor);
    }


    /**
     * 达人列表
     *
     * @return 分页所得达人列表
     */
    @GetMapping("getByPage")
    public ResponseJson getByPage(@Validated ActorSearchParam param) {
        return actorService.getByPage(param);
    }

    /**
     * 达人详情
     * 查询达人跟进列表
     *
     * @param id 达人id
     * @return 单个达人详细信息
     */
    @GetMapping("{id}/process_all")
    @Role(roleId = 14)
    public ResponseJson processAll(@PathVariable("id") Long id) {
        return actorService.processAll(id);
    }


    /**
     * 导入达人
     *
     * @param file excel
     * @return 导入结果
     */
    @PostMapping("import")
    public ResponseJson importActor(@RequestParam("file") MultipartFile file) {
        return actorService.importActor(file);
    }

    @PostMapping("exportList")
    public void exportList(ActorSearchParam param, HttpServletResponse response) {
        actorService.exportList(param, response);
    }
}
