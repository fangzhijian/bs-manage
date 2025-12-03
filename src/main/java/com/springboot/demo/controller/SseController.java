package com.springboot.demo.controller;

import com.springboot.demo.model.json.ResponseJson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code @description}
 * sse测试接口
 *
 * @author fangzhijian
 * @since 2025-12-03 16:15
 */
@Slf4j
@RestController
@AllArgsConstructor
public class SseController {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    // 用于保存所有连接的 SseEmitter，可以根据用户ID等关键字进行存储
    private static final Map<String, SseEmitter> EMITTER_MAP = new ConcurrentHashMap<>();

    // 存储在线客户端连接：key=客户端ID（如用户ID），value=消息发送者（Sink）
    // Sink 用于外部触发消息推送（如业务逻辑主动推送）
    private static final Map<String, FluxSink<ServerSentEvent<String>>> clientSinks = new ConcurrentHashMap<>();

    /**
     * SseEmitter建立连接
     *
     * @param clientId 客户端唯一标识
     * @return sse长连接
     */
    @GetMapping(value = "sseConnect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnect(String clientId) {

        // 设置超时时间，0表示永不超时。可以根据需要设置，例如 30_000L (30秒)
        SseEmitter emitter = new SseEmitter(5000L);
        // 注册回调函数，当连接完成或出错时，从Map中移除这个Emitter
        emitter.onCompletion(() -> EMITTER_MAP.remove(clientId));
        emitter.onError((e) -> EMITTER_MAP.remove(clientId));
        emitter.onTimeout(() -> EMITTER_MAP.remove(clientId));
        // 将新的 emitter 存入 Map
        EMITTER_MAP.put(clientId, emitter);

        // 可选：发送一个初始连接成功的事件
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT") // 事件名称，可选
                    .data("连接成功 for: " + clientId) // 事件数据
                    .reconnectTime(3000)); // 重连时间，可选
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return emitter;

    }

    /**
     * SseEmitter发送消息
     *
     * @param clientId 客户端唯一标识
     * @param message  消息
     * @return 消息
     */
    @GetMapping("sseSend")
    public String sseSend(String clientId, String message) {
        SseEmitter emitter = EMITTER_MAP.get(clientId);
        if (emitter != null) {
            try {
                // 构建并发送事件
                emitter.send(SseEmitter.event()
                        .name("MESSAGE") // 事件类型
                        .data(message)   // 事件数据
                        .id("msg-id-" + System.currentTimeMillis())); // ID
            } catch (IOException e) {
                // 发送失败，移除 emitter
                EMITTER_MAP.remove(clientId);
                return "发送失败，客户端可能已断开";
            }
            return "发送成功 to: " + clientId;
        }
        return "客户端不存在";
    }

    /**
     * Flux<ServerSentEvent>建立连接
     *
     * @param clientId 客户端唯一标识
     * @return sse长连接
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> test5(String clientId) {
        // 1. 创建 Flux，并通过 Sink 控制消息发射（EmitterProcessor 支持多线程发射）
        Flux<ServerSentEvent<String>> sseFlux = Flux.create(sink -> {
            // 存储 Sink，用于后续主动推送消息
            clientSinks.put(clientId, sink);
            System.out.println("客户端 " + clientId + " 连接成功，当前在线数：" + clientSinks.size());
            // 2. 连接断开时触发（客户端关闭、超时、异常），释放资源
            sink.onDispose(() -> {
                clientSinks.remove(clientId);
                System.out.println("客户端 " + clientId + " 断开连接，当前在线数：" + clientSinks.size());
            });
            // 3. 连接建立时，发送初始化消息
            ServerSentEvent<String> initEvent = ServerSentEvent.<String>builder()
                    .event("INIT")
                    .data("SSE 连接成功！")
                    .build();
            sink.next(initEvent);
        }, FluxSink.OverflowStrategy.LATEST); // 背压策略：仅保留最新消息

        // 4. 定时发送心跳消息（每 5 秒一次，避免连接超时）
        Flux<ServerSentEvent<String>> heartbeatFlux = Flux.interval(Duration.ofSeconds(5))
                .map(seq -> ServerSentEvent.<String>builder()
                        .event("HEARTBEAT")
                        .data("ping") // 心跳消息内容
                        .build());
        // 5. 合并业务消息流和心跳流（顺序发射）
        return Flux.merge(sseFlux, heartbeatFlux).timeout(Duration.ofMillis(12000))
                .subscribeOn(Schedulers.boundedElastic()) // 订阅线程池（避免阻塞 Netty 线程）
                .doOnError(error -> System.err.println("连接异常：" + error.getMessage()));
    }

    /**
     * Flux<ServerSentEvent>发送消息
     *
     * @param clientId 客户端唯一标识
     * @param message  消息
     * @return 消息
     */
    @GetMapping("/push")
    public Mono<String> pushToClient(@RequestParam String clientId, @RequestParam String message) {
        FluxSink<ServerSentEvent<String>> sink = clientSinks.get(clientId);
        if (sink == null || sink.isCancelled()) {
            return Mono.just("客户端 " + clientId + " 未连接或已断开");
        }

        // 构建 SSE 消息
        ServerSentEvent<String> sseEvent = ServerSentEvent.<String>builder()
                .event("MESSAGE") // 自定义业务事件名
                .data(message)
                .build();

        // 发射消息（非阻塞）
        sink.next(sseEvent);
        return Mono.just("消息推送成功！");
    }

    /**
     * nacos接受请求
     *
     * @return 数据
     */
    @GetMapping("receive/nacos")
    public ResponseJson test1() {
        for (Map.Entry<String, SseEmitter> entry : EMITTER_MAP.entrySet()) {
            System.out.println("客户端id" + entry.getKey());
        }
        System.out.println("test1方法被调用");
        System.out.println("clientSinks当前在线数：" + clientSinks.size());
        return new ResponseJson().setCode(500).setMsg("nacos调用成功");
    }

    /**
     * 测试nacos
     */
    @GetMapping("test/nacos")
    public void test4() {
        List<ServiceInstance> instances = discoveryClient.getInstances("bs-manage");
        for (ServiceInstance instance : instances) {
            String url = instance.getUri() + "/receive/nacos";
            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            System.out.println(forEntity.getBody());
        }

    }
}
