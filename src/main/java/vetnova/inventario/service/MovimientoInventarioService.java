package vetnova.inventario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.inventario.client.AuthClient;
import vetnova.inventario.dto.MovimientoRequest;
import vetnova.inventario.dto.MovimientoResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.exception.StockInsuficienteException;
import vetnova.inventario.exception.TokenInvalidoException;
import vetnova.inventario.model.MovimientoInventario;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.Stock;
import vetnova.inventario.repository.MovimientoInventarioRepository;
import vetnova.inventario.repository.ProductoRepository;
import vetnova.inventario.repository.StockRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final StockRepository stockRepository;
    private final ProductoRepository productoRepository;
    private final AuthClient authClient;

    @Transactional
    public MovimientoResponse registrarMovimiento(MovimientoRequest request, String tokenUsuario) {
        if (tokenUsuario != null && !tokenUsuario.isBlank() && !authClient.tokenEsValido(tokenUsuario)) {
            throw new TokenInvalidoException("El token proporcionado no es válido o ha expirado");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con id: " + request.getProductoId()));

        Stock stock = stockRepository.findByProductoIdAndSucursalId(request.getProductoId(), request.getSucursalId())
                .orElseGet(() -> Stock.builder()
                        .productoId(request.getProductoId())
                        .sucursalId(request.getSucursalId())
                        .cantidadDisponible(0)
                        .cantidadReservada(0)
                        .build());

        switch (request.getTipoMovimiento()) {
            case ENTRADA, TRASLADO, AJUSTE ->
                    stock.setCantidadDisponible(stock.getCantidadDisponible() + request.getCantidad());
            case SALIDA -> {
                if (stock.getCantidadDisponible() < request.getCantidad()) {
                    throw new StockInsuficienteException(
                            "Stock insuficiente para el producto '" + producto.getNombre() + "'. Disponible: "
                                    + stock.getCantidadDisponible() + ", solicitado: " + request.getCantidad());
                }
                stock.setCantidadDisponible(stock.getCantidadDisponible() - request.getCantidad());
            }
        }
        stockRepository.save(stock);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .productoId(request.getProductoId())
                .sucursalId(request.getSucursalId())
                .tipoMovimiento(request.getTipoMovimiento())
                .cantidad(request.getCantidad())
                .motivo(request.getMotivo())
                .usuarioId(request.getUsuarioId())
                .referenciaDocumento(request.getReferenciaDocumento())
                .build();

        return mapearResponse(movimientoRepository.save(movimiento));
    }

    public List<MovimientoResponse> listarTodos() {
        return movimientoRepository.findAll().stream().map(this::mapearResponse).toList();
    }

    public List<MovimientoResponse> listarPorProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId).stream()
                .map(this::mapearResponse).toList();
    }

    public List<MovimientoResponse> listarPorSucursal(Long sucursalId) {
        return movimientoRepository.findBySucursalIdOrderByFechaMovimientoDesc(sucursalId).stream()
                .map(this::mapearResponse).toList();
    }

    private MovimientoResponse mapearResponse(MovimientoInventario movimiento) {
        return MovimientoResponse.builder()
                .id(movimiento.getId())
                .productoId(movimiento.getProductoId())
                .sucursalId(movimiento.getSucursalId())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .cantidad(movimiento.getCantidad())
                .motivo(movimiento.getMotivo())
                .usuarioId(movimiento.getUsuarioId())
                .referenciaDocumento(movimiento.getReferenciaDocumento())
                .fechaMovimiento(movimiento.getFechaMovimiento())
                .build();
    }
}
