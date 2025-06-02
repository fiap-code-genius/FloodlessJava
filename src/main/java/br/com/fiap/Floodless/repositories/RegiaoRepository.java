package br.com.fiap.Floodless.repositories;

import br.com.fiap.Floodless.model.entities.Regiao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegiaoRepository extends JpaRepository<Regiao, Long> {
    List<Regiao> findByEstadoIgnoreCase(String estado);
} 