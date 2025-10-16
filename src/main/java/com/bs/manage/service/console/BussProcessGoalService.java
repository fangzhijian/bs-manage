package com.bs.manage.service.console;

import com.bs.manage.model.bean.console.BussProcessGoal;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2020/3/12 19:11
 * fzj
 */
public interface BussProcessGoalService extends CommonService<BussProcessGoal> {

    /**
     * 批量插入或更新
     * @param list 过程目标列表
     * @return 改动的条数
     */
    Integer insertDuplicateBatch(List<BussProcessGoal> list);

}
