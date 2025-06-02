package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Abrigo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO com informações completas de um abrigo")
public record AbrigoResponseDTO(
    @Schema(description = "ID do abrigo", example = "1")
    Long id,

    @Schema(description = "Nome do abrigo", example = "Abrigo Municipal Vila Mariana")
    String nome,

    @Schema(description = "Descrição do abrigo", example = "Abrigo com capacidade para famílias")
    String descricao,

    @Schema(description = "Endereço completo do abrigo", example = "Rua Vergueiro, 1000")
    String endereco,

    @Schema(description = "Capacidade máxima de pessoas", example = "100")
    Integer capacidadeMaxima,

    @Schema(description = "Ocupação atual do abrigo", example = "45")
    Integer ocupacaoAtual,

    @Schema(description = "Telefone para contato", example = "(11) 99999-9999")
    String telefoneContato,

    @Schema(description = "Email para contato", example = "abrigo@email.com")
    String emailContato,

    @Schema(description = "Status de atividade do abrigo", example = "true")
    Boolean ativo,

    @Schema(description = "Status de disponibilidade do abrigo", example = "true")
    Boolean disponivel,

    @Schema(description = "Quantidade de vagas disponíveis", example = "55")
    Integer vagasDisponiveis,

    @Schema(description = "Taxa de ocupação em porcentagem", example = "45.0")
    Double taxaOcupacao,

    @Schema(description = "ID da região onde o abrigo está localizado", example = "1")
    Long regiaoId,

    @Schema(description = "Nome da região onde o abrigo está localizado", example = "Vila Mariana")
    String regiaoNome,

    @Schema(description = "Nível de risco da região", example = "BAIXO")
    String regiaoNivelRisco
) {
    public static AbrigoResponseDTO fromEntity(Abrigo abrigo) {
        int vagasDisponiveis = abrigo.getCapacidadeMaxima() - abrigo.getOcupacaoAtual();
        double taxaOcupacao = (abrigo.getOcupacaoAtual().doubleValue() / abrigo.getCapacidadeMaxima().doubleValue()) * 100;
        
        return new AbrigoResponseDTO(
            abrigo.getId(),
            abrigo.getNome(),
            abrigo.getDescricao(),
            abrigo.getEndereco(),
            abrigo.getCapacidadeMaxima(),
            abrigo.getOcupacaoAtual(),
            abrigo.getTelefoneContato(),
            abrigo.getEmailContato(),
            abrigo.getAtivo(),
            abrigo.getDisponivel(),
            vagasDisponiveis,
            taxaOcupacao,
            abrigo.getRegiao() != null ? abrigo.getRegiao().getId() : null,
            abrigo.getRegiao() != null ? abrigo.getRegiao().getNome() : null,
            abrigo.getRegiao() != null ? abrigo.getRegiao().getNivelRisco().toString() : null
        );
    }
} 