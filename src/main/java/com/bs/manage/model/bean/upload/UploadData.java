package com.bs.manage.model.bean.upload;

import com.bs.manage.model.bean.common.CommonModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;

/**
 * 2020/4/15 13:19
 * fzj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("uploadData")
@SuperBuilder
@NoArgsConstructor
public class UploadData extends CommonModel {

    private static final long serialVersionUID = 7729164637237256438L;

    private String original_name;       //原始文件名
    private String file_name;           //文件名
    private String compress_name;       //压缩文件名
    private Long size;                  //大小
    private Integer height;             //宽
    private Integer width;              //高
    private String url;                 //访问链接
    private String compress_url;        //压缩文件的访问链接
    private Integer has_use;            //是否在使用,方便定期清理,0-否 1-是
    private Integer warn_level;         //警告级别 1-正常 2-涉嫌 3-违规 4-严重
    private Integer warn_level_label;   //警告级别说明
    private Integer upload_status;      //上传状态 0-失败 1-成功
    private String upload_msg;          //上传错误信息
    private Integer source;             //来源 1-授权证书
    private String source_label;        //来源说明
    private String remark;              //备注

}
