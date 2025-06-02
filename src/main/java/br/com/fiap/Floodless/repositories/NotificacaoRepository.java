package br.com.fiap.Floodless.repositories;

import br.com.fiap.Floodless.model.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    
    List<Notificacao> findAllByOrderByUrgenteDescDataCriacaoDesc();
    
    List<Notificacao> findByRegiaoIdOrderByDataCriacaoDesc(Long regiaoId);
    
    List<Notificacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);
    
    List<Notificacao> findByLidaFalseOrderByUrgenteDescDataCriacaoDesc();
    
    List<Notificacao> findByUsuarioIdAndLidaFalse(Long usuarioId);
}
