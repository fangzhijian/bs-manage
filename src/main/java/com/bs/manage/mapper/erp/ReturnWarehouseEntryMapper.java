package com.bs.manage.mapper.erp;

import com.bs.manage.mapper.common.CommonMapper;
import com.bs.manage.model.bean.erp.ReturnWarehouseEntry;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 2023/4/10 17:14
 * fzj
 */
@Repository
public interface ReturnWarehouseEntryMapper extends CommonMapper<ReturnWarehouseEntry> {

    //获取直播订单
    List<ReturnWarehouseEntry> getLiveOrder(Integer id);
}
