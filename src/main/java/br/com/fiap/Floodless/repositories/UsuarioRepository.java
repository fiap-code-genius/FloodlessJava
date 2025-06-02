package br.com.fiap.Floodless.repositories;

import br.com.fiap.Floodless.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByRegiaoId(Long regiaoId);
    
    List<Usuario> findByRegiaoIdAndReceberNotificacoesTrue(Long regiaoId);
    
    List<Usuario> findByRegiaoIdAndReceberAlertasTrue(Long regiaoId);
    
    List<Usuario> findByReceberNotificacoesTrue();
    
    List<Usuario> findByReceberAlertasTrue();
}
