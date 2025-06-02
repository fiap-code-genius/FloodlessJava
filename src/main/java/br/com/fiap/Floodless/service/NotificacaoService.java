package br.com.fiap.Floodless.service;

import br.com.fiap.Floodless.model.entities.Notificacao;
import br.com.fiap.Floodless.model.entities.Regiao;
import br.com.fiap.Floodless.model.entities.Usuario;
import br.com.fiap.Floodless.model.enums.NivelRisco;
import br.com.fiap.Floodless.model.enums.TipoNotificacao;
import br.com.fiap.Floodless.repositories.NotificacaoRepository;
import br.com.fiap.Floodless.repositories.RegiaoRepository;
import br.com.fiap.Floodless.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {
    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private RegiaoRepository regiaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Notificacao> buscarTodas() {
        return notificacaoRepository.findAllByOrderByUrgenteDescDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public List<Notificacao> buscarPorRegiao(Long regiaoId) {
        return notificacaoRepository.findByRegiaoIdOrderByDataCriacaoDesc(regiaoId);
    }

    @Transactional(readOnly = true)
    public List<Notificacao> buscarPorUsuario(Long usuarioId) {
        return notificacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Notificacao> buscarNaoLidas() {
        return notificacaoRepository.findByLidaFalseOrderByUrgenteDescDataCriacaoDesc();
    }

    @Transactional
    public Notificacao criarNotificacao(
            String titulo,
            String mensagem,
            TipoNotificacao tipo,
            Long regiaoId,
            Long usuarioId,
            Boolean urgente
    ) {
        Regiao regiao = regiaoRepository.findById(regiaoId)
            .orElseThrow(() -> new EntityNotFoundException("Região não encontrada"));

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        }

        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setRegiao(regiao);
        notificacao.setUsuario(usuario);
        notificacao.setUrgente(urgente != null ? urgente : false);
        notificacao.setLida(false);
        notificacao.setDataCriacao(LocalDateTime.now());

        return notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void criarNotificacaoMudancaRisco(Regiao regiao, NivelRisco nivelAnterior) {
        String titulo = String.format("Mudança no Nível de Risco - %s", regiao.getBairro());
        String mensagem = String.format(
            "O nível de risco da região %s mudou de %s para %s. %s",
            regiao.getBairro(),
            nivelAnterior,
            regiao.getNivelRisco(),
            regiao.getNivelRisco().ordinal() > nivelAnterior.ordinal() 
                ? "Fique atento às orientações das autoridades."
                : "Continue monitorando a situação."
        );

        criarNotificacao(
            titulo,
            mensagem,
            TipoNotificacao.MUDANCA_NIVEL_RISCO,
            regiao.getId(),
            null,
            regiao.getNivelRisco().ordinal() > nivelAnterior.ordinal()
        );
    }

    @Transactional
    public void criarNotificacaoEvacuacao(Regiao regiao) {
        String titulo = String.format("EVACUAÇÃO NECESSÁRIA - %s", regiao.getBairro());
        String mensagem = String.format(
            "ATENÇÃO! Devido ao alto risco de enchente, a evacuação da região %s é necessária. " +
            "Dirija-se imediatamente ao abrigo mais próximo. " +
            "Siga as orientações das autoridades locais.",
            regiao.getBairro()
        );

        criarNotificacao(
            titulo,
            mensagem,
            TipoNotificacao.EVACUACAO,
            regiao.getId(),
            null,
            true
        );
    }

    @Transactional
    public void marcarComoLida(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));
            
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioIdAndLidaFalse(usuarioId);
        
        notificacoes.forEach(n -> n.setLida(true));
        
        notificacaoRepository.saveAll(notificacoes);
    }

    @Transactional
    public void delete(Long id) {
        notificacaoRepository.deleteById(id);
    }
} 