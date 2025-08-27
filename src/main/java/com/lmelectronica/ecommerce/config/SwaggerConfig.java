package com.lmelectronica.ecommerce.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Ecommerce API")
                                .version("1.0")
                                .description("REST API for managing users, products, orders, and favorites in an online store.")
                                .contact(new io.swagger.v3.oas.models.info.Contact()
                                        .name("Lucas Bonggio")
                                        .email("lucas.abonggio@gmail.com")
                                        .url("https://github.com/lucasBonggio"))
                                .license(new io.swagger.v3.oas.models.info.License()
                                        .name("MIT License")
                                        .url("https://opensource.org/licenses/MIT")))
                        .servers(List.of(
                                new io.swagger.v3.oas.models.servers.Server()
                                        .url("http://localhost:8080")
                                        .description("Development server")))
                        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                        .components(new Components()
                                .addSecuritySchemes("Bearer Authentication",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
        }
}