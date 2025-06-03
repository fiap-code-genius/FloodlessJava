package br.com.fiap.Floodless.service;

import br.com.fiap.Floodless.model.Coordenadas;
import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClimaService {
    private static final Logger logger = LoggerFactory.getLogger(ClimaService.class);
    private static final int MAX_FALHAS_CONSECUTIVAS = 3;
    private static final Duration CIRCUIT_BREAKER_RESET = Duration.ofMinutes(15);
    private int falhasConsecutivas = 0;
    private LocalDateTime ultimaFalha;

    private final Map<String, Coordenadas> coordenadasCache = new ConcurrentHashMap<>();
    private final RateLimiter rateLimiter = RateLimiter.create(0.5); // Uma requisição a cada 2 segundos

    // Constantes para classificação de risco baseadas em critérios técnicos
    // Até 25mm/h - Chuva fraca a moderada
    private static final double LIMIAR_MODERADO = 45.0;  // 25-45mm/h - Chuva forte
    private static final double LIMIAR_ALTO = 65.0;      // 45-65mm/h - Chuva muito forte
    private static final double LIMIAR_CRITICO = 65.0;   // >65mm/h - Chuva extrema

    @Autowired
    private WebClient nominatimWebClient;

    @Autowired
    private WebClient openMeteoWebClient;

    private synchronized void registrarFalha() {
        falhasConsecutivas++;
        ultimaFalha = LocalDateTime.now();
        if (falhasConsecutivas >= MAX_FALHAS_CONSECUTIVAS) {
            logger.error("ALERTA: {} falhas consecutivas detectadas. Circuit breaker ativado.", MAX_FALHAS_CONSECUTIVAS);
        }
    }

    private synchronized void resetarFalhas() {
        falhasConsecutivas = 0;
        ultimaFalha = null;
    }

    private synchronized boolean deveUsarFallback() {
        if (falhasConsecutivas >= MAX_FALHAS_CONSECUTIVAS) {
            if (ultimaFalha != null && ultimaFalha.plus(CIRCUIT_BREAKER_RESET).isBefore(LocalDateTime.now())) {
                resetarFalhas();
                return false;
            }
            return true;
        }
        return false;
    }

    private Optional<Coordenadas> getCoordenadas(String endereco) {
        Coordenadas coords = coordenadasCache.get(endereco);
        if (coords != null && coords.isValido()) {
            logger.info("Usando coordenadas em cache para: {}", endereco);
            return Optional.of(coords);
        }
        return Optional.empty();
    }

    public void atualizarDadosClimaticos(Regiao regiao) {
        if (deveUsarFallback()) {
            logger.warn("Circuit breaker ativo. Usando valores padrão para {}", regiao.getNome());
            definirDadosPadrao(regiao);
            return;
        }

        if (!rateLimiter.tryAcquire()) {
            logger.warn("Rate limit atingido. Usando dados em cache ou padrão para {}", regiao.getNome());
            definirDadosPadrao(regiao);
            return;
        }

        try {
            String endereco = String.format("%s, %s, %s", regiao.getBairro(), regiao.getCidade(), regiao.getEstado());
            
            // Tenta usar coordenadas em cache
            Optional<Coordenadas> coordenadasCached = getCoordenadas(endereco);
            if (coordenadasCached.isPresent()) {
                Coordenadas coords = coordenadasCached.get();
                buscarDadosMeteorologicos(regiao, coords.lat(), coords.lon());
                return;
            }

            logger.info("Buscando coordenadas para endereço: {}", endereco);

            nominatimWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", endereco)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
                            .maxBackoff(Duration.ofSeconds(30))
                            .filter(throwable -> shouldRetry(throwable)))
                    .subscribe(locationData -> {
                        if (locationData != null && locationData.isArray() && locationData.size() > 0) {
                            JsonNode location = locationData.get(0);
                            double lat = location.get("lat").asDouble();
                            double lon = location.get("lon").asDouble();
                            
                            // Salva no cache
                            coordenadasCache.put(endereco, Coordenadas.of(lat, lon));
                            
                            logger.info("Coordenadas encontradas: lat={}, lon={}", lat, lon);
                            buscarDadosMeteorologicos(regiao, lat, lon);
                            resetarFalhas();
                        } else {
                            logger.warn("Não foi possível encontrar coordenadas para o endereço: {}", endereco);
                            registrarFalha();
                            definirDadosPadrao(regiao);
                        }
                    }, error -> {
                        logger.error("Erro ao processar coordenadas: {} - {}", error.getClass().getSimpleName(), error.getMessage());
                        registrarFalha();
                        definirDadosPadrao(regiao);
                    });
        } catch (Exception e) {
            logger.error("Erro ao atualizar dados climáticos: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            registrarFalha();
            definirDadosPadrao(regiao);
        }
    }

    private void buscarDadosMeteorologicos(Regiao regiao, double lat, double lon) {
        String openMeteoUrl = String.format("/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m,precipitation,rain,showers,weathercode&hourly=precipitation_probability,precipitation&forecast_days=1", lat, lon);
        logger.info("URL Open-Meteo: {}", openMeteoUrl);

        openMeteoWebClient.get()
                .uri(openMeteoUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
                        .maxBackoff(Duration.ofSeconds(30))
                        .filter(throwable -> shouldRetry(throwable)))
                .subscribe(weatherData -> {
                    if (weatherData != null) {
                        processarDadosMeteorologicos(regiao, weatherData);
                    } else {
                        logger.warn("Não foi possível obter dados meteorológicos");
                        definirDadosPadrao(regiao);
                    }
                }, error -> {
                    logger.error("Erro ao processar dados meteorológicos: {}", error.getMessage());
                    definirDadosPadrao(regiao);
                });
    }

    private void definirDadosPadrao(Regiao regiao) {
        // Se tiver dados anteriores, mantém os últimos valores conhecidos
        if (regiao.getNivelChuva() != null && regiao.getTemperatura() != null) {
            logger.info("Mantendo últimos dados conhecidos para a região: {}", regiao.getNome());
            return;
        }
        
        // Se não tiver dados anteriores, usa valores padrão
        logger.info("Definindo dados padrão para a região: {}", regiao.getNome());
        regiao.setTemperatura(25.0);
        regiao.setNivelChuva(0.0);
        regiao.setNivelRisco(NivelRisco.BAIXO);
        regiao.setAreaRisco(false);
        regiao.setUltimaAtualizacao(LocalDateTime.now());
    }

    private void processarDadosMeteorologicos(Regiao regiao, JsonNode weatherData) {
        try {
            JsonNode current = weatherData.get("current");
            JsonNode hourly = weatherData.get("hourly");

            double temperatura = current.get("temperature_2m").asDouble();
            double chuvaAtual = current.get("precipitation").asDouble();
            double rain = current.get("rain").asDouble();
            double showers = current.get("showers").asDouble();
            int weatherCode = current.get("weathercode").asInt();

            // Calcular média de probabilidade de precipitação para as próximas 24h
            double probMediaChuva = 0;
            double precipitacaoTotal = 0;
            JsonNode probArray = hourly.get("precipitation_probability");
            JsonNode precipArray = hourly.get("precipitation");

            for (int i = 0; i < 24 && i < probArray.size(); i++) {
                probMediaChuva += probArray.get(i).asDouble();
                precipitacaoTotal += precipArray.get(i).asDouble();
            }
            probMediaChuva /= 24;

            double nivelChuvaCalculado = calcularNivelChuva(
                    chuvaAtual,
                    rain,
                    showers,
                    weatherCode,
                    probMediaChuva,
                    precipitacaoTotal
            );

            regiao.setTemperatura(temperatura);
            regiao.setNivelChuva(nivelChuvaCalculado);
            regiao.setUltimaAtualizacao(LocalDateTime.now());

            atualizarNivelRisco(regiao);
            
            logger.info("Dados meteorológicos atualizados com sucesso para {}: temp={}, nivelChuva={}, risco={}",
                    regiao.getNome(), temperatura, nivelChuvaCalculado, regiao.getNivelRisco());
        } catch (Exception e) {
            logger.error("Erro ao processar dados meteorológicos: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            definirDadosPadrao(regiao);
        }
    }

    private double calcularNivelChuva(
            double chuvaAtual,
            double rain,
            double showers,
            int weatherCode,
            double probMediaChuva,
            double precipitacaoTotal
    ) {
        // Nível base é a soma da chuva atual, chuva e pancadas de chuva
        double nivelBase = chuvaAtual + rain + showers;

        // Adicionar fator de previsão (precipitação total prevista para 24h)
        nivelBase += (precipitacaoTotal * 0.5);

        // Aumentar nível baseado na probabilidade média de chuva
        nivelBase *= (1 + (probMediaChuva / 100.0));

        // Ajustar baseado no código do tempo
        double multiplicadorCodigo = 1.0;
        if (weatherCode >= 95) { // Tempestade forte
            multiplicadorCodigo = 2.0;
        } else if (weatherCode >= 80) { // Chuva forte
            multiplicadorCodigo = 1.5;
        } else if (weatherCode >= 60) { // Chuva moderada
            multiplicadorCodigo = 1.2;
        }
        nivelBase *= multiplicadorCodigo;

        return nivelBase;
    }

    private void atualizarNivelRisco(Regiao regiao) {
        if (regiao.getNivelChuva() != null) {
            double nivelChuva = regiao.getNivelChuva();

            if (nivelChuva > LIMIAR_CRITICO) {
                regiao.setNivelRisco(NivelRisco.CRITICO);
                regiao.setAreaRisco(true);
            } else if (nivelChuva > LIMIAR_ALTO) {
                regiao.setNivelRisco(NivelRisco.ALTO);
                regiao.setAreaRisco(true);
            } else if (nivelChuva > LIMIAR_MODERADO) {
                regiao.setNivelRisco(NivelRisco.MODERADO);
                regiao.setAreaRisco(false);
            } else {
                regiao.setNivelRisco(NivelRisco.BAIXO);
                regiao.setAreaRisco(false);
            }
        }
    }

    private boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
            org.springframework.web.reactive.function.client.WebClientResponseException ex = 
                (org.springframework.web.reactive.function.client.WebClientResponseException) throwable;
            int statusCode = ex.getStatusCode().value();
            return statusCode == 429 || statusCode >= 500;
        }
        return throwable instanceof java.net.SocketTimeoutException ||
               throwable instanceof java.net.ConnectException ||
               throwable instanceof io.netty.channel.ConnectTimeoutException ||
               throwable instanceof java.io.IOException;
    }
} 