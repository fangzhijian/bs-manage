package com.springboot.demo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * {@code @description}
 * Netty 聊天服务启动器
 *
 * @author fangzhijian
 * @since 2025-11-26 22:45
 */
@Component
@Slf4j
public class NettyChatServer implements CommandLineRunner {

    @Resource
    private NettyServerHandler nettyServerHandler;

    // Netty 端口
    @Value("${netty.chat.port:8089}")
    private Integer port;

    // Netty 主从线程组
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    // 启动 Netty 服务
    public void start() {
        bossGroup = new NioEventLoopGroup(1); // 主线程组（监听连接）
        workerGroup = new NioEventLoopGroup(); // 工作线程组（处理业务）

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // NIO 通道类型
                    .option(ChannelOption.SO_BACKLOG, 128) // 连接队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持长连接
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //显示配置服务端pooledByteBuf
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //显示配置客户端pooledByteBuf
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加HTTP服务器编解码器，用于将数据转换成HTTP协议格式进行传输。
                            pipeline.addLast(new HttpServerCodec());
                            //添加HTTP对象聚合处理器，用于将HTTP请求或响应中的多个消息片段聚合成完整的消息。
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            //对写大数据流的支持
                            pipeline.addLast(new ChunkedWriteHandler());
                            //添加WebSocket协议处理器，用于处理WebSocket握手、消息传输等操作。
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws/chat"));
                            //空闲检测（120秒无读写则关闭连接）
                            pipeline.addLast(new IdleStateHandler(120, 120, 0));
                            //添加WebSocket处理器，用于处理客户端与服务器端之间的数据交换，实现自定义的业务逻辑。
                            pipeline.addLast(nettyServerHandler);
                        }
                    }); // 通道初始化器

            // 绑定端口，同步等待启动成功
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("Netty 聊天服务启动成功，端口：{}", port);

            // 阻塞等待服务关闭（监听通道关闭事件）
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Netty 服务启动异常：", e);
        } finally {
            // 优雅关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Netty 服务已关闭");
        }
    }

    // Spring Boot 启动后执行
    @Override
    public void run(String... args) throws Exception {
        new Thread(this::start).start(); // 单独线程启动，避免阻塞 Spring 主线程
    }

    // Spring 销毁时关闭 Netty（优雅停机）
    @PreDestroy
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("Netty 服务优雅关闭");
    }
}
