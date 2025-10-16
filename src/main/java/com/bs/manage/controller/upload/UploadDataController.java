package com.bs.manage.controller.upload;

import com.bs.manage.model.json.ResponseJson;
import com.bs.manage.service.upload.UploadDataService;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 2020/4/14 17:14
 * fzj
 */
@RestController
@RequestMapping("admin/upload")
public class UploadDataController {

    private final UploadDataService uploadDataService;

    public UploadDataController(UploadDataService uploadDataService) {
        this.uploadDataService = uploadDataService;
    }


    /**
     * TODO 压缩功能还未实现
     * 上传单个文件
     *
     * @param file     上传的文件
     * @param source   文件的来源 1-授权证书
     * @param compress 是否需要压缩,默认只保存原图
     * @param original 压缩时否需要原图,默认压缩时不保留原图
     * @return 文件的url
     */
    @PostMapping("single")
    public ResponseJson uploadSingle(@RequestParam("file") MultipartFile file, @NotNull @Range(min = 1, max = 1) Integer source,
                                     Boolean compress, Boolean original) {
        if (file.isEmpty()) {
            return ResponseJson.success("至少上传一个文件");
        }
        MultipartFile[] multipartFiles = new MultipartFile[1];
        multipartFiles[0] = file;
        return ResponseJson.success(uploadDataService.upload(multipartFiles, source).get(0));
    }

    /**
     * TODO 压缩功能还未实现
     * 批量上传文件
     *
     * @param files    上传的文件
     * @param source   文件的来源 1-授权证书
     * @param compress 是否需要压缩,默认只保存原图
     * @param original 压缩时否需要原图,默认压缩时不保留原图
     * @return 多个文件的上传结果
     */
    @PostMapping("multi")
    public ResponseJson uploadMulti(@RequestParam("files") MultipartFile[] files, @NotNull @Range(min = 1, max = 1) Integer source,
                                    Boolean compress, Boolean original) {
        //检验文件名是否重复
        Map<String, Integer> fileNameMap = new HashMap<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                if (fileNameMap.get(file.getName()) != null) {
                    return ResponseJson.fail(String.format("文件名不能重复,文件名%S", file.getName()));
                }
                fileNameMap.put(file.getName(), 1);
            }
        }

        return ResponseJson.success(uploadDataService.upload(files, source));
    }

}
