package com.springboot.demo.controller.upload;

import com.springboot.demo.model.json.ResponseJson;
import com.springboot.demo.service.upload.UploadDataService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class UploadDataController {

    private final UploadDataService uploadDataService;


    /**
     * 上传单个文件
     *
     * @param file     上传的文件
     * @param source   文件的来源 1-授权证书
     * @return 文件的url
     */
    @PostMapping("single")
    public ResponseJson uploadSingle(@RequestParam("file") MultipartFile file, @NotNull @Range(min = 1, max = 1) Integer source) {
        if (file.isEmpty()) {
            return ResponseJson.success("至少上传一个文件");
        }
        MultipartFile[] multipartFiles = new MultipartFile[1];
        multipartFiles[0] = file;
        uploadDataService.upload(multipartFiles, source);
        return ResponseJson.success();
    }

    /**
     * 批量上传文件
     *
     * @param files    上传的文件
     * @param source   文件的来源 1-授权证书
     * @return 多个文件的上传结果
     */
    @PostMapping("multi")
    public ResponseJson uploadMulti(@RequestParam("files") MultipartFile[] files, @NotNull @Range(min = 1, max = 1) Integer source) {
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
        uploadDataService.upload(files, source);
        return ResponseJson.success();
    }

}
