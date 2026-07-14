package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.ProductoRequest;
import vetnova.inventario.dto.ProductoResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.exception.SkuDuplicadoException;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.Producto;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.repository.ProductoRepository;

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
@DisplayName("ProductoService - pruebas unitarias con 100% de cobertura")
class ProductoServiceTest {

    @Mock private ProductoRepository productoRepository;
    @InjectMocks private ProductoService productoService;

    private Producto vacuna;
    private ProductoRequest requestConSku;
    private ProductoRequest requestSinSku;

    @BeforeEach
    void setUp() {
        vacuna = Producto.builder()
                .id(1L).nombre("Vacuna Antirrábica").categoria(CategoriaProducto.MEDICAMENTO)
                .tipoUso(TipoUso.USO_CLINICO).precio(new BigDecimal("12000"))
                .codigoSku("VAC-001").stockMinimo(10).activo(true).build();

        requestConSku = new ProductoRequest("Vacuna Antirrábica", "desc",
                CategoriaProducto.MEDICAMENTO, TipoUso.USO_CLINICO, new BigDecimal("12000"),
                "VAC-001", "dosis", 10);

        requestSinSku = new ProductoRequest("Producto sin SKU", "desc",
                CategoriaProducto.ACCESORIO, TipoUso.VENTA, new BigDecimal("5000"),
                null, "unidad", 5);
    }

    @Nested @DisplayName("crear()")
    class Crear {

        @Test @DisplayName("Crea producto con SKU cuando no está duplicado")
        void creaConSkuNuevo() {
            when(productoRepository.existsByCodigoSku("VAC-001")).thenReturn(false);
            when(productoRepository.save(any())).thenReturn(vacuna);

            ProductoResponse resp = productoService.crear(requestConSku);

            assertThat(resp.getCodigoSku()).isEqualTo("VAC-001");
        }

        @Test @DisplayName("Crea producto sin SKU sin verificar duplicado")
        void creaSinSkuSinVerificar() {
            // SKU null → no debe llamar existsByCodigoSku
            Producto sinSku = Producto.builder().id(2L).nombre("Producto sin SKU")
                    .categoria(CategoriaProducto.ACCESORIO).tipoUso(TipoUso.VENTA)
                    .precio(new BigDecimal("5000")).activo(true).build();
            when(productoRepository.save(any())).thenReturn(sinSku);

            productoService.crear(requestSinSku);

            verify(productoRepository, never()).existsByCodigoSku(any());
        }

        @Test @DisplayName("Lanza excepción si el SKU ya existe")
        void lanzaExcepcionSkuDuplicado() {
            when(productoRepository.existsByCodigoSku("VAC-001")).thenReturn(true);
            assertThatThrownBy(() -> productoService.crear(requestConSku))
                    .isInstanceOf(SkuDuplicadoException.class);
            verify(productoRepository, never()).save(any());
        }
    }

    @Nested @DisplayName("Consultas")
    class Consultas {

        @Test @DisplayName("Lista todos los productos")
        void listarTodos() {
            when(productoRepository.findAll()).thenReturn(List.of(vacuna));
            assertThat(productoService.listarTodos()).hasSize(1);
        }

        @Test @DisplayName("Obtiene producto existente por ID")
        void obtienePorId() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacuna));
            assertThat(productoService.obtenerPorId(1L).getNombre()).isEqualTo("Vacuna Antirrábica");
        }

        @Test @DisplayName("Lanza excepción si el producto no existe")
        void noExiste() {
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test @DisplayName("Lista por categoría")
        void listaPorCategoria() {
            when(productoRepository.findByCategoria(CategoriaProducto.MEDICAMENTO)).thenReturn(List.of(vacuna));
            assertThat(productoService.listarPorCategoria(CategoriaProducto.MEDICAMENTO)).hasSize(1);
        }

        @Test @DisplayName("Lista por tipo de uso")
        void listaPorTipoUso() {
            when(productoRepository.findByTipoUso(TipoUso.USO_CLINICO)).thenReturn(List.of(vacuna));
            assertThat(productoService.listarPorTipoUso(TipoUso.USO_CLINICO)).hasSize(1);
        }
    }

    @Nested @DisplayName("actualizar()")
    class Actualizar {

        @Test @DisplayName("Actualiza sin cambiar SKU (no verifica duplicado)")
        void actualizaMismoSku() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacuna));
            when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            productoService.actualizar(1L, requestConSku); // mismo SKU

            verify(productoRepository, never()).existsByCodigoSku(any());
        }

        @Test @DisplayName("Actualiza con SKU null (no verifica duplicado)")
        void actualizaConSkuNull() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacuna));
            when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            productoService.actualizar(1L, requestSinSku); // SKU null

            verify(productoRepository, never()).existsByCodigoSku(any());
        }

        @Test @DisplayName("Actualiza con SKU distinto que no está en uso")
        void actualizaSkuDistintoLibre() {
            ProductoRequest nuevoSku = new ProductoRequest("Vacuna", "desc",
                    CategoriaProducto.MEDICAMENTO, TipoUso.USO_CLINICO, new BigDecimal("12000"),
                    "NUEVO-SKU", "dosis", 10);
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacuna));
            when(productoRepository.existsByCodigoSku("NUEVO-SKU")).thenReturn(false);
            when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ProductoResponse resp = productoService.actualizar(1L, nuevoSku);

            assertThat(resp.getCodigoSku()).isEqualTo("NUEVO-SKU");
        }

        @Test @DisplayName("Lanza excepción si el nuevo SKU ya pertenece a otro producto")
        void lanzaExcepcionSkuDuplicado() {
            ProductoRequest skuConflicto = new ProductoRequest("Vacuna", "desc",
                    CategoriaProducto.MEDICAMENTO, TipoUso.USO_CLINICO, new BigDecimal("12000"),
                    "OTRO-SKU", "dosis", 10);
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacuna));
            when(productoRepository.existsByCodigoSku("OTRO-SKU")).thenReturn(true);

            assertThatThrownBy(() -> productoService.actualizar(1L, skuConflicto))
                    .isInstanceOf(SkuDuplicadoException.class);
            verify(productoRepository, never()).save(any());
        }
    }

    @Nested @DisplayName("desactivar()")
    class Desactivar {

        @Test @DisplayName("Desactiva el producto (soft delete)")
        void desactiva() {
            when(productoRepository.findById(1L)).thenReturn(Optional.of(vacuna));
            when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            assertThat(productoService.desactivar(1L).getActivo()).isFalse();
        }
    }
}
