package edu.mondragon.pbl.gertuko.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.mondragon.pbl.gertuko.model.Estado;
import edu.mondragon.pbl.gertuko.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Producto findByProductoId(int productoId);
    List<Producto> findByVendedor_UsuarioId(int vendedorId); 
    List<Producto> findByVendedor_UsuarioIdAndEstadoNot(int vendedorId, Estado estado);
    List<Producto> findByEstadoNotOrderByMediaEstrellasDesc(Estado estado);
    List<Producto> findByEtiqueta_EtiquetaIdOrderByMediaEstrellasDesc(int etiquetaId);
    List<Producto> findByVendedor_UsuarioIdNotOrderByMediaEstrellasDesc(int vendedorId);
    List<Producto> findByEtiqueta_EtiquetaIdAndEstadoNotOrderByMediaEstrellasDesc(int etiquetaId, Estado estado);
    List<Producto> findByVendedor_UsuarioIdNotAndEstadoNotOrderByMediaEstrellasDesc(int usuarioId, Estado estado);
    List<Producto> findByVendedor_Zona_ZonaIdAndEstadoNotOrderByMediaEstrellasDesc(int zonaId, Estado estado);
    List<Producto> findByEtiqueta_EtiquetaIdAndVendedor_Zona_ZonaIdAndEstadoNotOrderByMediaEstrellasDesc(int etiquetaId, int zonaId, Estado estado);
    List<Producto> findByEtiqueta_EtiquetaIdAndVendedor_UsuarioIdNotOrderByMediaEstrellasDesc(int etiquetaId, int usuarioId);
    List<Producto> findByVendedor_UsuarioIdNotAndEtiqueta_EtiquetaIdAndEstadoNotOrderByMediaEstrellasDesc(int usuarioId, int etiquetaId, Estado estado);
}
