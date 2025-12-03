package com.springboot.demo.netty.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * {@code @description}
 * 消息返回体
 *
 * @author fangzhijian
 * @since 2025-11-27 13:13
 */
@Data
@Accessors(chain = true)
public class MessageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型：1-商品列表 2-定时数据
     */
    private Integer type;
}
