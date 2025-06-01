package br.com.fiap.Floodless.repositories;

import br.com.fiap.Floodless.model.entities.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegiaoRepository extends JpaRepository<Regiao, Long> {

    List<Regiao> findByEstadoIgnoreCase(String estado);
}
