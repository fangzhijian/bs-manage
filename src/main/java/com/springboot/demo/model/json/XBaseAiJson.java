package com.springboot.demo.model.json;

import lombok.Data;

/**
 *
 * @author fangzhijian
 * @Desc
 * @date 2025/10/11 15:00
 */
@Data
public class XBaseAiJson {
    Integer code;
    String message;
    XBaseAiData data;
}
