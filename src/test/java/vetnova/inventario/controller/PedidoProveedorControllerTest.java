package vetnova.inventario.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.inventario.dto.*;
import vetnova.inventario.model.EstadoPedido;
import vetnova.inventario.service.PedidoProveedorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoProveedorController - pruebas unitarias")
class PedidoProveedorControllerTest {

    @Mock
    private PedidoProveedorService pedidoProveedorService;

    private PedidoProveedorController controller;

    private PedidoProveedorResponse pedidoResponse;

    @BeforeEach
    void setUp() {
        controller = new PedidoProveedorController(pedidoProveedorService);
        pedidoResponse = PedidoProveedorResponse.builder().id(1L).estado(EstadoPedido.PENDIENTE).build();
    }

    @Test
    @DisplayName("crear() responde 201 CREATED")
    void crearResponde201() {
        DetallePedidoRequest detalle = new DetallePedidoRequest(7L, 10, null);
        PedidoProveedorRequest request = new PedidoProveedorRequest(1L, 2L, 5L, "obs", List.of(detalle));
        when(pedidoProveedorService.crear(request)).thenReturn(pedidoResponse);

        ResponseEntity<PedidoProveedorResponse> response = controller.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("listarTodos() responde 200")
    void listarTodos() {
        when(pedidoProveedorService.listarTodos()).thenReturn(List.of(pedidoResponse));

        ResponseEntity<List<PedidoProveedorResponse>> response = controller.listarTodos();

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId() delega en el servicio")
    void obtenerPorId() {
        when(pedidoProveedorService.obtenerPorId(1L)).thenReturn(pedidoResponse);

        controller.obtenerPorId(1L);

        verify(pedidoProveedorService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("actualizarEstado() delega en el servicio")
    void actualizarEstado() {
        ActualizarEstadoPedidoRequest request = new ActualizarEstadoPedidoRequest(EstadoPedido.ENVIADO);
        when(pedidoProveedorService.actualizarEstado(1L, request)).thenReturn(pedidoResponse);

        ResponseEntity<PedidoProveedorResponse> response = controller.actualizarEstado(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("recibir() delega en el servicio")
    void recibir() {
        RecibirPedidoRequest request = new RecibirPedidoRequest(List.of(new RecepcionDetalleItem(1L, 5)));
        when(pedidoProveedorService.recibir(1L, request)).thenReturn(pedidoResponse);

        controller.recibir(1L, request);

        verify(pedidoProveedorService).recibir(1L, request);
    }
}
