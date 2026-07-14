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
import vetnova.inventario.client.AuthClient;
import vetnova.inventario.dto.MovimientoRequest;
import vetnova.inventario.dto.MovimientoResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.exception.StockInsuficienteException;
import vetnova.inventario.exception.TokenInvalidoException;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.Stock;
import vetnova.inventario.model.TipoMovimiento;
import vetnova.inventario.repository.MovimientoInventarioRepository;
import vetnova.inventario.repository.ProductoRepository;
import vetnova.inventario.repository.StockRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoInventarioService - pruebas unitarias con 100% de cobertura")
class MovimientoInventarioServiceTest {

    @Mock private MovimientoInventarioRepository movimientoRepository;
    @Mock private StockRepository stockRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private AuthClient authClient;

    @InjectMocks private MovimientoInventarioService movimientoService;

    private Producto jeringas;
    private Stock stockExistente;

    @BeforeEach
    void setUp() {
        jeringas = Producto.builder().id(5L).nombre("Jeringas 5ml").build();
        stockExistente = Stock.builder().id(1L).productoId(5L).sucursalId(2L)
                .cantidadDisponible(30).cantidadReservada(0).build();
    }

    @Nested @DisplayName("Validación de token")
    class Token {

        @Test @DisplayName("Continúa si el token es válido")
        void continuaConTokenValido() {
            MovimientoRequest req = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null);
            when(authClient.tokenEsValido("tok-ok")).thenReturn(true);
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(req, "tok-ok");

            verify(authClient).tokenEsValido("tok-ok");
        }

        @Test @DisplayName("Lanza excepción si el token es inválido")
        void lanzaExcepcionTokenInvalido() {
            when(authClient.tokenEsValido("tok-bad")).thenReturn(false);
            assertThatThrownBy(() -> movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null), "tok-bad"))
                    .isInstanceOf(TokenInvalidoException.class);
            verify(productoRepository, never()).findById(any());
        }

        @Test @DisplayName("Omite validación cuando el token es null")
        void omiteValidacionSiTokenNull() {
            MovimientoRequest req = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null);
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(req, null);

            verify(authClient, never()).tokenEsValido(any());
        }

        @Test @DisplayName("Omite validación cuando el token es vacío")
        void omiteValidacionSiTokenVacio() {
            MovimientoRequest req = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null);
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(req, "   ");

            verify(authClient, never()).tokenEsValido(any());
        }
    }

    @Nested @DisplayName("Tipos de movimiento")
    class TiposMovimiento {

        @Test @DisplayName("ENTRADA incrementa el stock")
        void entrada() {
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 10, "Reposición", 1L, null), null);

            assertThat(stockExistente.getCantidadDisponible()).isEqualTo(40);
        }

        @Test @DisplayName("TRASLADO incrementa el stock (misma rama que ENTRADA)")
        void traslado() {
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 2L, TipoMovimiento.TRASLADO, 5, "Traslado", 1L, null), null);

            assertThat(stockExistente.getCantidadDisponible()).isEqualTo(35);
        }

        @Test @DisplayName("AJUSTE incrementa el stock (misma rama que ENTRADA)")
        void ajuste() {
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 2L, TipoMovimiento.AJUSTE, 3, "Ajuste inv.", 1L, null), null);

            assertThat(stockExistente.getCantidadDisponible()).isEqualTo(33);
        }

        @Test @DisplayName("SALIDA descuenta el stock cuando hay suficiente")
        void salidaConStockSuficiente() {
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 2L, TipoMovimiento.SALIDA, 10, "Venta", 1L, "VENTA-1"), null);

            assertThat(stockExistente.getCantidadDisponible()).isEqualTo(20);
        }

        @Test @DisplayName("SALIDA rechaza si el stock es insuficiente")
        void salidaStockInsuficiente() {
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 2L)).thenReturn(Optional.of(stockExistente));

            assertThatThrownBy(() -> movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 2L, TipoMovimiento.SALIDA, 999, null, 1L, null), null))
                    .isInstanceOf(StockInsuficienteException.class);
            verify(movimientoRepository, never()).save(any());
        }
    }

    @Nested @DisplayName("Gestión de Stock")
    class GestionStock {

        @Test @DisplayName("Crea una fila de stock nueva si no existía para esa sucursal")
        void creaStockNuevo() {
            when(productoRepository.findById(5L)).thenReturn(Optional.of(jeringas));
            when(stockRepository.findByProductoIdAndSucursalId(5L, 9L)).thenReturn(Optional.empty());
            when(movimientoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Stock> cap = ArgumentCaptor.forClass(Stock.class);
            movimientoService.registrarMovimiento(
                    new MovimientoRequest(5L, 9L, TipoMovimiento.ENTRADA, 15, null, 1L, null), null);

            verify(stockRepository).save(cap.capture());
            assertThat(cap.getValue().getCantidadDisponible()).isEqualTo(15);
        }

        @Test @DisplayName("Lanza excepción si el producto no existe")
        void productoNoExiste() {
            when(productoRepository.findById(404L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> movimientoService.registrarMovimiento(
                    new MovimientoRequest(404L, 2L, TipoMovimiento.ENTRADA, 5, null, 1L, null), null))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested @DisplayName("Listados")
    class Listados {

        @Test @DisplayName("Lista todos los movimientos")
        void listarTodos() {
            when(movimientoRepository.findAll()).thenReturn(List.of());
            assertThat(movimientoService.listarTodos()).isEmpty();
        }

        @Test @DisplayName("Lista movimientos por producto")
        void listarPorProducto() {
            when(movimientoRepository.findByProductoIdOrderByFechaMovimientoDesc(5L)).thenReturn(List.of());
            assertThat(movimientoService.listarPorProducto(5L)).isEmpty();
        }

        @Test @DisplayName("Lista movimientos por sucursal")
        void listarPorSucursal() {
            when(movimientoRepository.findBySucursalIdOrderByFechaMovimientoDesc(2L)).thenReturn(List.of());
            assertThat(movimientoService.listarPorSucursal(2L)).isEmpty();
        }
    }
}
