package vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.inventario.model.Proveedor;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    List<Proveedor> findByActivoTrue();
}
