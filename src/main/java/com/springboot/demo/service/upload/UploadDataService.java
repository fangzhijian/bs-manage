package com.springboot.demo.service.upload;

import org.springframework.web.multipart.MultipartFile;



/**
 * 2020/4/15 9:47
 * fzj
 */
public interface UploadDataService {

    /**
     * 批量上传文件
     *
     * @param files  上传的文件
     * @param source 上传的文件来源 1-授权证书
     */
    void upload(MultipartFile[] files, Integer source);

}
