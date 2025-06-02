package br.com.fiap.Floodless.dto;

import br.com.fiap.Floodless.model.entities.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO com informações básicas de um usuário")
public record UsuarioSimplificadoDTO(
    @Schema(description = "ID do usuário", example = "1")
    Long id,

    @Schema(description = "Nome completo do usuário", example = "João Silva")
    String nome,

    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    String email
) {
    public UsuarioSimplificadoDTO(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail()
        );
    }
} 