package vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.inventario.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findBySucursalId(Long sucursalId);

    List<Stock> findByProductoId(Long productoId);

    Optional<Stock> findByProductoIdAndSucursalId(Long productoId, Long sucursalId);
}
