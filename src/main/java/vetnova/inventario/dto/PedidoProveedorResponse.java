package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.inventario.model.EstadoPedido;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoProveedorResponse {
    private Long id;
    private Long proveedorId;
    private EstadoPedido estado;
    private Long sucursalId;
    private Long usuarioId;
    private String observaciones;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaRecepcion;
    private List<DetallePedidoResponse> detalles;
}
