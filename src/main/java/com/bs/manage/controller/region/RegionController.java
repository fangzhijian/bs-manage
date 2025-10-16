package com.bs.manage.controller.region;

import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.region.RegionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 2020/3/2 15:14
 * fzj
 */
@RestController
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping("admin/region")
    @Cacheable(cacheNames = RedisConstants.REGION)
    public ResponseJson getAllRegion() {
        return ResponseJson.success(regionService.getAllRegion());
    }


}
