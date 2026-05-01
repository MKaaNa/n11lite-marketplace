package com.n11.marketplace.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI n11LiteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("N11Lite Marketplace API")
                        .description("Spring Boot + React marketplace final project API")
                        .version("v1")
                        .contact(new Contact()
                                .name("N11Lite Project")
                                .email("noreply@n11lite.com")));
    }
}
