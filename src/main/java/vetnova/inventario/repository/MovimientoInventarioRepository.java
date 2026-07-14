package vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.inventario.model.MovimientoInventario;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);

    List<MovimientoInventario> findBySucursalIdOrderByFechaMovimientoDesc(Long sucursalId);
}
