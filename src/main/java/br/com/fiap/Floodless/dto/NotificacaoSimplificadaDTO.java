package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Notificacao;
import br.com.fiap.Floodless.model.enums.TipoNotificacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO com informações resumidas de uma notificação")
public record NotificacaoSimplificadaDTO(
    @Schema(description = "ID da notificação", example = "1")
    Long id,

    @Schema(description = "Título da notificação", example = "Alerta de Enchente")
    String titulo,

    @Schema(description = "Tipo da notificação", example = "ALERTA_RISCO")
    TipoNotificacao tipo,

    @Schema(description = "Indica se a notificação é urgente", example = "true")
    Boolean urgente,

    @Schema(description = "Data e hora de criação", example = "2024-03-20T10:30:00")
    LocalDateTime dataCriacao
) {
    public static NotificacaoSimplificadaDTO fromEntity(Notificacao notificacao) {
        return new NotificacaoSimplificadaDTO(
            notificacao.getId(),
            notificacao.getTitulo(),
            notificacao.getTipo(),
            notificacao.getUrgente(),
            notificacao.getDataCriacao()
        );
    }
} 