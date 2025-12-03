package com.springboot.demo.netty;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.springboot.demo.netty.message.MessageInfo;
import com.springboot.demo.netty.message.MessageParam;
import com.springboot.demo.netty.message.MessageVo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * {@code @description}
 * 聊天服务端业务处理器
 * <p>
 * 将Handler注入到spring容器中需添加@ChannelHandler.Sharable注解
 *
 * @author fangzhijian
 * @since 2025-11-26 22:43
 */
@Slf4j
@Service
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private SensitiveWordBs sensitiveWordBs;

    // 接收客户端消息（SimpleChannelInboundHandler 自动释放消息资源）
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        log.info("收到客户端消息：{}", frame.text());
        MessageParam messageParam;
        try {
            messageParam = JSON.parseObject(frame.text(), MessageParam.class);
            if (messageParam == null || messageParam.getType() == null || messageParam.getLiveId() == null) {
                return;
            }
        } catch (JSONException jsonException) {
            log.error("数据格式不是JSON");
            return;
        }
        Channel channel = ctx.channel();
        switch (messageParam.getType()) {
            case 1 -> login(channel, messageParam);
            case 2 -> message(messageParam);
        }
    }

    //客户端建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("客户端已建立连接，ip：{} 端口：{}", socketAddress.getAddress().getHostAddress(), socketAddress.getPort());
    }

    //保存进缓存
    public void login(Channel channel, MessageParam messageParam) {
        ChannelManager.addChannel(getClientId(channel), messageParam.getLiveId(), channel);
        //在线人数统计+1
        //发送初始化数据
        MessageVo messageVo = new MessageVo();
        messageVo.setType(1);
        ChannelManager.sendMessage(channel, messageVo);
    }

    // 客户端断开连接，移除缓存
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Long liveId = ChannelManager.removeChannel(getClientId(ctx.channel()));
        //在线人数-1
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

    //使用ip加端口号组成唯一客户端id
    private String getClientId(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
    }

    //将消息存入redis，并定时广播推送
    public void message(MessageParam messageParam) {
        MessageInfo messageInfo = messageParam.getMessageInfo();
        //过滤敏感词替换为*
        messageInfo.setMessage(sensitiveWordBs.replace(messageInfo.getMessage()));
        //将消息存入redis并定时广播推送
    }

}
