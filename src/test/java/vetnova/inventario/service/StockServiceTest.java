package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.ItemVerificacionStock;
import vetnova.inventario.dto.StockResponse;
import vetnova.inventario.dto.VerificarStockRequest;
import vetnova.inventario.dto.VerificarStockResponse;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.Stock;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.repository.ProductoRepository;
import vetnova.inventario.repository.StockRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService - pruebas unitarias con 100% de cobertura")
class StockServiceTest {

    @Mock private StockRepository stockRepository;
    @Mock private ProductoRepository productoRepository;
    @InjectMocks private StockService stockService;

    private Producto alimento;
    private Stock stockS1;

    @BeforeEach
    void setUp() {
        alimento = Producto.builder()
                .id(10L).nombre("Alimento Premium 15kg")
                .categoria(CategoriaProducto.ALIMENTO).tipoUso(TipoUso.VENTA)
                .precio(new BigDecimal("45000")).stockMinimo(5).activo(true).build();

        stockS1 = Stock.builder()
                .id(100L).productoId(10L).sucursalId(1L)
                .cantidadDisponible(20).cantidadReservada(5).build();
    }

    @Nested @DisplayName("Consultas básicas")
    class Consultas {

        @Test @DisplayName("Lista todo el stock")
        void listarTodo() {
            when(stockRepository.findAll()).thenReturn(List.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimento));
            assertThat(stockService.listarTodo()).hasSize(1);
        }

        @Test @DisplayName("Lista stock de una sucursal")
        void listarPorSucursal() {
            when(stockRepository.findBySucursalId(1L)).thenReturn(List.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimento));
            List<StockResponse> res = stockService.listarPorSucursal(1L);
            assertThat(res.get(0).getNombreProducto()).isEqualTo("Alimento Premium 15kg");
        }

        @Test @DisplayName("Lista stock de un producto (producto no encontrado → nombre null)")
        void listarPorProductoNombreNull() {
            when(stockRepository.findByProductoId(10L)).thenReturn(List.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.empty()); // cubre rama orElse(null)
            List<StockResponse> res = stockService.listarPorProducto(10L);
            assertThat(res.get(0).getNombreProducto()).isNull();
        }
    }

    @Nested @DisplayName("listarBajoMinimo()")
    class BajoMinimo {

        @Test @DisplayName("Incluye el stock que está bajo el mínimo")
        void incluyeBajoMinimo() {
            Stock stockBajo = Stock.builder().productoId(10L).sucursalId(2L)
                    .cantidadDisponible(2).cantidadReservada(0).build();
            when(stockRepository.findAll()).thenReturn(List.of(stockBajo));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimento));
            assertThat(stockService.listarBajoMinimo()).hasSize(1);
        }

        @Test @DisplayName("Excluye stock que supera el mínimo")
        void excluyeSobreMinimo() {
            // cantidadDisponible=20 > stockMinimo=5 → no debe aparecer
            when(stockRepository.findAll()).thenReturn(List.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimento));
            assertThat(stockService.listarBajoMinimo()).isEmpty();
        }

        @Test @DisplayName("Excluye cuando stockMinimo es null")
        void excluyeCuandoMinimoEsNull() {
            alimento.setStockMinimo(null); // cubre la rama: stockMinimo != null → false
            when(stockRepository.findAll()).thenReturn(List.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimento));
            assertThat(stockService.listarBajoMinimo()).isEmpty();
        }

        @Test @DisplayName("Excluye cuando el producto no existe en el repositorio (orElse false)")
        void excluyeCuandoProductoNoExiste() {
            when(stockRepository.findAll()).thenReturn(List.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.empty()); // cubre orElse(false)
            assertThat(stockService.listarBajoMinimo()).isEmpty();
        }
    }

    @Nested @DisplayName("verificarDisponibilidad()")
    class VerificarDisponibilidad {

        @Test @DisplayName("Retorna disponible cuando el stock libre es suficiente")
        void disponible() {
            // disponible real = 20 - 5 = 15, se piden 10
            ItemVerificacionStock item = new ItemVerificacionStock(10L, 10);
            when(stockRepository.findByProductoIdAndSucursalId(10L, 1L)).thenReturn(Optional.of(stockS1));

            VerificarStockResponse resp = stockService.verificarDisponibilidad(
                    new VerificarStockRequest(1L, List.of(item)));

            assertThat(resp.isDisponible()).isTrue();
            assertThat(resp.getProductosSinStock()).isEmpty();
        }

        @Test @DisplayName("Retorna no disponible cuando no alcanza el stock")
        void noDisponible() {
            ItemVerificacionStock item = new ItemVerificacionStock(10L, 50);
            when(stockRepository.findByProductoIdAndSucursalId(10L, 1L)).thenReturn(Optional.of(stockS1));
            when(productoRepository.findById(10L)).thenReturn(Optional.of(alimento));

            VerificarStockResponse resp = stockService.verificarDisponibilidad(
                    new VerificarStockRequest(1L, List.of(item)));

            assertThat(resp.isDisponible()).isFalse();
            assertThat(resp.getProductosSinStock()).containsExactly("Alimento Premium 15kg");
        }

        @Test @DisplayName("Sin fila de stock → disponible real = 0 → nombre del producto desconocido")
        void sinFilaDeStockProductoDesconocido() {
            // stock null → disponible = 0; producto no encontrado → "Producto id X"
            ItemVerificacionStock item = new ItemVerificacionStock(99L, 1);
            when(stockRepository.findByProductoIdAndSucursalId(99L, 1L)).thenReturn(Optional.empty());
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());

            VerificarStockResponse resp = stockService.verificarDisponibilidad(
                    new VerificarStockRequest(1L, List.of(item)));

            assertThat(resp.isDisponible()).isFalse();
            assertThat(resp.getProductosSinStock()).containsExactly("Producto id 99");
        }

        @Test @DisplayName("Múltiples ítems: reporta solo los que no tienen stock")
        void multiplesItemsMixtos() {
            Stock stockVacuna = Stock.builder().productoId(20L).sucursalId(1L)
                    .cantidadDisponible(1).cantidadReservada(0).build();
            Producto vacuna = Producto.builder().id(20L).nombre("Vacuna Triple").build();

            when(stockRepository.findByProductoIdAndSucursalId(10L, 1L)).thenReturn(Optional.of(stockS1));
            when(stockRepository.findByProductoIdAndSucursalId(20L, 1L)).thenReturn(Optional.of(stockVacuna));
            when(productoRepository.findById(20L)).thenReturn(Optional.of(vacuna));

            VerificarStockResponse resp = stockService.verificarDisponibilidad(
                    new VerificarStockRequest(1L, List.of(
                            new ItemVerificacionStock(10L, 5),   // OK: 20-5=15 ≥ 5
                            new ItemVerificacionStock(20L, 5)    // Falla: 1 < 5
                    )));

            assertThat(resp.isDisponible()).isFalse();
            assertThat(resp.getProductosSinStock()).containsExactly("Vacuna Triple");
        }
    }
}
