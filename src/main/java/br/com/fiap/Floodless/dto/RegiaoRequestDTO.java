package br.com.fiap.Floodless.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para criação e atualização de regiões")
public record RegiaoRequestDTO(
    @Schema(description = "Nome da região", example = "Vila Mariana")
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    @Schema(description = "Estado onde a região está localizada", example = "SP")
    @NotBlank(message = "Estado é obrigatório")
    @Size(max = 50, message = "Estado deve ter no máximo 50 caracteres")
    @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ser uma sigla de 2 letras maiúsculas")
    String estado,

    @Schema(description = "Cidade onde a região está localizada", example = "São Paulo")
    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    String cidade,

    @Schema(description = "Bairro onde a região está localizada", example = "Vila Mariana")
    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 50, message = "Bairro deve ter no máximo 50 caracteres")
    String bairro,

    @Schema(description = "CEP da região", example = "04021-001")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
    String cep
) {} 