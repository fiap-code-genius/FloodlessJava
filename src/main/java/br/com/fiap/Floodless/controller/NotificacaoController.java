package br.com.fiap.Floodless.controller;

import br.com.fiap.Floodless.dto.NotificacaoRequestDTO;
import br.com.fiap.Floodless.dto.NotificacaoResponseDTO;
import br.com.fiap.Floodless.model.entities.Notificacao;
import br.com.fiap.Floodless.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificações", description = "Gerenciamento de notificações e alertas do sistema")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as notificações", description = "Retorna uma lista com todas as notificações ordenadas por urgência e data de criação")
    @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarTodas() {
        List<NotificacaoResponseDTO> notificacoes = notificacaoService.buscarTodas()
            .stream()
            .map(NotificacaoResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/nao-lidas")
    @Operation(summary = "Listar notificações não lidas", description = "Retorna uma lista com todas as notificações não lidas ordenadas por urgência e data")
    @ApiResponse(responseCode = "200", description = "Lista de notificações não lidas retornada com sucesso")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarNaoLidas() {
        List<NotificacaoResponseDTO> notificacoes = notificacaoService.buscarNaoLidas()
            .stream()
            .map(NotificacaoResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/regiao/{regiaoId}")
    @Operation(summary = "Buscar notificações por região", description = "Retorna uma lista de notificações de uma região específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notificações da região retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Região não encontrada")
    })
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarPorRegiao(
            @Parameter(description = "ID da região") @PathVariable Long regiaoId) {
        List<NotificacaoResponseDTO> notificacoes = notificacaoService.buscarPorRegiao(regiaoId)
            .stream()
            .map(NotificacaoResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar notificações por usuário", description = "Retorna uma lista de notificações de um usuário específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de notificações do usuário retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId) {
        List<NotificacaoResponseDTO> notificacoes = notificacaoService.buscarPorUsuario(usuarioId)
            .stream()
            .map(NotificacaoResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(notificacoes);
    }

    @PostMapping
    @Operation(summary = "Criar notificação", description = "Cria uma nova notificação no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Notificação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na criação da notificação"),
        @ApiResponse(responseCode = "404", description = "Região ou usuário não encontrado")
    })
    public ResponseEntity<NotificacaoResponseDTO> criar(
            @RequestBody @Valid NotificacaoRequestDTO dto,
            UriComponentsBuilder uriBuilder) {
        Notificacao notificacao = notificacaoService.criarNotificacao(
            dto.titulo(),
            dto.mensagem(),
            dto.tipo(),
            dto.regiaoId(),
            dto.usuarioId(),
            dto.urgente()
        );

        URI uri = uriBuilder.path("/api/notificacoes/{id}")
            .buildAndExpand(notificacao.getId())
            .toUri();

        return ResponseEntity.created(uri).body(NotificacaoResponseDTO.fromEntity(notificacao));
    }

    @PatchMapping("/{id}/marcar-como-lida")
    @Operation(summary = "Marcar notificação como lida", description = "Marca uma notificação específica como lida")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificação marcada como lida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<Void> marcarComoLida(
            @Parameter(description = "ID da notificação") @PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/usuario/{usuarioId}/marcar-todas-como-lidas")
    @Operation(summary = "Marcar todas notificações como lidas", description = "Marca todas as notificações não lidas de um usuário como lidas")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificações marcadas como lidas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> marcarTodasComoLidas(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId) {
        notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir notificação", description = "Remove uma notificação do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificação excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID da notificação") @PathVariable Long id) {
        notificacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 