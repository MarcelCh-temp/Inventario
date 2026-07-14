package vetnova.inventario.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.inventario.dto.MovimientoRequest;
import vetnova.inventario.dto.MovimientoResponse;
import vetnova.inventario.model.TipoMovimiento;
import vetnova.inventario.service.MovimientoInventarioService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoController - pruebas unitarias")
class MovimientoControllerTest {

    @Mock
    private MovimientoInventarioService movimientoService;

    private MovimientoController controller;

    @BeforeEach
    void setUp() {
        controller = new MovimientoController(movimientoService);
    }

    @Test
    @DisplayName("registrar() pasa el header Authorization como token y responde 201")
    void registrarPasaTokenYResponde201() {
        MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.ENTRADA, 10, "motivo", 1L, null);
        MovimientoResponse esperado = MovimientoResponse.builder().id(1L).tipoMovimiento(TipoMovimiento.ENTRADA).build();
        when(movimientoService.registrarMovimiento(request, "token-123")).thenReturn(esperado);

        ResponseEntity<MovimientoResponse> response = controller.registrar("token-123", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(movimientoService).registrarMovimiento(request, "token-123");
    }

    @Test
    @DisplayName("registrar() funciona también sin header Authorization")
    void registrarSinToken() {
        MovimientoRequest request = new MovimientoRequest(5L, 2L, TipoMovimiento.SALIDA, 3, null, 1L, null);
        MovimientoResponse esperado = MovimientoResponse.builder().id(2L).tipoMovimiento(TipoMovimiento.SALIDA).build();
        when(movimientoService.registrarMovimiento(request, null)).thenReturn(esperado);

        ResponseEntity<MovimientoResponse> response = controller.registrar(null, request);

        assertThat(response.getBody().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("listarTodos() responde con el historial completo")
    void listarTodos() {
        when(movimientoService.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<MovimientoResponse>> response = controller.listarTodos();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("listarPorProducto() delega con el id correcto")
    void listarPorProducto() {
        when(movimientoService.listarPorProducto(5L)).thenReturn(List.of());

        controller.listarPorProducto(5L);

        verify(movimientoService).listarPorProducto(5L);
    }

    @Test
    @DisplayName("listarPorSucursal() delega con el id correcto")
    void listarPorSucursal() {
        when(movimientoService.listarPorSucursal(2L)).thenReturn(List.of());

        controller.listarPorSucursal(2L);

        verify(movimientoService).listarPorSucursal(2L);
    }
}
