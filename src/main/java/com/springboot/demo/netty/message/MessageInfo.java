package com.springboot.demo.netty.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * {@code @description}
 * 消息内容
 * @author fangzhijian
 * @since 2025-11-26 13:28
 */
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class MessageInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 消息
     */
    private String message;

}
