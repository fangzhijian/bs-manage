package com.bs.manage.netty;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code @description}
 * 在线用户连接管理（线程安全）
 *
 * @author fangzhijian
 * @since 2025-11-26 22:37
 */
public class ChannelManager {

    // 存储在线用户：key=userId，value=Channel
    public static final Map<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    // 所有在线 Channel 分组（用于群聊广播）
    private static final ChannelGroup ALL_CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 添加用户连接
    public static void addUser(String userId, Channel channel) {
        USER_CHANNEL_MAP.put(userId, channel);
        ALL_CHANNEL_GROUP.add(channel);
    }

    // 移除用户连接
    public static void removeUser(String userId) {
        Channel channel = USER_CHANNEL_MAP.remove(userId);
        if (channel != null) {
            ALL_CHANNEL_GROUP.remove(channel);
        }
    }

    // 根据 userId 获取 Channel
    public static Channel getUserChannel(String userId) {
        return USER_CHANNEL_MAP.get(userId);
    }

    // 获取所有在线用户ID
    public static String[] getAllOnlineUserIds() {
        return USER_CHANNEL_MAP.keySet().toArray(new String[0]);
    }

    // 群聊广播消息
    public static void broadcast(ChatMessage message) {
        String json = JSON.toJSONString(message);
        ALL_CHANNEL_GROUP.writeAndFlush(new TextWebSocketFrame(json));
    }
}
