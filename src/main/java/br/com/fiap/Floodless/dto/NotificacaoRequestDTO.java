package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.enums.TipoNotificacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criação de notificações")
public record NotificacaoRequestDTO(
    @Schema(description = "Título da notificação", example = "Alerta de Enchente")
    @NotBlank(message = "Título é obrigatório")
    String titulo,
    
    @Schema(description = "Conteúdo da mensagem", example = "Risco de enchente na região. Procure abrigo.")
    @NotBlank(message = "Mensagem é obrigatória")
    String mensagem,
    
    @Schema(description = "Tipo da notificação", example = "ALERTA_RISCO")
    @NotNull(message = "Tipo de notificação é obrigatório")
    TipoNotificacao tipo,
    
    @Schema(description = "ID da região relacionada", example = "1")
    @NotNull(message = "ID da região é obrigatório")
    Long regiaoId,
    
    @Schema(description = "ID do usuário destinatário (opcional)", example = "1")
    Long usuarioId,
    
    @Schema(description = "Indica se a notificação é urgente", example = "true", defaultValue = "false")
    Boolean urgente,
    
    @Schema(description = "Indica se a notificação já foi lida", example = "false", defaultValue = "false")
    Boolean lida
) {} 