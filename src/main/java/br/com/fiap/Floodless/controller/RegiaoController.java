package br.com.fiap.Floodless.controller;

import br.com.fiap.Floodless.dto.RegiaoRequestDTO;
import br.com.fiap.Floodless.dto.RegiaoResponseDTO;
import br.com.fiap.Floodless.service.RegiaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/regioes")
@Tag(name = "Regiões", description = "API para gerenciamento de regiões e seus dados climáticos")
public class RegiaoController {

    @Autowired
    private RegiaoService regiaoService;

    @PostMapping
    @Operation(summary = "Criar nova região", 
               description = "Cria uma nova região e obtém automaticamente seus dados climáticos através de APIs externas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Região criada com sucesso",
                    content = @Content(schema = @Schema(implementation = RegiaoResponseDTO.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Dados inválidos fornecidos",
                    content = @Content),
        @ApiResponse(responseCode = "500", 
                    description = "Erro ao processar a requisição",
                    content = @Content)
    })
    public ResponseEntity<RegiaoResponseDTO> criar(
            @RequestBody @Valid RegiaoRequestDTO dto,
            UriComponentsBuilder uriBuilder) {
        RegiaoResponseDTO responseDTO = regiaoService.criarDTO(dto);
        URI uri = uriBuilder.path("/api/regioes/{id}").buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todas as regiões", 
               description = "Retorna uma lista com todas as regiões cadastradas e seus dados climáticos")
    @ApiResponse(responseCode = "200", 
                description = "Lista de regiões recuperada com sucesso",
                content = @Content(schema = @Schema(implementation = RegiaoResponseDTO.class)))
    public ResponseEntity<List<RegiaoResponseDTO>> listar() {
        List<RegiaoResponseDTO> regioes = regiaoService.listarTodosDTO();
        return ResponseEntity.ok(regioes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar região por ID", 
               description = "Retorna os dados de uma região específica baseado no ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Região encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = RegiaoResponseDTO.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Região não encontrada",
                    content = @Content)
    })
    public ResponseEntity<RegiaoResponseDTO> buscarPorId(
            @Parameter(description = "ID da região a ser buscada") 
            @PathVariable Long id) {
        RegiaoResponseDTO regiao = regiaoService.buscarPorIdDTO(id);
        return ResponseEntity.ok(regiao);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar região", 
               description = "Atualiza os dados de uma região existente e recalcula seus dados climáticos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Região atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = RegiaoResponseDTO.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Dados inválidos fornecidos",
                    content = @Content),
        @ApiResponse(responseCode = "404", 
                    description = "Região não encontrada",
                    content = @Content)
    })
    public ResponseEntity<RegiaoResponseDTO> atualizar(
            @Parameter(description = "ID da região a ser atualizada") 
            @PathVariable Long id,
            @RequestBody @Valid RegiaoRequestDTO dto) {
        RegiaoResponseDTO regiao = regiaoService.atualizarDTO(id, dto);
        return ResponseEntity.ok(regiao);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar região", 
               description = "Remove uma região do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                    description = "Região deletada com sucesso",
                    content = @Content),
        @ApiResponse(responseCode = "404", 
                    description = "Região não encontrada",
                    content = @Content)
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da região a ser deletada") 
            @PathVariable Long id) {
        regiaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
} 