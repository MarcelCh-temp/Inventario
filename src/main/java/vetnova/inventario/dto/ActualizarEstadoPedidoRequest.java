package vetnova.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.inventario.model.EstadoPedido;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoPedidoRequest {

    @NotNull
    private EstadoPedido estado;
}
