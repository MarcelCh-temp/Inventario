package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.*;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.model.*;
import vetnova.inventario.repository.DetallePedidoProveedorRepository;
import vetnova.inventario.repository.MovimientoInventarioRepository;
import vetnova.inventario.repository.PedidoProveedorRepository;
import vetnova.inventario.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoProveedorService - pruebas unitarias con 100% de cobertura")
class PedidoProveedorServiceTest {

    @Mock private PedidoProveedorRepository pedidoRepo;
    @Mock private DetallePedidoProveedorRepository detalleRepo;
    @Mock private StockRepository stockRepo;
    @Mock private MovimientoInventarioRepository movimientoRepo;

    @InjectMocks private PedidoProveedorService pedidoService;

    private PedidoProveedor pedidoPendiente;

    @BeforeEach
    void setUp() {
        pedidoPendiente = PedidoProveedor.builder()
                .id(1L).proveedorId(10L).sucursalId(2L).usuarioId(5L)
                .estado(EstadoPedido.PENDIENTE).build();
    }

    @Nested @DisplayName("crear()")
    class Crear {

        @Test @DisplayName("Crea el pedido y guarda cada detalle")
        void creaPedidoConDetalles() {
            DetallePedidoRequest detalleReq = new DetallePedidoRequest(7L, 20, new BigDecimal("1500"));
            PedidoProveedorRequest request = new PedidoProveedorRequest(
                    10L, 2L, 5L, "Obs", List.of(detalleReq));

            when(pedidoRepo.save(any(PedidoProveedor.class))).thenAnswer(inv -> {
                PedidoProveedor p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });
            when(detalleRepo.findByPedidoId(1L)).thenReturn(List.of());

            PedidoProveedorResponse resp = pedidoService.crear(request);

            assertThat(resp.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
            verify(detalleRepo).save(any(DetallePedidoProveedor.class));
        }
    }

    @Nested @DisplayName("Consultas")
    class Consultas {

        @Test @DisplayName("Lista todos los pedidos")
        void listaTodos() {
            when(pedidoRepo.findAll()).thenReturn(List.of(pedidoPendiente));
            when(detalleRepo.findByPedidoId(1L)).thenReturn(List.of());
            assertThat(pedidoService.listarTodos()).hasSize(1);
        }

        @Test @DisplayName("Obtiene un pedido por ID")
        void obtienePorId() {
            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(detalleRepo.findByPedidoId(1L)).thenReturn(List.of());
            assertThat(pedidoService.obtenerPorId(1L).getId()).isEqualTo(1L);
        }

        @Test @DisplayName("Lanza excepción si el pedido no existe")
        void pedidoNoExiste() {
            when(pedidoRepo.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> pedidoService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested @DisplayName("actualizarEstado()")
    class ActualizarEstado {

        @Test @DisplayName("Cambia el estado de PENDIENTE a ENVIADO")
        void cambiaEstado() {
            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(pedidoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(detalleRepo.findByPedidoId(1L)).thenReturn(List.of());

            PedidoProveedorResponse resp = pedidoService.actualizarEstado(
                    1L, new ActualizarEstadoPedidoRequest(EstadoPedido.ENVIADO));

            assertThat(resp.getEstado()).isEqualTo(EstadoPedido.ENVIADO);
        }

        @Test @DisplayName("Lanza excepción si el pedido ya fue recibido")
        void rechazaSiYaRecibido() {
            pedidoPendiente.setEstado(EstadoPedido.RECIBIDO);
            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            assertThatThrownBy(() -> pedidoService.actualizarEstado(
                    1L, new ActualizarEstadoPedidoRequest(EstadoPedido.CANCELADO)))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(pedidoRepo, never()).save(any());
        }
    }

    @Nested @DisplayName("recibir()")
    class Recibir {

        @Test @DisplayName("Recibe el pedido, actualiza stock existente y registra ENTRADA")
        void recibePedidoConStockExistente() {
            Stock stockActual = Stock.builder().productoId(7L).sucursalId(2L)
                    .cantidadDisponible(10).cantidadReservada(0).build();
            DetallePedidoProveedor detalle = DetallePedidoProveedor.builder()
                    .id(50L).pedidoId(1L).productoId(7L).cantidadSolicitada(20).cantidadRecibida(0).build();
            RecepcionDetalleItem item = new RecepcionDetalleItem(50L, 18);

            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(detalleRepo.findById(50L)).thenReturn(Optional.of(detalle));
            when(stockRepo.findByProductoIdAndSucursalId(7L, 2L)).thenReturn(Optional.of(stockActual));
            when(pedidoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(detalleRepo.findByPedidoId(1L)).thenReturn(List.of(detalle));

            pedidoService.recibir(1L, new RecibirPedidoRequest(List.of(item)));

            assertThat(stockActual.getCantidadDisponible()).isEqualTo(28); // 10 + 18
            ArgumentCaptor<MovimientoInventario> movCap = ArgumentCaptor.forClass(MovimientoInventario.class);
            verify(movimientoRepo).save(movCap.capture());
            assertThat(movCap.getValue().getTipoMovimiento()).isEqualTo(TipoMovimiento.ENTRADA);
        }

        @Test @DisplayName("Crea una fila de stock nueva si no existía antes")
        void creaStockNuevoAlRecibir() {
            DetallePedidoProveedor detalle = DetallePedidoProveedor.builder()
                    .id(50L).pedidoId(1L).productoId(7L).cantidadSolicitada(20).cantidadRecibida(0).build();
            RecepcionDetalleItem item = new RecepcionDetalleItem(50L, 15);

            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(detalleRepo.findById(50L)).thenReturn(Optional.of(detalle));
            when(stockRepo.findByProductoIdAndSucursalId(7L, 2L)).thenReturn(Optional.empty()); // No existía
            when(pedidoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(detalleRepo.findByPedidoId(1L)).thenReturn(List.of(detalle));

            pedidoService.recibir(1L, new RecibirPedidoRequest(List.of(item)));

            ArgumentCaptor<Stock> stockCap = ArgumentCaptor.forClass(Stock.class);
            verify(stockRepo).save(stockCap.capture());
            assertThat(stockCap.getValue().getCantidadDisponible()).isEqualTo(15);
        }

        @Test @DisplayName("Lanza excepción si el pedido ya está RECIBIDO")
        void rechazaSiYaRecibido() {
            pedidoPendiente.setEstado(EstadoPedido.RECIBIDO);
            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            assertThatThrownBy(() -> pedidoService.recibir(1L, new RecibirPedidoRequest(List.of())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("recibido");
        }

        @Test @DisplayName("Lanza excepción si el pedido está CANCELADO")
        void rechazaSiCancelado() {
            pedidoPendiente.setEstado(EstadoPedido.CANCELADO);
            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            assertThatThrownBy(() -> pedidoService.recibir(1L, new RecibirPedidoRequest(List.of())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cancelado");
        }

        @Test @DisplayName("Lanza excepción si el detalle no existe")
        void detalleNoExiste() {
            when(pedidoRepo.findById(1L)).thenReturn(Optional.of(pedidoPendiente));
            when(detalleRepo.findById(999L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> pedidoService.recibir(
                    1L, new RecibirPedidoRequest(List.of(new RecepcionDetalleItem(999L, 5)))))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
