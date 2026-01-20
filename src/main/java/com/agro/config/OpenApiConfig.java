package com.agro.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AgroConnect API",
                version = "1.0",
                description = "AgroConnect Backend APIs"
        )
)
public class OpenApiConfig {
}
