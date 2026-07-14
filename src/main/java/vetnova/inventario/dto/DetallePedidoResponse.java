package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoResponse {
    private Long id;
    private Long productoId;
    private Integer cantidadSolicitada;
    private Integer cantidadRecibida;
    private BigDecimal precioUnitario;
}
