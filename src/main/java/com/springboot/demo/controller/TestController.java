package com.springboot.demo.controller;

import com.springboot.demo.model.json.XBaseAiJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@code @description}
 * 测试接口类
 *
 * @author fangzhijian
 * @since 2025/10/11 13:34
 */
@Slf4j
@RestController
public class TestController {

    /**
     * 头米ai对话
     *
     * @return ai对话内容
     */
    @GetMapping(value = "/tmAI")
    public XBaseAiJson tmAI() {
        String uri = "/chat/api/chat_message/0199dce2-f617-7c12-a875-6309facd4457";
        String token = "application-af30aa0d9b80aa893ef348ca6c2c87dd";
        WebClient webClient = WebClient.builder().baseUrl("http://120.26.23.172:18080")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + token).build();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("message", "怎么进件开户");
        map.add("stream", "false");
        map.add("re_chat", "true");
        return webClient.post().uri(uri).body(BodyInserters.fromFormData(map))
                .retrieve().bodyToMono(XBaseAiJson.class).block();
    }

    @GetMapping("test")
    public void test() {

    }
}
