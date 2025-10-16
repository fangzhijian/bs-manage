package com.bs.manage.service.configures;

import com.bs.manage.model.bean.console.BussProcessGoal;
import com.bs.manage.model.bean.console.BussResultGoal;
import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.model.param.configures.ProcessGoalDisplay;
import com.bs.manage.model.param.configures.ResultGoalDisplay;
import com.bs.manage.model.param.configures.ResultGoalResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 2020/3/12 10:49
 * fzj
 */
public interface BussGoalService {

    /**
     * 业务过程目标导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    ResponseJson processImport(MultipartFile file);

    /**
     * 业务结果目标导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    ResponseJson resultImport(MultipartFile file);


    List<ProcessGoalDisplay> getAllProcess(BussProcessGoal bussProcessGoal);

    ResponseJson updateProcess(BussProcessGoal bussProcessGoal);


    ResultGoalResult getAllResult(BussResultGoal bussResultGoal);


    ResponseJson updateResult(BussResultGoal bussResultGoal);


}
