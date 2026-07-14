package vetnova.inventario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vetnova.inventario.dto.ProveedorRequest;
import vetnova.inventario.dto.ProveedorResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.model.Proveedor;
import vetnova.inventario.repository.ProveedorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        Proveedor proveedor = Proveedor.builder()
                .nombre(request.getNombre())
                .rut(request.getRut())
                .contacto(request.getContacto())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .activo(true)
                .build();

        return mapearResponse(proveedorRepository.save(proveedor));
    }

    public List<ProveedorResponse> listarTodos() {
        return proveedorRepository.findAll().stream().map(this::mapearResponse).toList();
    }

    public ProveedorResponse obtenerPorId(Long id) {
        return mapearResponse(buscarPorId(id));
    }

    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setNombre(request.getNombre());
        proveedor.setRut(request.getRut());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());
        return mapearResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorResponse desactivar(Long id) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.setActivo(false);
        return mapearResponse(proveedorRepository.save(proveedor));
    }

    private Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el proveedor con id: " + id));
    }

    private ProveedorResponse mapearResponse(Proveedor proveedor) {
        return ProveedorResponse.builder()
                .id(proveedor.getId())
                .nombre(proveedor.getNombre())
                .rut(proveedor.getRut())
                .contacto(proveedor.getContacto())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .activo(proveedor.getActivo())
                .build();
    }
}
