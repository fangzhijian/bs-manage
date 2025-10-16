package com.bs.manage.model.json;

import lombok.Data;

/**
 *
 * @author fangzhijian
 * @Desc
 * @date 2025/10/11 15:43
 */
@Data
public class XBaseStreamJson {

    String chat_id;
    String chat_record_id;
    Boolean is_end;
    String content;

}
