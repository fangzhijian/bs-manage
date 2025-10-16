package com.bs.manage.mapper.kpi;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.kpi.KpiMain;
import com.bs.manage.model.param.kpi.KpiListParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 2020/6/15 16:28
 * fzj
 */
@Repository
public interface KpiMainMapper extends CommonMapper<KpiMain> {

    Integer countByPage(KpiListParam param);

    List<KpiMain> getByPage(KpiListParam param);

    List<KpiMain> getByUserIds(@Param("month")Integer month,@Param("userIds")List<Long> userIds);
}
