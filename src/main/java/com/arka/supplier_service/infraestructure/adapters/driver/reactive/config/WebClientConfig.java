package com.arka.supplier_service.infraestructure.adapters.driver.reactive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Bean
    public WebClient catalogWebClient(WebClient.Builder builder, @Value("${catalog.url}") String catalogUrl) {
        return builder
                .baseUrl(catalogUrl)
                .build();
    }
}