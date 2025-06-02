package br.com.fiap.Floodless.repositories;

import br.com.fiap.Floodless.model.entities.Abrigo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbrigoRepository extends JpaRepository<Abrigo, Long> {
    
    List<Abrigo> findByDisponivelTrue();
}
