package br.com.fiap.Floodless.model;

import java.time.LocalDateTime;

public record Coordenadas(
    double lat,
    double lon,
    LocalDateTime ultimaAtualizacao
) {
    public boolean isValido() {
        return ultimaAtualizacao.plusDays(7).isAfter(LocalDateTime.now());
    }

    public static Coordenadas of(double lat, double lon) {
        return new Coordenadas(lat, lon, LocalDateTime.now());
    }
} 