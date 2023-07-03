package com.microservice.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// For calling of other services API
@Configuration
public class WebClientConfig {

    // The following webClient return object is from web-flux module in POM
    @Bean
    // This will enable the client to call the instance one after another
    @LoadBalanced // add load balancing abilities to the web client builder
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
