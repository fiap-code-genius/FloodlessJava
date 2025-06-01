package br.com.fiap.Floodless.repositories;

import br.com.fiap.Floodless.model.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
}
