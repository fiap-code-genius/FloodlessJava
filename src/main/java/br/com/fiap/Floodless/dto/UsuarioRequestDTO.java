package br.com.fiap.Floodless.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "DTO para criação e atualização de usuários")
public record UsuarioRequestDTO(
    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @NotBlank(message = "Nome é obrigatório")
    String nome,
    
    @Schema(description = "Email do usuário", example = "joao.silva@email.com")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    
    @Schema(description = "Senha do usuário", example = "Senha@123")
    @NotBlank(message = "Senha é obrigatória")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$", 
            message = "Senha deve conter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial")
    String senha,
    
    @Schema(description = "Telefone do usuário (10 ou 11 dígitos)", example = "11999999999")
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    String telefone,
    
    @Schema(description = "ID da região onde o usuário reside", example = "1")
    @NotNull(message = "ID da região é obrigatório")
    Long regiaoId,
    
    @Schema(description = "Receber notificações gerais", example = "true", defaultValue = "true")
    Boolean receberNotificacoes,
    
    @Schema(description = "Receber alertas de emergência", example = "true", defaultValue = "true")
    Boolean receberAlertas
) {} 