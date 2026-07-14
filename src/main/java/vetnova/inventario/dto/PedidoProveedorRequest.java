package vetnova.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoProveedorRequest {

    @NotNull
    private Long proveedorId;

    private Long sucursalId;

    private Long usuarioId;

    private String observaciones;

    @NotEmpty(message = "El pedido debe incluir al menos un producto")
    private List<@Valid DetallePedidoRequest> detalles;
}
