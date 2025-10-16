package com.bs.manage.mapper.console;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.console.BussProcessSummary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2020/3/12 18:59
 * fzj
 */
@Repository
public interface BussProcessSummaryMapper extends CommonMapper<BussProcessSummary> {

    /**
     * 批量插入或更新
     * 别更新释放的数量
     *
     * @param list 过程统计列表
     * @return 改动的条数
     */
    Integer insertDuplicateBatch(List<BussProcessSummary> list);

    /**
     * 根据唯一所有批量更新
     *
     * @param list 业务过程统计集合
     */
    void updateBatchByUnique(List<BussProcessSummary> list);

}
