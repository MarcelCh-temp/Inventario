package vetnova.inventario.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.inventario.dto.ProductoRequest;
import vetnova.inventario.dto.ProductoResponse;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.service.ProductoService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoController - pruebas unitarias")
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    private ProductoController controller;

    private ProductoResponse productoResponse;

    @BeforeEach
    void setUp() {
        controller = new ProductoController(productoService);
        productoResponse = ProductoResponse.builder().id(1L).nombre("Vacuna Antirrábica")
                .categoria(CategoriaProducto.MEDICAMENTO).tipoUso(TipoUso.USO_CLINICO)
                .precio(new BigDecimal("12000")).activo(true).build();
    }

    @Test
    @DisplayName("crear() delega en el servicio y responde 201 CREATED")
    void crearDelegaYResponde201() {
        ProductoRequest request = new ProductoRequest("Vacuna Antirrábica", "desc", CategoriaProducto.MEDICAMENTO,
                TipoUso.USO_CLINICO, new BigDecimal("12000"), "SKU-1", "dosis", 10);
        when(productoService.crear(request)).thenReturn(productoResponse);

        ResponseEntity<ProductoResponse> response = controller.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(productoResponse);
    }

    @Test
    @DisplayName("listarTodos() responde 200 con la lista del servicio")
    void listarTodosResponde200() {
        when(productoService.listarTodos()).thenReturn(List.of(productoResponse));

        ResponseEntity<List<ProductoResponse>> response = controller.listarTodos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId() delega correctamente")
    void obtenerPorIdDelega() {
        when(productoService.obtenerPorId(1L)).thenReturn(productoResponse);

        ResponseEntity<ProductoResponse> response = controller.obtenerPorId(1L);

        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(productoService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("listarPorCategoria() delega con el filtro correcto")
    void listarPorCategoriaDelega() {
        when(productoService.listarPorCategoria(CategoriaProducto.MEDICAMENTO)).thenReturn(List.of(productoResponse));

        ResponseEntity<List<ProductoResponse>> response = controller.listarPorCategoria(CategoriaProducto.MEDICAMENTO);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("listarPorTipoUso() delega con el filtro correcto")
    void listarPorTipoUsoDelega() {
        when(productoService.listarPorTipoUso(TipoUso.USO_CLINICO)).thenReturn(List.of(productoResponse));

        ResponseEntity<List<ProductoResponse>> response = controller.listarPorTipoUso(TipoUso.USO_CLINICO);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("actualizar() delega en el servicio")
    void actualizarDelega() {
        ProductoRequest request = new ProductoRequest("Vacuna Antirrábica", "desc", CategoriaProducto.MEDICAMENTO,
                TipoUso.USO_CLINICO, new BigDecimal("13000"), "SKU-1", "dosis", 10);
        when(productoService.actualizar(1L, request)).thenReturn(productoResponse);

        ResponseEntity<ProductoResponse> response = controller.actualizar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("desactivar() delega en el servicio")
    void desactivarDelega() {
        when(productoService.desactivar(1L)).thenReturn(productoResponse);

        ResponseEntity<ProductoResponse> response = controller.desactivar(1L);

        verify(productoService).desactivar(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
