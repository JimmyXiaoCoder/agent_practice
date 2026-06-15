package com.ai_gen.controller;

import cn.hutool.json.JSONUtil;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sse() {
        Flux<String> just = Flux.just("a", "b", "d", "c");
        return just.map(chunk -> {
            Map<String,String> map = new HashMap<>();
            map.put("d",chunk);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String jsonStr = JSONUtil.toJsonStr(map);
            return ServerSentEvent.<String>builder().data(jsonStr).build();
        });


//        return Flux.just(
//                ServerSentEvent.<String>builder()
//                        .event("message")
//                        .id("1")
//                        .data("hello")
//                        .build()
//        );
    }

    @GetMapping("/jtest")
    public String test() {
        return "ok";
    }

}
