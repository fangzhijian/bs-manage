package com.bs.manage.netty;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

/**
 * {@code @description}
 * 聊天服务端业务处理器
 *
 * @author fangzhijian
 * @since 2025-11-26 22:43
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 接收客户端消息（SimpleChannelInboundHandler 自动释放消息资源）
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        log.info("收到客户端消息：{}", frame.text());
        // 1. 解析 JSON 消息
        ChatMessage message = JSON.parseObject(frame.text(), ChatMessage.class);
        // 2. 根据消息类型处理业务
        switch (message.getType()) {
            case 1: // 登录：绑定 userId 和 Channel
                handleLogin(ctx, message);
                break;
            case 2: // 单聊：转发给指定接收者
                handleSingleChat(message);
                break;
            case 3: // 群聊：广播给所有在线用户
                handleGroupChat(message);
                break;
            default:
                log.error("不支持的消息类型：{}", message.getType());
        }
    }

    //客户端建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("客户端已建立连接，ip：{} 端口：{}", socketAddress.getAddress().getHostAddress(), socketAddress.getPort());
    }

    // 客户端断开连接
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // 移除离线用户
        String offlineUserId = getUserIdByChannel(ctx.channel());
        if (offlineUserId != null) {
            ChannelManager.removeUser(offlineUserId);
            log.info("用户离线：{}，当前在线人数：{}", offlineUserId, ChannelManager.getAllOnlineUserIds().length);

            // 广播离线通知
            ChatMessage offlineNotice = buildNoticeMessage(offlineUserId + " 已离线");
            offlineNotice.setSenderId("system");
            ChannelManager.broadcast(offlineNotice);
        }
    }

    // 空闲检测事件（超时关闭连接）
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            log.info("客户端空闲超时，关闭连接");
            ctx.close();
        }
    }

    // 异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            log.info("连接异常：{}", cause.getMessage());
        } else {
            log.error("连接异常：", cause);
        }
        ctx.close();
    }

    // 处理登录（客户端连接后发送登录消息，绑定 userId）
    private void handleLogin(ChannelHandlerContext ctx, ChatMessage message) {
        String userId = message.getSenderId();
        if (userId == null || userId.isEmpty()) {
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(buildNoticeMessage("登录失败：用户ID不能为空"))));
            ctx.close();
            return;
        }

        // 绑定用户与 Channel
        ChannelManager.addUser(userId, ctx.channel());
        log.info("用户登录：{}，当前在线人数：{}", userId, ChannelManager.getAllOnlineUserIds().length);

        // 发送登录成功通知 + 在线用户列表
        ChatMessage loginNotice = buildNoticeMessage("登录成功，当前在线用户：" + String.join(",", ChannelManager.getAllOnlineUserIds()));
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(loginNotice)));

        // 向其他用户广播上线通知
        ChatMessage onlineNotice = buildNoticeMessage(userId + " 已上线");
        onlineNotice.setSenderId("system");
        ChannelManager.broadcast(onlineNotice);
    }

    // 处理单聊
    private void handleSingleChat(ChatMessage message) {
        String receiverId = message.getReceiverId();
        Channel receiverChannel = ChannelManager.getUserChannel(receiverId);
        if (receiverChannel == null || !receiverChannel.isActive()) {
            // 接收者不在线，回复发送者
            Channel senderChannel = ChannelManager.getUserChannel(message.getSenderId());
            if (senderChannel != null) {
                ChatMessage notice = buildNoticeMessage("用户 " + receiverId + " 已离线");
                senderChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(notice)));
            }
            return;
        }

        // 转发单聊消息
        receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }

    // 处理群聊
    private void handleGroupChat(ChatMessage message) {
        message.setReceiverId("all");
        ChannelManager.broadcast(message);
    }

    // 工具方法：构建系统通知消息
    private ChatMessage buildNoticeMessage(String content) {
        ChatMessage message = new ChatMessage();
        message.setType(4); // 通知类型
        message.setSenderId("system");
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }

    // 工具方法：通过 Channel 获取 userId
    private String getUserIdByChannel(Channel channel) {
        for (Map.Entry<String, Channel> entry : ChannelManager.USER_CHANNEL_MAP.entrySet()) {
            if (entry.getValue() == channel) {
                return entry.getKey();
            }
        }
        return null;
    }
}
