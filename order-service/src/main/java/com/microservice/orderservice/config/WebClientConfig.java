package com.microservice.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// For calling of other services API
@Configuration
public class WebClientConfig {

    // The following webClient return object is from web-flux module in POM
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
