package com.fredmaina.event_management.GlobalServices;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("events by Fred app")
                        .version("0.0.1")
                        .description("Events by Fred app is a dynamic platform that empowers users to create and manage events, purchase tickets seamlessly, and connect with a vibrant community of event enthusiasts."))
                .servers(List.of(
                        new Server()
                                .url("https://api.events.v0.fredmaina.com")
                                .description("Production server")
                ));
    }
}
