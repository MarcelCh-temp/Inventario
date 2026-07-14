package vetnova.inventario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.inventario.dto.*;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.model.*;
import vetnova.inventario.repository.DetallePedidoProveedorRepository;
import vetnova.inventario.repository.MovimientoInventarioRepository;
import vetnova.inventario.repository.PedidoProveedorRepository;
import vetnova.inventario.repository.StockRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoProveedorService {

    private final PedidoProveedorRepository pedidoProveedorRepository;
    private final DetallePedidoProveedorRepository detalleRepository;
    private final StockRepository stockRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Transactional
    public PedidoProveedorResponse crear(PedidoProveedorRequest request) {
        PedidoProveedor pedido = PedidoProveedor.builder()
                .proveedorId(request.getProveedorId())
                .sucursalId(request.getSucursalId())
                .usuarioId(request.getUsuarioId())
                .observaciones(request.getObservaciones())
                .estado(EstadoPedido.PENDIENTE)
                .build();
        pedido = pedidoProveedorRepository.save(pedido);

        for (DetallePedidoRequest item : request.getDetalles()) {
            detalleRepository.save(DetallePedidoProveedor.builder()
                    .pedidoId(pedido.getId())
                    .productoId(item.getProductoId())
                    .cantidadSolicitada(item.getCantidadSolicitada())
                    .cantidadRecibida(0)
                    .precioUnitario(item.getPrecioUnitario())
                    .build());
        }

        return mapearResponse(pedido);
    }

    public List<PedidoProveedorResponse> listarTodos() {
        return pedidoProveedorRepository.findAll().stream().map(this::mapearResponse).toList();
    }

    public PedidoProveedorResponse obtenerPorId(Long id) {
        return mapearResponse(buscarPedido(id));
    }

    @Transactional
    public PedidoProveedorResponse actualizarEstado(Long id, ActualizarEstadoPedidoRequest request) {
        PedidoProveedor pedido = buscarPedido(id);

        if (pedido.getEstado() == EstadoPedido.RECIBIDO) {
            throw new IllegalArgumentException("No se puede cambiar el estado de un pedido ya recibido");
        }

        pedido.setEstado(request.getEstado());
        return mapearResponse(pedidoProveedorRepository.save(pedido));
    }

    @Transactional
    public PedidoProveedorResponse recibir(Long id, RecibirPedidoRequest request) {
        PedidoProveedor pedido = buscarPedido(id);

        if (pedido.getEstado() == EstadoPedido.RECIBIDO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException(
                    "El pedido se encuentra " + pedido.getEstado().name().toLowerCase() + " y no puede recibirse");
        }

        for (RecepcionDetalleItem item : request.getItems()) {
            DetallePedidoProveedor detalle = detalleRepository.findById(item.getDetalleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró el detalle de pedido con id: " + item.getDetalleId()));

            detalle.setCantidadRecibida(item.getCantidadRecibida());
            detalleRepository.save(detalle);

            Stock stock = stockRepository.findByProductoIdAndSucursalId(detalle.getProductoId(), pedido.getSucursalId())
                    .orElseGet(() -> Stock.builder()
                            .productoId(detalle.getProductoId())
                            .sucursalId(pedido.getSucursalId())
                            .cantidadDisponible(0)
                            .cantidadReservada(0)
                            .build());
            stock.setCantidadDisponible(stock.getCantidadDisponible() + item.getCantidadRecibida());
            stockRepository.save(stock);

            movimientoRepository.save(MovimientoInventario.builder()
                    .productoId(detalle.getProductoId())
                    .sucursalId(pedido.getSucursalId())
                    .tipoMovimiento(TipoMovimiento.ENTRADA)
                    .cantidad(item.getCantidadRecibida())
                    .motivo("Recepción de pedido a proveedor #" + pedido.getId())
                    .usuarioId(pedido.getUsuarioId())
                    .referenciaDocumento("PEDIDO-" + pedido.getId())
                    .build());
        }

        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setFechaRecepcion(LocalDateTime.now());
        return mapearResponse(pedidoProveedorRepository.save(pedido));
    }

    private PedidoProveedor buscarPedido(Long id) {
        return pedidoProveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pedido con id: " + id));
    }

    private PedidoProveedorResponse mapearResponse(PedidoProveedor pedido) {
        List<DetallePedidoResponse> detalles = detalleRepository.findByPedidoId(pedido.getId()).stream()
                .map(d -> DetallePedidoResponse.builder()
                        .id(d.getId())
                        .productoId(d.getProductoId())
                        .cantidadSolicitada(d.getCantidadSolicitada())
                        .cantidadRecibida(d.getCantidadRecibida())
                        .precioUnitario(d.getPrecioUnitario())
                        .build())
                .toList();

        return PedidoProveedorResponse.builder()
                .id(pedido.getId())
                .proveedorId(pedido.getProveedorId())
                .estado(pedido.getEstado())
                .sucursalId(pedido.getSucursalId())
                .usuarioId(pedido.getUsuarioId())
                .observaciones(pedido.getObservaciones())
                .fechaPedido(pedido.getFechaPedido())
                .fechaRecepcion(pedido.getFechaRecepcion())
                .detalles(detalles)
                .build();
    }
}
