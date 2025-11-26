package com.bs.manage.netty;

import lombok.Data;

/**
 * {@code @description}
 * 统一消息实体
 *
 * @author fangzhijian
 * @since 2025-11-26 22:35
 */
@Data
public class ChatMessage {
    // 消息类型：1-登录 2-单聊 3-群聊 4-上下线通知
    private Integer type;
    // 发送者ID（客户端唯一标识）
    private String senderId;
    // 接收者ID（单聊时指定，群聊为 "all"）
    private String receiverId;
    // 消息内容
    private String content;
    // 发送时间（毫秒级）
    private Long timestamp;
}
