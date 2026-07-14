package vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.inventario.model.PedidoProveedor;

public interface PedidoProveedorRepository extends JpaRepository<PedidoProveedor, Long> {
}
