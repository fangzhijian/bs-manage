package com.bs.manage.service.region;

import com.bs.manage.model.bean.region.Region;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.common.CommonService;

/**
 * 2020/3/2 15:16
 * fzj
 */
public interface RegionService extends CommonService<Region> {

    ResponseJson getAllRegion();

}
