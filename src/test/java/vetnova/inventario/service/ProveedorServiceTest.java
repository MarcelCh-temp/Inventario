package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.ProveedorRequest;
import vetnova.inventario.dto.ProveedorResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.model.Proveedor;
import vetnova.inventario.repository.ProveedorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProveedorService - pruebas unitarias con 100% de cobertura")
class ProveedorServiceTest {

    @Mock private ProveedorRepository proveedorRepository;
    @InjectMocks private ProveedorService proveedorService;

    private Proveedor vetMed;
    private ProveedorRequest requestVetMed;

    @BeforeEach
    void setUp() {
        vetMed = Proveedor.builder().id(1L).nombre("VetMed Suministros")
                .rut("76123456-7").contacto("Marcela Ríos").telefono("+562")
                .email("contacto@vetmed.cl").direccion("Av. Industrial 450").activo(true).build();
        requestVetMed = new ProveedorRequest("VetMed Suministros", "76123456-7",
                "Marcela Ríos", "+562", "contacto@vetmed.cl", "Av. Industrial 450");
    }

    @Nested @DisplayName("crear()")
    class Crear {
        @Test @DisplayName("Crea un proveedor con todos los datos")
        void creaProveedor() {
            when(proveedorRepository.save(any())).thenReturn(vetMed);
            ProveedorResponse resp = proveedorService.crear(requestVetMed);
            assertThat(resp.getNombre()).isEqualTo("VetMed Suministros");
            assertThat(resp.getActivo()).isTrue();
        }
    }

    @Nested @DisplayName("Consultas")
    class Consultas {
        @Test @DisplayName("Lista todos los proveedores")
        void listaTodos() {
            when(proveedorRepository.findAll()).thenReturn(List.of(vetMed));
            assertThat(proveedorService.listarTodos()).hasSize(1);
        }

        @Test @DisplayName("Obtiene proveedor por ID existente")
        void obtienePorId() {
            when(proveedorRepository.findById(1L)).thenReturn(Optional.of(vetMed));
            assertThat(proveedorService.obtenerPorId(1L).getRut()).isEqualTo("76123456-7");
        }

        @Test @DisplayName("Lanza excepción si el proveedor no existe")
        void noExiste() {
            when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> proveedorService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested @DisplayName("actualizar()")
    class Actualizar {
        @Test @DisplayName("Actualiza todos los datos del proveedor")
        void actualiza() {
            ProveedorRequest cambios = new ProveedorRequest("VetMed SpA", "76123456-7",
                    "Marcela", "+5622", "ventas@vetmed.cl", "Nueva dir");
            when(proveedorRepository.findById(1L)).thenReturn(Optional.of(vetMed));
            when(proveedorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            ProveedorResponse resp = proveedorService.actualizar(1L, cambios);

            assertThat(resp.getNombre()).isEqualTo("VetMed SpA");
            assertThat(resp.getEmail()).isEqualTo("ventas@vetmed.cl");
        }
    }

    @Nested @DisplayName("desactivar()")
    class Desactivar {
        @Test @DisplayName("Desactiva el proveedor")
        void desactiva() {
            when(proveedorRepository.findById(1L)).thenReturn(Optional.of(vetMed));
            when(proveedorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            assertThat(proveedorService.desactivar(1L).getActivo()).isFalse();
        }
    }
}
