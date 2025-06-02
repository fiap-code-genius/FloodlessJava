package br.com.fiap.Floodless.service;

import br.com.fiap.Floodless.model.entities.Usuario;
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
public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRegiao(Long regiaoId) {
        return usuarioRepository.findByRegiaoId(regiaoId);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosParaNotificar(Long regiaoId) {
        return usuarioRepository.findByRegiaoIdAndReceberNotificacoesTrue(regiaoId);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosParaAlertar(Long regiaoId) {
        return usuarioRepository.findByRegiaoIdAndReceberAlertasTrue(regiaoId);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodosParaNotificar() {
        return usuarioRepository.findByReceberNotificacoesTrue();
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodosParaAlertar() {
        return usuarioRepository.findByReceberAlertasTrue();
    }

    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        // Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Configurar valores padrão
        usuario.setReceberNotificacoes(usuario.getReceberNotificacoes() != null ? usuario.getReceberNotificacoes() : true);
        usuario.setReceberAlertas(usuario.getReceberAlertas() != null ? usuario.getReceberAlertas() : true);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setUltimoLogin(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = buscarPorId(id);

        // Validar email único se estiver sendo alterado
        if (!usuario.getEmail().equals(usuarioAtualizado.getEmail()) &&
            usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setTelefone(usuarioAtualizado.getTelefone());
        usuario.setRegiao(usuarioAtualizado.getRegiao());
        usuario.setReceberNotificacoes(usuarioAtualizado.getReceberNotificacoes() != null ? usuarioAtualizado.getReceberNotificacoes() : usuario.getReceberNotificacoes());
        usuario.setReceberAlertas(usuarioAtualizado.getReceberAlertas() != null ? usuarioAtualizado.getReceberAlertas() : usuario.getReceberAlertas());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarPreferenciasNotificacao(Long id, Boolean receberNotificacoes, Boolean receberAlertas) {
        Usuario usuario = buscarPorId(id);
        
        if (receberNotificacoes != null) {
            usuario.setReceberNotificacoes(receberNotificacoes);
        }
        if (receberAlertas != null) {
            usuario.setReceberAlertas(receberAlertas);
        }
        
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void registrarLogin(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
} 