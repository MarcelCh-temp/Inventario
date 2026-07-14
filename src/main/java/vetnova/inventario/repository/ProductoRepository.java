package vetnova.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.TipoUso;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria(CategoriaProducto categoria);

    List<Producto> findByTipoUso(TipoUso tipoUso);

    List<Producto> findByActivoTrue();

    Optional<Producto> findByCodigoSku(String codigoSku);

    boolean existsByCodigoSku(String codigoSku);
}
