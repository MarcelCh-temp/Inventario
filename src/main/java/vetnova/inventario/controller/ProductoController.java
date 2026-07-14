package vetnova.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.inventario.dto.ProductoRequest;
import vetnova.inventario.dto.ProductoResponse;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.TipoUso;
import vetnova.inventario.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo de medicamentos, alimentos, insumos clínicos y accesorios")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar productos")
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar productos por categoría", description = "MEDICAMENTO, ALIMENTO, INSUMO_CLINICO o ACCESORIO")
    public ResponseEntity<List<ProductoResponse>> listarPorCategoria(@PathVariable CategoriaProducto categoria) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
    }

    @GetMapping("/tipo/{tipoUso}")
    @Operation(summary = "Listar productos por tipo de uso", description = "VENTA, USO_CLINICO o AMBOS")
    public ResponseEntity<List<ProductoResponse>> listarPorTipoUso(@PathVariable TipoUso tipoUso) {
        return ResponseEntity.ok(productoService.listarPorTipoUso(tipoUso));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar producto (soft delete)")
    public ResponseEntity<ProductoResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.desactivar(id));
    }
}
