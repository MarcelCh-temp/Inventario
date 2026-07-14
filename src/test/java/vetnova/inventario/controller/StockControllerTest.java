package vetnova.inventario.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import vetnova.inventario.dto.ItemVerificacionStock;
import vetnova.inventario.dto.StockResponse;
import vetnova.inventario.dto.VerificarStockRequest;
import vetnova.inventario.dto.VerificarStockResponse;
import vetnova.inventario.service.StockService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockController - pruebas unitarias")
class StockControllerTest {

    @Mock
    private StockService stockService;

    private StockController controller;

    @BeforeEach
    void setUp() {
        controller = new StockController(stockService);
    }

    @Test
    @DisplayName("listarTodo() responde con la lista del servicio")
    void listarTodo() {
        StockResponse stock = StockResponse.builder().id(1L).productoId(10L).sucursalId(1L)
                .cantidadDisponible(20).cantidadReservada(0).build();
        when(stockService.listarTodo()).thenReturn(List.of(stock));

        ResponseEntity<List<StockResponse>> response = controller.listarTodo();

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("listarPorSucursal() delega con el id correcto")
    void listarPorSucursal() {
        when(stockService.listarPorSucursal(5L)).thenReturn(List.of());

        ResponseEntity<List<StockResponse>> response = controller.listarPorSucursal(5L);

        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("listarPorProducto() delega con el id correcto")
    void listarPorProducto() {
        when(stockService.listarPorProducto(10L)).thenReturn(List.of());

        ResponseEntity<List<StockResponse>> response = controller.listarPorProducto(10L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("listarBajoMinimo() responde con las alertas del servicio")
    void listarBajoMinimo() {
        when(stockService.listarBajoMinimo()).thenReturn(List.of());

        ResponseEntity<List<StockResponse>> response = controller.listarBajoMinimo();

        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("verificarDisponibilidad() delega el request al servicio")
    void verificarDisponibilidad() {
        VerificarStockRequest request = new VerificarStockRequest(1L, List.of(new ItemVerificacionStock(10L, 5)));
        VerificarStockResponse esperado = VerificarStockResponse.builder().disponible(true).productosSinStock(List.of()).build();
        when(stockService.verificarDisponibilidad(request)).thenReturn(esperado);

        ResponseEntity<VerificarStockResponse> response = controller.verificarDisponibilidad(request);

        assertThat(response.getBody().isDisponible()).isTrue();
    }
}
