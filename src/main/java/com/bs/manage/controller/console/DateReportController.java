package com.bs.manage.controller.console;

import com.bs.manage.model.bean.console.DateReport;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.console.DateReportSearchParam;
import com.bs.manage.service.console.DateReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2020/3/4 11:29
 * fzj
 * 日报
 */
@RestController
@RequestMapping("admin/console/report")
public class DateReportController {

    private final DateReportService dateReportService;

    public DateReportController(DateReportService dateReportService) {
        this.dateReportService = dateReportService;
    }

    @PostMapping()
    public ResponseJson insert(@Validated @RequestBody DateReport dateReport) {
        return dateReportService.insert(dateReport);
    }

    @DeleteMapping("{id}")
    public ResponseJson delete(@PathVariable("id") Long id) {
        return dateReportService.delete(id);
    }

    @PutMapping("{id}")
    public ResponseJson update(@PathVariable("id") Long id, @Validated @RequestBody DateReport dateReport) {
        dateReport.setId(id);
        return dateReportService.update(dateReport);
    }

    @GetMapping
    public ResponseJson getByPage(@Validated DateReportSearchParam param) {
        return ResponseJson.success(dateReportService.getByPage(param));
    }

    /**
     * 查询当前账号拥有的客户
     *
     * @return 当前账号拥有的客户
     */
    @GetMapping("owm_customer")
    public ResponseJson getOwmCustomer() {
        return ResponseJson.success(dateReportService.getOwmCustomer());
    }
}
