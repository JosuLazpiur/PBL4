package edu.mondragon.pbl.gertuko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.mondragon.pbl.gertuko.model.Etiqueta;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {}