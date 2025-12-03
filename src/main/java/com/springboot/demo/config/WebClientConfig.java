package com.springboot.demo.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * {@code @description}
 *
 * @author fangzhijian
 * @since 2025-11-24 17:28
 */
@Configuration
public class WebClientConfig {
    /**
     * 配置连接池参数
     */
    @Bean
    public WebClient webClientWithPool() {
        // 1. 定义连接池（ConnectionProvider）
        ConnectionProvider connectionProvider = ConnectionProvider.builder("webclient-pool")
                .maxConnections(200) // 最大连接数：根据服务性能调整（如 100-500）
                .pendingAcquireTimeout(Duration.ofMillis(3000)) // 获取连接的等待超时时间（3秒）
                .pendingAcquireMaxCount(1000) // 等待队列最大长度（默认 500，超出则抛异常）
                .maxIdleTime(Duration.ofSeconds(60)) // 连接最大空闲时间（60秒，空闲超期释放）
                .maxLifeTime(Duration.ofSeconds(120)) // 连接最大存活时间（120秒，强制更换连接）
                .evictInBackground(Duration.ofSeconds(30)) // 后台定时清理空闲连接（30秒执行一次）
                .build();

        // 2. 配置 HttpClient（底层基于 Reactor Netty）
        HttpClient httpClient = HttpClient.create(connectionProvider)
                // 连接超时（5秒）
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 读超时（8秒）：从连接读取数据的超时
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(8, TimeUnit.SECONDS)))
                // 启用 TCP 保持连接（避免连接被防火墙断开）
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 禁用 Nagle 算法（减少延迟，适合小数据包高频请求）
                .option(ChannelOption.TCP_NODELAY, true);

        // 3. 构建 WebClient，绑定 HttpClient 连接池
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 默认请求头
                .build();
    }
}
