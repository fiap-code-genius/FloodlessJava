package br.com.fiap.Floodless.service;

import br.com.fiap.Floodless.dto.RegiaoRequestDTO;
import br.com.fiap.Floodless.dto.RegiaoResponseDTO;
import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.repositories.RegiaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegiaoService {
    private static final Logger logger = LoggerFactory.getLogger(RegiaoService.class);

    @Autowired
    private RegiaoRepository regiaoRepository;

    @Autowired
    private ClimaService climaService;

    @Transactional
    public RegiaoResponseDTO criarDTO(RegiaoRequestDTO dto) {
        Regiao regiao = new Regiao();
        regiao.setNome(dto.nome());
        regiao.setEstado(dto.estado());
        regiao.setCidade(dto.cidade());
        regiao.setBairro(dto.bairro());
        regiao.setCep(dto.cep());

        climaService.atualizarDadosClimaticos(regiao);

        regiao = regiaoRepository.save(regiao);
        return new RegiaoResponseDTO(regiao);
    }

    @Transactional
    public Regiao criar(RegiaoRequestDTO dto) {
        Regiao regiao = new Regiao();
        regiao.setNome(dto.nome());
        regiao.setEstado(dto.estado());
        regiao.setCidade(dto.cidade());
        regiao.setBairro(dto.bairro());
        regiao.setCep(dto.cep());

        climaService.atualizarDadosClimaticos(regiao);

        return regiaoRepository.save(regiao);
    }

    @Transactional(readOnly = true)
    public List<RegiaoResponseDTO> listarTodosDTO() {
        return regiaoRepository.findAll().stream()
                .map(RegiaoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Regiao> listarTodos() {
        return regiaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RegiaoResponseDTO buscarPorIdDTO(Long id) {
        return regiaoRepository.findById(id)
                .map(RegiaoResponseDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));
    }

    @Transactional(readOnly = true)
    public Regiao buscarPorId(Long id) {
        return regiaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));
    }

    @Transactional
    public RegiaoResponseDTO atualizarDTO(Long id, RegiaoRequestDTO dto) {
        Regiao regiao = regiaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));

        regiao.setNome(dto.nome());
        regiao.setEstado(dto.estado());
        regiao.setCidade(dto.cidade());
        regiao.setBairro(dto.bairro());
        regiao.setCep(dto.cep());

        climaService.atualizarDadosClimaticos(regiao);

        regiao = regiaoRepository.save(regiao);
        return new RegiaoResponseDTO(regiao);
    }

    @Transactional
    public Regiao atualizar(Long id, RegiaoRequestDTO dto) {
        Regiao regiao = regiaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));

        regiao.setNome(dto.nome());
        regiao.setEstado(dto.estado());
        regiao.setCidade(dto.cidade());
        regiao.setBairro(dto.bairro());
        regiao.setCep(dto.cep());

        climaService.atualizarDadosClimaticos(regiao);

        return regiaoRepository.save(regiao);
    }

    @Transactional
    public void deletar(Long id) {
        if (!regiaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Região não encontrada");
        }
        regiaoRepository.deleteById(id);
    }

    @Scheduled(fixedRate = 3600000) // Executa a cada 1 hora (3600000 ms)
    public void atualizarTodasRegioes() {
        logger.info("Iniciando atualização automática de todas as regiões");
        List<Regiao> regioes = regiaoRepository.findAll();
        
        int sucessos = 0;
        int falhas = 0;
        
        for (Regiao regiao : regioes) {
            try {
                logger.info("Atualizando dados climáticos para região: {} ({}/{})", 
                    regiao.getNome(), sucessos + falhas + 1, regioes.size());
                    
                climaService.atualizarDadosClimaticos(regiao);
                regiaoRepository.save(regiao);
                
                // Aumenta o delay entre atualizações para 30 segundos
                Thread.sleep(30000);
                
                sucessos++;
                logger.info("Atualização concluída para região: {}", regiao.getNome());
            } catch (Exception e) {
                falhas++;
                logger.error("Erro ao atualizar região {}: {} - {}", 
                    regiao.getNome(), e.getClass().getSimpleName(), e.getMessage());
            }
        }
        
        logger.info("Atualização automática concluída. Sucessos: {}, Falhas: {}", sucessos, falhas);
        
        // Se todas as atualizações falharam, registra um alerta
        if (falhas == regioes.size()) {
            logger.error("ALERTA: Todas as atualizações de região falharam. Possível problema com as APIs externas.");
        }
    }
} 