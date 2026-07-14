package vetnova.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.inventario.dto.MovimientoRequest;
import vetnova.inventario.dto.MovimientoResponse;
import vetnova.inventario.service.MovimientoInventarioService;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Inventario", description = "Entradas, salidas, traslados y ajustes de stock")
public class MovimientoController {

    private final MovimientoInventarioService movimientoService;

    @PostMapping
    @Operation(summary = "Registrar movimiento de inventario",
            description = "Endpoint consumido por el microservicio de Ventas al confirmar el pago de una venta "
                    + "(genera un movimiento SALIDA). También lo usa Bodega para registrar entradas y ajustes.")
    public ResponseEntity<MovimientoResponse> registrar(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse response = movimientoService.registrarMovimiento(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos (historial completo)")
    public ResponseEntity<List<MovimientoResponse>> listarTodos() {
        return ResponseEntity.ok(movimientoService.listarTodos());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Historial de movimientos de un producto")
    public ResponseEntity<List<MovimientoResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(movimientoService.listarPorProducto(productoId));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @Operation(summary = "Historial de movimientos de una sucursal")
    public ResponseEntity<List<MovimientoResponse>> listarPorSucursal(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(movimientoService.listarPorSucursal(sucursalId));
    }
}
