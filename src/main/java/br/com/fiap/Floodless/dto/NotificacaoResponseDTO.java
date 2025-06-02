package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Notificacao;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import br.com.fiap.Floodless.model.enums.TipoNotificacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO com informações completas de uma notificação")
public record NotificacaoResponseDTO(
    @Schema(description = "ID da notificação", example = "1")
    Long id,

    @Schema(description = "Título da notificação", example = "Alerta de Enchente")
    String titulo,

    @Schema(description = "Conteúdo da mensagem", example = "Risco de enchente na região. Procure abrigo.")
    String mensagem,

    @Schema(description = "Tipo da notificação", example = "ALERTA_RISCO")
    TipoNotificacao tipo,

    @Schema(description = "ID da região relacionada", example = "1")
    Long regiaoId,

    @Schema(description = "Nome da região", example = "Vila Mariana")
    String regiaoNome,

    @Schema(description = "Bairro da região", example = "Vila Mariana")
    String regiaoBairro,

    @Schema(description = "Nível de risco atual da região", example = "ALTO")
    NivelRisco regiaoNivelRisco,

    @Schema(description = "ID do usuário destinatário", example = "1")
    Long usuarioId,

    @Schema(description = "Nome do usuário", example = "João Silva")
    String usuarioNome,

    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    String usuarioEmail,

    @Schema(description = "Indica se a notificação é urgente", example = "true")
    Boolean urgente,

    @Schema(description = "Indica se a notificação já foi lida", example = "false")
    Boolean lida,

    @Schema(description = "Data e hora de criação", example = "2024-03-20T10:30:00")
    LocalDateTime dataCriacao
) {
    public static NotificacaoResponseDTO fromEntity(Notificacao notificacao) {
        return new NotificacaoResponseDTO(
            notificacao.getId(),
            notificacao.getTitulo(),
            notificacao.getMensagem(),
            notificacao.getTipo(),
            notificacao.getRegiao().getId(),
            notificacao.getRegiao().getNome(),
            notificacao.getRegiao().getBairro(),
            notificacao.getRegiao().getNivelRisco(),
            notificacao.getUsuario() != null ? notificacao.getUsuario().getId() : null,
            notificacao.getUsuario() != null ? notificacao.getUsuario().getNome() : null,
            notificacao.getUsuario() != null ? notificacao.getUsuario().getEmail() : null,
            notificacao.getUrgente(),
            notificacao.getLida(),
            notificacao.getDataCriacao()
        );
    }
} 