package com.bs.manage.controller.configures;

import com.bs.manage.annotation.Role;
import com.bs.manage.constant.RedisConstants;
import com.bs.manage.model.bean.configures.Configures;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.configures.ConfiguresService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2020/3/12 10:00
 * fzj
 */
@RestController
@RequestMapping("admin/configure")
@Role(isAdmin = true)
public class ConfiguresController {

    private final ConfiguresService configuresService;

    public ConfiguresController(ConfiguresService configuresService) {
        this.configuresService = configuresService;
    }

    @PostMapping
    @CacheEvict(cacheNames = RedisConstants.CONFIGURE, allEntries = true)
    public ResponseJson insert(@Validated Configures configures) {
        return configuresService.insert(configures);
    }

    @PutMapping("{id}")
    @CacheEvict(cacheNames = RedisConstants.CONFIGURE, allEntries = true)
    public ResponseJson update(@PathVariable("id") Long id, @Validated Configures configures) {
        configures.setId(id);
        configures.setCode(null);
        return configuresService.update(configures);
    }

    @GetMapping
    public ResponseJson getAll() {
        return ResponseJson.success(configuresService.getAll());
    }
}
