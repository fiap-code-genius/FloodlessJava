package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "DTO com os dados completos de uma região, incluindo informações climáticas, moradores e abrigos")
public record RegiaoResponseDTO(
    @Schema(description = "ID único da região", example = "1")
    Long id,

    @Schema(description = "Nome da região", example = "Vila Mariana")
    String nome,

    @Schema(description = "Estado onde a região está localizada", example = "SP")
    String estado,

    @Schema(description = "Cidade onde a região está localizada", example = "São Paulo")
    String cidade,

    @Schema(description = "Bairro onde a região está localizada", example = "Vila Mariana")
    String bairro,

    @Schema(description = "CEP da região", example = "04021-001")
    String cep,

    @Schema(description = "Nível de risco atual da região", example = "BAIXO")
    NivelRisco nivelRisco,

    @Schema(description = "Nível de chuva em milímetros", example = "12.5")
    Double nivelChuva,

    @Schema(description = "Temperatura em graus Celsius", example = "25.8")
    Double temperatura,

    @Schema(description = "Indica se a região é considerada área de risco", example = "false")
    Boolean areaRisco,

    @Schema(description = "Data e hora da última atualização dos dados climáticos", example = "2024-03-20T10:30:00")
    LocalDateTime ultimaAtualizacao,

    @Schema(description = "Lista de moradores cadastrados na região")
    List<UsuarioSimplificadoDTO> moradores,

    @Schema(description = "Lista de abrigos disponíveis na região")
    List<AbrigoSimplificadoDTO> abrigos
) {
    public RegiaoResponseDTO(Regiao regiao) {
        this(
            regiao.getId(),
            regiao.getNome(),
            regiao.getEstado(),
            regiao.getCidade(),
            regiao.getBairro(),
            regiao.getCep(),
            regiao.getNivelRisco(),
            regiao.getNivelChuva(),
            regiao.getTemperatura(),
            regiao.getAreaRisco(),
            regiao.getUltimaAtualizacao(),
            regiao.getMoradores() != null 
                ? regiao.getMoradores().stream()
                    .map(UsuarioSimplificadoDTO::new)
                    .collect(Collectors.toList())
                : List.of(),
            regiao.getAbrigos() != null
                ? regiao.getAbrigos().stream()
                    .map(AbrigoSimplificadoDTO::new)
                    .collect(Collectors.toList())
                : List.of()
        );
    }
} 