package com.soft231.daycarecenterbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("托育中心管理系统 API")
                        .description("托育中心管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("soft231")));
    }
}
