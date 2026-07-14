package vetnova.inventario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vetnova.inventario.dto.ActualizarEstadoPedidoRequest;
import vetnova.inventario.dto.PedidoProveedorRequest;
import vetnova.inventario.dto.PedidoProveedorResponse;
import vetnova.inventario.dto.RecibirPedidoRequest;
import vetnova.inventario.service.PedidoProveedorService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos a Proveedores", description = "Reposición de stock solicitada a proveedores")
public class PedidoProveedorController {

    private final PedidoProveedorService pedidoProveedorService;

    @PostMapping
    @Operation(summary = "Crear pedido de reposición")
    public ResponseEntity<PedidoProveedorResponse> crear(@Valid @RequestBody PedidoProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoProveedorService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos a proveedores")
    public ResponseEntity<List<PedidoProveedorResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoProveedorService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    public ResponseEntity<PedidoProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoProveedorService.obtenerPorId(id));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Por ejemplo, marcar como ENVIADO o CANCELADO")
    public ResponseEntity<PedidoProveedorResponse> actualizarEstado(@PathVariable Long id,
                                                                       @Valid @RequestBody ActualizarEstadoPedidoRequest request) {
        return ResponseEntity.ok(pedidoProveedorService.actualizarEstado(id, request));
    }

    @PostMapping("/{id}/recibir")
    @Operation(summary = "Recibir mercadería de un pedido",
            description = "Registra la cantidad recibida por producto y genera automáticamente los movimientos "
                    + "ENTRADA correspondientes, actualizando el stock real de la sucursal")
    public ResponseEntity<PedidoProveedorResponse> recibir(@PathVariable Long id,
                                                              @Valid @RequestBody RecibirPedidoRequest request) {
        return ResponseEntity.ok(pedidoProveedorService.recibir(id, request));
    }
}
