package com.springboot.demo.netty.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * {@code @description}
 * 消息入参
 *
 * @author fangzhijian
 * @since 2025-11-27 13:13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MessageParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 消息类型：1-登录 2-直播间消息
     */
    @NotNull
    private Integer type;
    /**
     * 直播间id
     */
    @NotNull
    private Long liveId;
    /**
     * 消息内容，type=2时传
     */
    private MessageInfo messageInfo;
}
