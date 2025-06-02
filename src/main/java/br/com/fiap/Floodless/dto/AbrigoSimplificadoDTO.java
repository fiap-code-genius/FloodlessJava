package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Abrigo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO com informações básicas de um abrigo")
public record AbrigoSimplificadoDTO(
    @Schema(description = "ID do abrigo", example = "1")
    Long id,

    @Schema(description = "Nome do abrigo", example = "Abrigo Municipal Vila Mariana")
    String nome,

    @Schema(description = "Endereço do abrigo", example = "Rua Vergueiro, 1000")
    String endereco,

    @Schema(description = "Capacidade total do abrigo", example = "100")
    Integer capacidade,

    @Schema(description = "Quantidade atual de pessoas abrigadas", example = "45")
    Integer ocupacaoAtual
) {
    public AbrigoSimplificadoDTO(Abrigo abrigo) {
        this(
            abrigo.getId(),
            abrigo.getNome(),
            abrigo.getEndereco(),
            abrigo.getCapacidadeMaxima(),
            abrigo.getOcupacaoAtual()
        );
    }
} 