package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.mondragon.pbl.gertuko.model.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Integer> {
    List<Valoracion> findByProducto_ProductoId(int productoId);
    int countByProducto_ProductoId(Integer productoId);
}
