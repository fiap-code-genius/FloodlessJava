package br.com.fiap.Floodless.controller;

import br.com.fiap.Floodless.dto.AbrigoRequestDTO;
import br.com.fiap.Floodless.dto.AbrigoResponseDTO;
import br.com.fiap.Floodless.model.entities.Abrigo;
import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.service.AbrigoService;
import br.com.fiap.Floodless.repositories.RegiaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/abrigos")
@Tag(name = "Abrigos", description = "Gerenciamento de abrigos para situações de emergência")
public class AbrigoController {

    @Autowired
    private AbrigoService abrigoService;

    @Autowired
    private RegiaoRepository regiaoRepository;

    @GetMapping
    @Operation(summary = "Listar todos os abrigos", description = "Retorna uma lista com todos os abrigos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de abrigos retornada com sucesso")
    public ResponseEntity<List<AbrigoResponseDTO>> listarTodos() {
        List<AbrigoResponseDTO> abrigos = abrigoService.buscarTodos()
            .stream()
            .map(AbrigoResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(abrigos);
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar abrigos disponíveis", description = "Retorna uma lista com todos os abrigos que possuem vagas disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de abrigos disponíveis retornada com sucesso")
    public ResponseEntity<List<AbrigoResponseDTO>> listarDisponiveis() {
        List<AbrigoResponseDTO> abrigos = abrigoService.buscarAbrigosDisponiveis()
            .stream()
            .map(AbrigoResponseDTO::fromEntity)
            .toList();
        return ResponseEntity.ok(abrigos);
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo abrigo", description = "Cadastra um novo abrigo no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Abrigo cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos no cadastro do abrigo")
    })
    public ResponseEntity<AbrigoResponseDTO> cadastrar(
            @RequestBody @Valid AbrigoRequestDTO dto,
            UriComponentsBuilder uriBuilder) {
        Abrigo abrigo = new Abrigo(dto);
        
        Regiao regiao = regiaoRepository.findById(dto.regiaoId())
            .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));
        abrigo.setRegiao(regiao);
        
        Abrigo abrigoSalvo = abrigoService.cadastrar(abrigo);
        
        URI uri = uriBuilder.path("/api/abrigos/{id}")
            .buildAndExpand(abrigoSalvo.getId())
            .toUri();
            
        return ResponseEntity.created(uri).body(AbrigoResponseDTO.fromEntity(abrigoSalvo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar abrigo", description = "Atualiza os dados de um abrigo existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Abrigo atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na atualização do abrigo"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<AbrigoResponseDTO> atualizar(
            @Parameter(description = "ID do abrigo") @PathVariable Long id,
            @RequestBody @Valid AbrigoRequestDTO dto) {
        Abrigo abrigo = new Abrigo(dto);
        
        Regiao regiao = regiaoRepository.findById(dto.regiaoId())
            .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));
        abrigo.setRegiao(regiao);
        
        Abrigo abrigoAtualizado = abrigoService.atualizar(id, abrigo);
        return ResponseEntity.ok(AbrigoResponseDTO.fromEntity(abrigoAtualizado));
    }

    @PatchMapping("/{id}/ocupacao")
    @Operation(summary = "Atualizar ocupação", description = "Atualiza a quantidade de pessoas ocupando o abrigo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ocupação atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida de ocupação"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<Void> atualizarOcupacao(
            @Parameter(description = "ID do abrigo") @PathVariable Long id,
            @Parameter(description = "Nova quantidade de ocupantes") @RequestParam int ocupacao) {
        abrigoService.atualizarOcupacao(id, ocupacao);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/entrada")
    @Operation(summary = "Registrar entrada", description = "Registra a entrada de pessoas no abrigo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Entrada registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida ou abrigo sem vagas suficientes"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<Void> registrarEntrada(
            @Parameter(description = "ID do abrigo") @PathVariable Long id,
            @Parameter(description = "Quantidade de pessoas entrando") @RequestParam int quantidade) {
        abrigoService.registrarEntrada(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/saida")
    @Operation(summary = "Registrar saída", description = "Registra a saída de pessoas do abrigo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Saída registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida de saída"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<Void> registrarSaida(
            @Parameter(description = "ID do abrigo") @PathVariable Long id,
            @Parameter(description = "Quantidade de pessoas saindo") @RequestParam int quantidade) {
        abrigoService.registrarSaida(id, quantidade);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir abrigo", description = "Remove um abrigo do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Abrigo excluído com sucesso"),
        @ApiResponse(responseCode = "400", description = "Não é possível excluir abrigo com pessoas"),
        @ApiResponse(responseCode = "404", description = "Abrigo não encontrado")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do abrigo") @PathVariable Long id) {
        abrigoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}