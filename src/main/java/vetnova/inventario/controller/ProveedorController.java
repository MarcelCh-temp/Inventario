package vetnova.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.inventario.dto.ProveedorRequest;
import vetnova.inventario.dto.ProveedorResponse;
import vetnova.inventario.service.ProveedorService;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de proveedores de medicamentos e insumos")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    @Operation(summary = "Crear proveedor")
    public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar proveedores")
    public ResponseEntity<List<ProveedorResponse>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    public ResponseEntity<ProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor")
    public ResponseEntity<ProveedorResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar proveedor")
    public ResponseEntity<ProveedorResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.desactivar(id));
    }
}
