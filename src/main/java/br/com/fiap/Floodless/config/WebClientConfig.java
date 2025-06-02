package br.com.fiap.Floodless.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient nominatimWebClient() {
        return WebClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .build();
    }

    @Bean
    public WebClient openMeteoWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }
} 