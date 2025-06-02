package br.com.fiap.Floodless.controller;

import br.com.fiap.Floodless.dto.UsuarioRequestDTO;
import br.com.fiap.Floodless.dto.UsuarioResponseDTO;
import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.model.entities.Usuario;
import br.com.fiap.Floodless.service.RegiaoService;
import br.com.fiap.Floodless.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RegiaoService regiaoService;

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarTodos()
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
    }

    @GetMapping("/regiao/{regiaoId}")
    @Operation(summary = "Buscar usuários por região", description = "Retorna uma lista de usuários de uma região específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários da região retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de busca inválidos")
    })
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorRegiao(
            @Parameter(description = "ID da região") @PathVariable Long regiaoId) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorRegiao(regiaoId)
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/notificacoes/regiao/{regiaoId}")
    @Operation(summary = "Buscar usuários para notificar por região", description = "Retorna uma lista de usuários que aceitam notificações em uma região específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de busca inválidos")
    })
    public ResponseEntity<List<UsuarioResponseDTO>> buscarUsuariosParaNotificar(
            @Parameter(description = "ID da região") @PathVariable Long regiaoId) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarUsuariosParaNotificar(regiaoId)
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/alertas/regiao/{regiaoId}")
    @Operation(summary = "Buscar usuários para alertar por região", description = "Retorna uma lista de usuários que aceitam alertas em uma região específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de busca inválidos")
    })
    public ResponseEntity<List<UsuarioResponseDTO>> buscarUsuariosParaAlertar(
            @Parameter(description = "ID da região") @PathVariable Long regiaoId) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarUsuariosParaAlertar(regiaoId)
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/notificacoes")
    @Operation(summary = "Buscar todos os usuários para notificar", description = "Retorna uma lista de todos os usuários que aceitam notificações")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodosParaNotificar() {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarTodosParaNotificar()
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/alertas")
    @Operation(summary = "Buscar todos os usuários para alertar", description = "Retorna uma lista de todos os usuários que aceitam alertas")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodosParaAlertar() {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarTodosParaAlertar()
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cadastra um novo usuário no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos no cadastro do usuário")
    })
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @RequestBody @Valid UsuarioRequestDTO dto,
            UriComponentsBuilder uriBuilder) {
        Usuario usuario = new Usuario(dto);
        
        // Busca e define a região do usuário
        Regiao regiao = regiaoService.buscarPorId(dto.regiaoId());
        usuario.setRegiao(regiao);
        
        Usuario usuarioSalvo = usuarioService.cadastrar(usuario);
        
        URI uri = uriBuilder.path("/api/usuarios/{id}")
            .buildAndExpand(usuarioSalvo.getId())
            .toUri();
            
        return ResponseEntity.created(uri).body(UsuarioResponseDTO.fromEntity(usuarioSalvo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na atualização do usuário"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @RequestBody @Valid UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario(dto);
        
        // Busca e define a região do usuário
        Regiao regiao = regiaoService.buscarPorId(dto.regiaoId());
        usuario.setRegiao(regiao);
        
        Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuarioAtualizado));
    }

    @PatchMapping("/{id}/preferencias-notificacao")
    @Operation(summary = "Atualizar preferências de notificação", description = "Atualiza as preferências de notificação de um usuário")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Preferências atualizadas com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> atualizarPreferenciasNotificacao(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Parameter(description = "Receber notificações gerais") @RequestParam(required = false) Boolean receberNotificacoes,
            @Parameter(description = "Receber alertas de emergência") @RequestParam(required = false) Boolean receberAlertas) {
        usuarioService.atualizarPreferenciasNotificacao(id, receberNotificacoes, receberAlertas);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/registrar-login")
    @Operation(summary = "Registrar login", description = "Registra um novo acesso do usuário ao sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Login registrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> registrarLogin(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.registrarLogin(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do usuário") @PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 