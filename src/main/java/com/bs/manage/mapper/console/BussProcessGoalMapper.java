package com.bs.manage.mapper.console;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.console.BussProcessGoal;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/3/12 11:12
 * fzj
 */
@Repository
public interface BussProcessGoalMapper extends CommonMapper<BussProcessGoal> {

    /**
     * 批量插入或更新
     * @param list 过程目标列表
     * @return 改动的条数
     */
    Integer insertDuplicateBatch(List<BussProcessGoal> list);

}
