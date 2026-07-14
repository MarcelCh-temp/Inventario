package vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.inventario.model.DetallePedidoProveedor;

import java.util.List;

public interface DetallePedidoProveedorRepository extends JpaRepository<DetallePedidoProveedor, Long> {

    List<DetallePedidoProveedor> findByPedidoId(Long pedidoId);
}
