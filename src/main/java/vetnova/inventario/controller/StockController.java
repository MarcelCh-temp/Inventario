package vetnova.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.inventario.dto.StockResponse;
import vetnova.inventario.dto.VerificarStockRequest;
import vetnova.inventario.dto.VerificarStockResponse;
import vetnova.inventario.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Disponibilidad de productos por sucursal")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Listar todo el stock")
    public ResponseEntity<List<StockResponse>> listarTodo() {
        return ResponseEntity.ok(stockService.listarTodo());
    }

    @GetMapping("/sucursal/{sucursalId}")
    @Operation(summary = "Listar stock de una sucursal")
    public ResponseEntity<List<StockResponse>> listarPorSucursal(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(stockService.listarPorSucursal(sucursalId));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar stock de un producto en todas las sucursales")
    public ResponseEntity<List<StockResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(stockService.listarPorProducto(productoId));
    }

    @GetMapping("/bajo-minimo")
    @Operation(summary = "Listar productos bajo el stock mínimo", description = "Alerta para Bodega y Administrador de Sucursal")
    public ResponseEntity<List<StockResponse>> listarBajoMinimo() {
        return ResponseEntity.ok(stockService.listarBajoMinimo());
    }

    @PostMapping("/verificar-disponibilidad")
    @Operation(summary = "Verificar disponibilidad de stock",
            description = "Endpoint consumido por el microservicio de Ventas antes de aceptar un producto en el "
                    + "carrito o confirmar una compra")
    public ResponseEntity<VerificarStockResponse> verificarDisponibilidad(
            @Valid @RequestBody VerificarStockRequest request) {
        return ResponseEntity.ok(stockService.verificarDisponibilidad(request));
    }
}
