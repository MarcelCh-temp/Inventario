package vetnova.inventario.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vetnova.inventario.dto.ProveedorRequest;
import vetnova.inventario.dto.ProveedorResponse;
import vetnova.inventario.service.ProveedorService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProveedorController - pruebas unitarias")
class ProveedorControllerTest {

    @Mock
    private ProveedorService proveedorService;

    private ProveedorController controller;

    private ProveedorResponse proveedorResponse;

    @BeforeEach
    void setUp() {
        controller = new ProveedorController(proveedorService);
        proveedorResponse = ProveedorResponse.builder().id(1L).nombre("VetMed Suministros").activo(true).build();
    }

    @Test
    @DisplayName("crear() responde 201 CREATED")
    void crearResponde201() {
        ProveedorRequest request = new ProveedorRequest("VetMed Suministros", "76123456-7", "Marcela", "+569", "a@b.cl", "dir");
        when(proveedorService.crear(request)).thenReturn(proveedorResponse);

        ResponseEntity<ProveedorResponse> response = controller.crear(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("listarTodos() responde 200")
    void listarTodos() {
        when(proveedorService.listarTodos()).thenReturn(List.of(proveedorResponse));

        ResponseEntity<List<ProveedorResponse>> response = controller.listarTodos();

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId() delega en el servicio")
    void obtenerPorId() {
        when(proveedorService.obtenerPorId(1L)).thenReturn(proveedorResponse);

        controller.obtenerPorId(1L);

        verify(proveedorService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("actualizar() delega en el servicio")
    void actualizar() {
        ProveedorRequest request = new ProveedorRequest("VetMed SpA", "76123456-7", "Marcela", "+569", "a@b.cl", "dir");
        when(proveedorService.actualizar(1L, request)).thenReturn(proveedorResponse);

        ResponseEntity<ProveedorResponse> response = controller.actualizar(1L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("desactivar() delega en el servicio")
    void desactivar() {
        when(proveedorService.desactivar(1L)).thenReturn(proveedorResponse);

        controller.desactivar(1L);

        verify(proveedorService).desactivar(1L);
    }
}
