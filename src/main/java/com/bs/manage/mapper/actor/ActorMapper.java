package com.bs.manage.mapper.actor;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.actor.Actor;
import com.bs.manage.model.param.actor.ActorSearchParam;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/3/5 15:14
 * fzj
 */
@Repository
public interface ActorMapper extends CommonMapper<Actor> {

    String getMaxSn();

    Integer countByPage(ActorSearchParam param);

    List<Actor> getByPage(ActorSearchParam param);

}
