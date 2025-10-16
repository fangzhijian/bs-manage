package com.bs.manage.service.erp;

import com.bs.manage.model.bean.erp.ReturnWarehouseEntry;
import com.bs.manage.service.common.CommonService;

import java.util.List;

/**
 * 2023/4/10 17:20
 * fzj
 */
public interface ReturnWarehouseEntryService extends CommonService<ReturnWarehouseEntry> {

    //获取直播订单
    List<ReturnWarehouseEntry> getLiveOrder(Integer id);

}
