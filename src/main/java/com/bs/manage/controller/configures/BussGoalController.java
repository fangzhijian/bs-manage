package com.bs.manage.controller.configures;

import com.bs.manage.annotation.Role;
import com.bs.manage.model.bean.console.BussProcessGoal;
import com.bs.manage.model.bean.console.BussResultGoal;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.configures.BussGoalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 2020/3/12 10:14
 * fzj
 */
@RestController
@RequestMapping("admin/goal")
@Role(isAdmin = true)
public class BussGoalController {

    private final BussGoalService bussGoalService;

    public BussGoalController(BussGoalService bussGoalService) {
        this.bussGoalService = bussGoalService;
    }

    /**
     * 业务过程目标导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    @PostMapping("process/import")
    public ResponseJson processImport(@RequestParam("file") MultipartFile file) {
        return bussGoalService.processImport(file);
    }

    /**
     * 业务结果目标导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    @PostMapping("result/import")
    public ResponseJson resultImport(@RequestParam("file") MultipartFile file) {
        return bussGoalService.resultImport(file);
    }

    @GetMapping("process")
    public ResponseJson getAllProcess(BussProcessGoal bussProcessGoal) {
        return ResponseJson.success(bussGoalService.getAllProcess(bussProcessGoal));
    }

    @PutMapping("process")
    public ResponseJson updateProcess(BussProcessGoal bussProcessGoal) {
        return bussGoalService.updateProcess(bussProcessGoal);
    }


    @GetMapping("result")
    public ResponseJson getAllResult(BussResultGoal bussResultGoal) {
        return ResponseJson.success(bussGoalService.getAllResult(bussResultGoal));
    }

    @PutMapping("result")
    public ResponseJson updateResult(BussResultGoal bussResultGoal) {
        return bussGoalService.updateResult(bussResultGoal);
    }
}
