package vetnova.inventario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.inventario.dto.ProductoRequest;
import vetnova.inventario.dto.ProductoResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.exception.SkuDuplicadoException;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.repository.ProductoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (request.getCodigoSku() != null && !request.getCodigoSku().isBlank()
                && productoRepository.existsByCodigoSku(request.getCodigoSku())) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + request.getCodigoSku());
        }

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .tipoUso(request.getTipoUso())
                .precio(request.getPrecio())
                .codigoSku(request.getCodigoSku())
                .unidadMedida(request.getUnidadMedida())
                .stockMinimo(request.getStockMinimo())
                .activo(true)
                .build();

        return mapearResponse(productoRepository.save(producto));
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream().map(this::mapearResponse).toList();
    }

    public ProductoResponse obtenerPorId(Long id) {
        return mapearResponse(buscarPorId(id));
    }

    public List<ProductoResponse> listarPorCategoria(CategoriaProducto categoria) {
        return productoRepository.findByCategoria(categoria).stream().map(this::mapearResponse).toList();
    }

    public List<ProductoResponse> listarPorTipoUso(TipoUso tipoUso) {
        return productoRepository.findByTipoUso(tipoUso).stream().map(this::mapearResponse).toList();
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);

        boolean cambioSku = request.getCodigoSku() != null && !request.getCodigoSku().equals(producto.getCodigoSku());
        if (cambioSku && productoRepository.existsByCodigoSku(request.getCodigoSku())) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + request.getCodigoSku());
        }

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setTipoUso(request.getTipoUso());
        producto.setPrecio(request.getPrecio());
        producto.setCodigoSku(request.getCodigoSku());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setStockMinimo(request.getStockMinimo());

        return mapearResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse desactivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivo(false);
        return mapearResponse(productoRepository.save(producto));
    }

    private Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con id: " + id));
    }

    private ProductoResponse mapearResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .categoria(producto.getCategoria())
                .tipoUso(producto.getTipoUso())
                .precio(producto.getPrecio())
                .codigoSku(producto.getCodigoSku())
                .unidadMedida(producto.getUnidadMedida())
                .stockMinimo(producto.getStockMinimo())
                .activo(producto.getActivo())
                .build();
    }
}
