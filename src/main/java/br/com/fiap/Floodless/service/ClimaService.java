package br.com.fiap.Floodless.service;

import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ClimaService {
    private static final Logger logger = LoggerFactory.getLogger(ClimaService.class);

    // Constantes para classificação de risco baseadas em critérios técnicos
    // Até 25mm/h - Chuva fraca a moderada
    private static final double LIMIAR_MODERADO = 45.0;  // 25-45mm/h - Chuva forte
    private static final double LIMIAR_ALTO = 65.0;      // 45-65mm/h - Chuva muito forte
    private static final double LIMIAR_CRITICO = 65.0;   // >65mm/h - Chuva extrema

    @Autowired
    private WebClient nominatimWebClient;

    @Autowired
    private WebClient openMeteoWebClient;

    public void atualizarDadosClimaticos(Regiao regiao) {
        try {
            String endereco = String.format("%s, %s, %s", regiao.getBairro(), regiao.getCidade(), regiao.getEstado());
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
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                    .timeout(Duration.ofSeconds(30))
                    .onErrorResume(e -> {
                        logger.error("Erro ao buscar coordenadas: {}", e.getMessage());
                        return Mono.empty();
                    })
                    .subscribe(locationData -> {
                        if (locationData != null && locationData.isArray() && locationData.size() > 0) {
                            JsonNode location = locationData.get(0);
                            double lat = location.get("lat").asDouble();
                            double lon = location.get("lon").asDouble();
                            logger.info("Coordenadas encontradas: lat={}, lon={}", lat, lon);
                            buscarDadosMeteorologicos(regiao, lat, lon);
                        } else {
                            logger.warn("Não foi possível encontrar coordenadas para o endereço: {}", endereco);
                            definirDadosPadrao(regiao);
                        }
                    }, error -> {
                        logger.error("Erro ao processar coordenadas: {}", error.getMessage());
                        definirDadosPadrao(regiao);
                    });
        } catch (Exception e) {
            logger.error("Erro ao atualizar dados climáticos: ", e);
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
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(e -> {
                    logger.error("Erro ao buscar dados meteorológicos: {}", e.getMessage());
                    return Mono.empty();
                })
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
        logger.info("Definindo dados padrão para a região: {}", regiao.getNome());
        regiao.setTemperatura(25.0);
        regiao.setNivelChuva(0.0);
        regiao.setNivelRisco(NivelRisco.BAIXO);
        regiao.setAreaRisco(false);
    }

    private void processarDadosMeteorologicos(Regiao regiao, JsonNode weatherData) {
        JsonNode current = weatherData.get("current");
        JsonNode hourly = weatherData.get("hourly");

        logger.info("Dados atuais: {}", current);
        logger.info("Dados horários: {}", hourly);

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

        for (int i = 0; i < 24; i++) {
            probMediaChuva += probArray.get(i).asDouble();
            precipitacaoTotal += precipArray.get(i).asDouble();
        }
        probMediaChuva /= 24;

        logger.info("Probabilidade média de chuva: {}, Precipitação total prevista: {}",
                probMediaChuva, precipitacaoTotal);

        // Calcular nível de chuva considerando todos os fatores
        double nivelChuvaCalculado = calcularNivelChuva(
                chuvaAtual,
                rain,
                showers,
                weatherCode,
                probMediaChuva,
                precipitacaoTotal
        );

        logger.info("Nível de chuva calculado: {}", nivelChuvaCalculado);

        regiao.setTemperatura(temperatura);
        regiao.setNivelChuva(nivelChuvaCalculado);
        regiao.setUltimaAtualizacao(LocalDateTime.now());

        // Atualizar nível de risco baseado nos dados climáticos
        atualizarNivelRisco(regiao);
    }

    private double calcularNivelChuva(
            double chuvaAtual,
            double rain,
            double showers,
            int weatherCode,
            double probMediaChuva,
            double precipitacaoTotal
    ) {
        logger.info("Iniciando cálculo de nível de chuva com os seguintes valores:");
        logger.info("Chuva atual: {}", chuvaAtual);
        logger.info("Rain: {}", rain);
        logger.info("Showers: {}", showers);
        logger.info("Weather Code: {}", weatherCode);
        logger.info("Probabilidade média de chuva: {}", probMediaChuva);
        logger.info("Precipitação total prevista: {}", precipitacaoTotal);

        // Nível base é a soma da chuva atual, chuva e pancadas de chuva
        double nivelBase = chuvaAtual + rain + showers;
        logger.info("Nível base (soma de chuva atual, rain e showers): {}", nivelBase);

        // Adicionar fator de previsão (precipitação total prevista para 24h)
        nivelBase += (precipitacaoTotal * 0.5);
        logger.info("Nível após adicionar fator de previsão: {}", nivelBase);

        // Aumentar nível baseado na probabilidade média de chuva
        nivelBase *= (1 + (probMediaChuva / 100.0));
        logger.info("Nível após ajuste de probabilidade: {}", nivelBase);

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
        logger.info("Nível final após ajuste do código do tempo (multiplicador: {}): {}", multiplicadorCodigo, nivelBase);

        return nivelBase;
    }

    private void atualizarNivelRisco(Regiao regiao) {
        if (regiao.getNivelChuva() != null) {
            double nivelChuva = regiao.getNivelChuva();

            if (nivelChuva > 100.0) {
                regiao.setNivelRisco(NivelRisco.CRITICO);
                regiao.setAreaRisco(true);
            } else if (nivelChuva > 60.0) {
                regiao.setNivelRisco(NivelRisco.ALTO);
                regiao.setAreaRisco(true);
            } else if (nivelChuva > 30.0) {
                regiao.setNivelRisco(NivelRisco.MODERADO);
                regiao.setAreaRisco(false);
            } else {
                regiao.setNivelRisco(NivelRisco.BAIXO);
                regiao.setAreaRisco(false);
            }
        }
    }

    private NivelRisco calcularNivelRisco(
            double chuvaAtual,
            double rain,
            double showers,
            int weatherCode,
            double probMediaChuva,
            double precipitacaoTotal
    ) {
        logger.info("Iniciando cálculo de nível de risco com os seguintes valores:");
        logger.info("Chuva atual: {}", chuvaAtual);
        logger.info("Rain: {}", rain);
        logger.info("Showers: {}", showers);
        logger.info("Weather Code: {}", weatherCode);
        logger.info("Probabilidade média de chuva: {}", probMediaChuva);
        logger.info("Precipitação total prevista: {}", precipitacaoTotal);

        // Nível base é a soma da chuva atual, chuva e pancadas de chuva
        double nivelBase = chuvaAtual + rain + showers;
        logger.info("Nível base (soma de chuva atual, rain e showers): {}", nivelBase);

        // Multiplicador baseado no código do tempo
        double multiplicadorCodigo = 1.0;

        // Códigos WMO para condições severas (trovoadas, tempestades etc)
        if (weatherCode >= 95 && weatherCode <= 99) {
            multiplicadorCodigo = 2.0;
        } else if (weatherCode >= 80 && weatherCode <= 94) {
            multiplicadorCodigo = 1.5;
        } else if (weatherCode >= 60 && weatherCode <= 79) {
            multiplicadorCodigo = 1.2;
        }

        logger.info("Multiplicador baseado no código do tempo: {}", multiplicadorCodigo);

        // Cálculo do nível de chuva considerando todos os fatores
        double nivelChuva = nivelBase * multiplicadorCodigo * (probMediaChuva / 100.0);
        logger.info("Nível de chuva calculado: {}", nivelChuva);

        // Classificação do risco baseada nos limiares técnicos
        if (nivelChuva >= LIMIAR_CRITICO || precipitacaoTotal >= 100.0) {
            return NivelRisco.CRITICO;
        } else if (nivelChuva >= LIMIAR_ALTO || precipitacaoTotal >= 80.0) {
            return NivelRisco.ALTO;
        } else if (nivelChuva >= LIMIAR_MODERADO || precipitacaoTotal >= 50.0) {
            return NivelRisco.MODERADO;
        } else {
            return NivelRisco.BAIXO;
        }
    }
} 