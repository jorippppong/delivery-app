package com.sparta.foodorder.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Value("${server.url}")
    private String serverUrl;
    
    @Bean
    public OpenAPI customOpenAPI() {
        // 환경별로 서버 URL 동적 설정
        String description = serverUrl.contains("localhost") 
                ? "로컬 개발 서버" 
                : "프로덕션 서버";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Food Order API")
                        .version("v1.0")
                        .description("배달 주문 서비스 API 문서")
                        .contact(new Contact()
                                .name("9조 프로젝트")
                                .email("team9@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(description)
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
