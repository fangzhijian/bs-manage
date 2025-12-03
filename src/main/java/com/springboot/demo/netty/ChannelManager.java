package com.springboot.demo.netty;

import com.alibaba.fastjson2.JSON;
import com.springboot.demo.netty.message.MessageVo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
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

    // clientId为ip+端口号，在http端同一个设备和ip每次建立长链接的端口号都会不同
    // 存储在线客户端：key=clientId，value=Channel扩展类
    private static final Map<String, ChannelExtend> CLIENT_CHANNEL_MAP = new ConcurrentHashMap<>();
    // 所有在线 Channel 分组（用于群聊广播），key=直播间id，value=ChannelGroup
    private static final Map<Long, ChannelGroup> LIVE_GROUP_MAP = new ConcurrentHashMap<>();


    // 添加客户端连接
    public static void addChannel(String clientId, Long liveId, Channel channel) {
        CLIENT_CHANNEL_MAP.put(clientId, ChannelExtend.of(channel, liveId));
        LIVE_GROUP_MAP.compute(liveId, (key, value) -> value == null ?
                new DefaultChannelGroup(GlobalEventExecutor.INSTANCE) : value).add(channel);
    }

    // 移除客户端连接，并返回直播间id
    public static Long removeChannel(String clientId) {
        Long liveId = null;
        ChannelExtend channelExtend = CLIENT_CHANNEL_MAP.remove(clientId);
        if (channelExtend != null) {
            liveId = channelExtend.getLiveId();
            ChannelGroup channelGroup = LIVE_GROUP_MAP.get(liveId);
            if (channelGroup != null && !channelGroup.isEmpty()) {
                channelGroup.remove(channelExtend.getChannel());
            }
        }
        return liveId;
    }


    //获取Channel分组
    public static ChannelGroup getChannelGroup(Long liveId) {
        return LIVE_GROUP_MAP.get(liveId);
    }

    //获取所有在线Channel分组
    public static Map<Long, ChannelGroup> getChannelGroupMap() {
        return LIVE_GROUP_MAP;
    }

    // 发送消息
    public static void sendMessage(Channel channel, MessageVo messageVo) {
        if (channel != null) {
            String json = JSON.toJSONString(messageVo);
            //使用零拷贝发送消息
            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
            try {
                byteBuf.writeCharSequence(json, StandardCharsets.UTF_8);
                channel.writeAndFlush(new TextWebSocketFrame(byteBuf.retain()));
            } finally {
                byteBuf.release();
            }
        }
    }

    // 群聊广播消息
    public static void broadcast(ChannelGroup channelGroup, MessageVo messageVo) {
        if (channelGroup != null && !channelGroup.isEmpty()) {
            String json = JSON.toJSONString(messageVo);
            //使用零拷贝发送消息
            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
            try {
                byteBuf.writeCharSequence(json, StandardCharsets.UTF_8);
                channelGroup.writeAndFlush(new TextWebSocketFrame(byteBuf.retain()));
            } finally {
                byteBuf.release();
            }
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    private static class ChannelExtend {
        /**
         * Channel
         */
        private Channel channel;
        /**
         * 直播间id
         */
        private Long liveId;
    }
}
