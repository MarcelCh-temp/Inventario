package vetnova.inventario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vetnova.inventario.dto.ItemVerificacionStock;
import vetnova.inventario.dto.StockResponse;
import vetnova.inventario.dto.VerificarStockRequest;
import vetnova.inventario.dto.VerificarStockResponse;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.Stock;
import vetnova.inventario.repository.ProductoRepository;
import vetnova.inventario.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductoRepository productoRepository;

    public List<StockResponse> listarTodo() {
        return stockRepository.findAll().stream().map(this::mapearResponse).toList();
    }

    public List<StockResponse> listarPorSucursal(Long sucursalId) {
        return stockRepository.findBySucursalId(sucursalId).stream().map(this::mapearResponse).toList();
    }

    public List<StockResponse> listarPorProducto(Long productoId) {
        return stockRepository.findByProductoId(productoId).stream().map(this::mapearResponse).toList();
    }

    public List<StockResponse> listarBajoMinimo() {
        return stockRepository.findAll().stream()
                .filter(this::estaBajoMinimo)
                .map(this::mapearResponse)
                .toList();
    }

    public VerificarStockResponse verificarDisponibilidad(VerificarStockRequest request) {
        List<String> productosSinStock = new ArrayList<>();

        for (ItemVerificacionStock item : request.getItems()) {
            Stock stock = stockRepository
                    .findByProductoIdAndSucursalId(item.getProductoId(), request.getSucursalId())
                    .orElse(null);

            int disponibleReal = (stock == null) ? 0 : stock.getCantidadDisponible() - stock.getCantidadReservada();

            if (disponibleReal < item.getCantidad()) {
                String nombre = productoRepository.findById(item.getProductoId())
                        .map(Producto::getNombre)
                        .orElse("Producto id " + item.getProductoId());
                productosSinStock.add(nombre);
            }
        }

        return VerificarStockResponse.builder()
                .disponible(productosSinStock.isEmpty())
                .productosSinStock(productosSinStock)
                .build();
    }

    private boolean estaBajoMinimo(Stock stock) {
        return productoRepository.findById(stock.getProductoId())
                .map(producto -> producto.getStockMinimo() != null
                        && stock.getCantidadDisponible() < producto.getStockMinimo())
                .orElse(false);
    }

    private StockResponse mapearResponse(Stock stock) {
        String nombreProducto = productoRepository.findById(stock.getProductoId())
                .map(Producto::getNombre)
                .orElse(null);

        return StockResponse.builder()
                .id(stock.getId())
                .productoId(stock.getProductoId())
                .nombreProducto(nombreProducto)
                .sucursalId(stock.getSucursalId())
                .cantidadDisponible(stock.getCantidadDisponible())
                .cantidadReservada(stock.getCantidadReservada())
                .ultimaActualizacion(stock.getUltimaActualizacion())
                .build();
    }
}
