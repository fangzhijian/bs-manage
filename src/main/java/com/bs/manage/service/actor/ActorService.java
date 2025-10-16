package com.bs.manage.service.actor;

import com.bs.manage.model.bean.actor.Actor;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.actor.ActorSearchParam;
import com.bs.manage.service.common.CommonService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;


/**
 * 2020/3/5 15:15
 * fzj
 */
public interface ActorService extends CommonService<Actor> {

    ResponseJson getByPage(ActorSearchParam param);

    ResponseJson processAll(Long id);

    ResponseJson importActor(MultipartFile file);

    void exportList(ActorSearchParam param, HttpServletResponse response);
}
