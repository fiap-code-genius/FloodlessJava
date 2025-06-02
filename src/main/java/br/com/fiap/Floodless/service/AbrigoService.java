package br.com.fiap.Floodless.service;

import br.com.fiap.Floodless.model.entities.Abrigo;
import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import br.com.fiap.Floodless.repositories.AbrigoRepository;
import br.com.fiap.Floodless.repositories.RegiaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AbrigoService {
    private static final Logger logger = LoggerFactory.getLogger(AbrigoService.class);

    @Autowired
    private AbrigoRepository abrigoRepository;

    @Autowired
    private RegiaoRepository regiaoRepository;

    @Transactional(readOnly = true)
    public List<Abrigo> buscarTodos() {
        return abrigoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Abrigo> buscarAbrigosDisponiveis() {
        return abrigoRepository.findByDisponivelTrue();
    }

    @Transactional
    public Abrigo cadastrar(Abrigo abrigo) {
        abrigo.setDisponivel(true);
        abrigo.setAtivo(true);
        return abrigoRepository.save(abrigo);
    }

    @Transactional
    public Abrigo atualizar(Long id, Abrigo abrigoAtualizado) {
        Abrigo abrigo = buscarPorId(id);
        
        abrigo.setNome(abrigoAtualizado.getNome());
        abrigo.setDescricao(abrigoAtualizado.getDescricao());
        abrigo.setEndereco(abrigoAtualizado.getEndereco());
        abrigo.setCapacidadeMaxima(abrigoAtualizado.getCapacidadeMaxima());
        abrigo.setOcupacaoAtual(abrigoAtualizado.getOcupacaoAtual());
        abrigo.setTelefoneContato(abrigoAtualizado.getTelefoneContato());
        abrigo.setEmailContato(abrigoAtualizado.getEmailContato());
        abrigo.setAtivo(abrigoAtualizado.getAtivo());
        abrigo.setDisponivel(abrigoAtualizado.getDisponivel());
        
        if (abrigoAtualizado.getRegiao() != null) {
            Regiao regiao = regiaoRepository.findById(abrigoAtualizado.getRegiao().getId())
                .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));
            abrigo.setRegiao(regiao);
        }

        return abrigoRepository.save(abrigo);
    }

    @Transactional
    public void atualizarOcupacao(Long id, int novaOcupacao) {
        Abrigo abrigo = buscarPorId(id);
        
        if (novaOcupacao > abrigo.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Ocupação não pode exceder a capacidade do abrigo");
        }
        
        abrigo.setOcupacaoAtual(novaOcupacao);
        
        // Atualiza disponibilidade baseado na ocupação
        abrigo.setDisponivel(novaOcupacao < abrigo.getCapacidadeMaxima());
        
        abrigoRepository.save(abrigo);
    }

    @Transactional
    public void registrarEntrada(Long id, int quantidade) {
        Abrigo abrigo = buscarPorId(id);
        
        int novaOcupacao = abrigo.getOcupacaoAtual() + quantidade;
        if (novaOcupacao > abrigo.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("Não há vagas suficientes no abrigo");
        }
        
        abrigo.setOcupacaoAtual(novaOcupacao);
        if (novaOcupacao >= abrigo.getCapacidadeMaxima()) {
            abrigo.setDisponivel(false);
        }
        
        abrigoRepository.save(abrigo);
    }

    @Transactional
    public void registrarSaida(Long id, int quantidade) {
        Abrigo abrigo = buscarPorId(id);
        
        int novaOcupacao = abrigo.getOcupacaoAtual() - quantidade;
        if (novaOcupacao < 0) {
            throw new IllegalArgumentException("Quantidade de saída maior que ocupação atual");
        }
        
        abrigo.setOcupacaoAtual(novaOcupacao);
        abrigo.setDisponivel(true);
        
        abrigoRepository.save(abrigo);
    }

    @Transactional
    public void delete(Long id) {
        Abrigo abrigo = buscarPorId(id);
        if (abrigo.getOcupacaoAtual() > 0) {
            throw new IllegalStateException("Não é possível remover um abrigo com pessoas");
        }
        abrigoRepository.delete(abrigo);
    }

    private Abrigo buscarPorId(Long id) {
        return abrigoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));
    }
} 