package vetnova.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequest {

    @NotNull
    private Long productoId;

    @NotNull
    @Positive
    private Integer cantidadSolicitada;

    private BigDecimal precioUnitario;
}
