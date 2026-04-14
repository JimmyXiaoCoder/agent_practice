package com.ai_gen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.ai_gen.mapper")
public class AiGenBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiGenBackendApplication.class, args);
    }

}
