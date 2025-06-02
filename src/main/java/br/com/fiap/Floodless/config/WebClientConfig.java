package br.com.fiap.Floodless.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private HttpClient createHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Bean
    public WebClient nominatimWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "Floodless/1.0 (https://floodless.onrender.com)")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();
    }

    @Bean
    public WebClient openMeteoWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .baseUrl("https://api.open-meteo.com")
                .defaultHeader("User-Agent", "Floodless/1.0 (https://floodless.onrender.com)")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();
    }
} 