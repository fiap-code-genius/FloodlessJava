package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Usuario;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "DTO com informações completas de um usuário")
public record UsuarioResponseDTO(
    @Schema(description = "ID do usuário", example = "1")
    Long id,

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    String nome,

    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    String email,

    @Schema(description = "Telefone do usuário", example = "11999999999")
    String telefone,

    @Schema(description = "ID da região onde o usuário reside", example = "1")
    Long regiaoId,

    @Schema(description = "Nome da região", example = "Vila Mariana")
    String regiaoNome,

    @Schema(description = "Bairro da região", example = "Vila Mariana")
    String regiaoBairro,

    @Schema(description = "Cidade da região", example = "São Paulo")
    String regiaoCidade,

    @Schema(description = "Estado da região", example = "SP")
    String regiaoEstado,

    @Schema(description = "Nível de risco atual da região", example = "BAIXO")
    NivelRisco regiaoNivelRisco,

    @Schema(description = "Receber notificações gerais", example = "true")
    Boolean receberNotificacoes,

    @Schema(description = "Receber alertas de emergência", example = "true")
    Boolean receberAlertas,

    @Schema(description = "Data e hora do último login", example = "2024-03-20T10:30:00")
    LocalDateTime ultimoLogin,

    @Schema(description = "Data e hora do cadastro", example = "2024-03-01T14:20:00")
    LocalDateTime dataCadastro,

    @Schema(description = "Lista de notificações não lidas")
    List<NotificacaoSimplificadaDTO> notificacoesNaoLidas
) {
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.getRegiao().getId(),
            usuario.getRegiao().getNome(),
            usuario.getRegiao().getBairro(),
            usuario.getRegiao().getCidade(),
            usuario.getRegiao().getEstado(),
            usuario.getRegiao().getNivelRisco(),
            usuario.getReceberNotificacoes(),
            usuario.getReceberAlertas(),
            usuario.getUltimoLogin(),
            usuario.getDataCadastro(),
            usuario.getNotificacoes() != null ?
                usuario.getNotificacoes().stream()
                    .filter(n -> !n.getLida())
                    .map(NotificacaoSimplificadaDTO::fromEntity)
                    .toList() :
                List.of()
        );
    }
} 