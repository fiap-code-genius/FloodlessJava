package br.com.fiap.Floodless.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para criação e atualização de abrigos")
public record AbrigoRequestDTO(
    @Schema(description = "Nome do abrigo", example = "Abrigo Municipal Vila Mariana")
    @NotBlank(message = "Nome do abrigo é obrigatório")
    String nome,
    
    @Schema(description = "Descrição do abrigo", example = "Abrigo com capacidade para famílias")
    String descricao,
    
    @Schema(description = "Endereço completo do abrigo", example = "Rua Vergueiro, 1000")
    @NotBlank(message = "Endereço é obrigatório")
    String endereco,
    
    @Schema(description = "Capacidade máxima de pessoas", example = "100")
    @NotNull(message = "Capacidade máxima é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser maior que zero")
    Integer capacidadeMaxima,
    
    @Schema(description = "Ocupação atual do abrigo", example = "0")
    @NotNull(message = "Ocupação atual é obrigatória")
    @Min(value = 0, message = "Ocupação não pode ser negativa")
    Integer ocupacaoAtual,
    
    @Schema(description = "Telefone para contato", example = "(11) 99999-9999")
    String telefoneContato,
    
    @Schema(description = "Email para contato", example = "abrigo@email.com")
    @Email(message = "Email inválido")
    String emailContato,
    
    @Schema(description = "Status de atividade do abrigo", example = "true", defaultValue = "true")
    Boolean ativo,

    @Schema(description = "Status de disponibilidade do abrigo", example = "true", defaultValue = "true")
    Boolean disponivel,
    
    @Schema(description = "ID da região onde o abrigo está localizado", example = "1")
    @NotNull(message = "ID da região é obrigatório")
    Long regiaoId
) {} 