package com.bs.manage.service.upload;

import com.bs.manage.model.bean.upload.UploadData;
import com.bs.manage.service.common.CommonService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 2020/4/15 9:47
 * fzj
 */
public interface UploadDataService extends CommonService<UploadData> {

    /**
     * 批量上传文件
     *
     * @param files  上传的文件
     * @param source 上传的文件来源 1-授权证书
     */
    List<UploadData> upload(MultipartFile[] files, Integer source);

    /**
     * 标记是否使用,优先使用id匹配
     *
     * @param hasUse      0-未使用 1-使用
     * @param id          图片id
     * @param url         文件访问链接
     * @param compressUrl 压缩文件的访问链接
     */
    void markUse(Integer hasUse, Long id, String url, String compressUrl);
}
