package vetnova.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.inventario.model.TipoMovimiento;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequest {

    @NotNull
    private Long productoId;

    private Long sucursalId;

    @NotNull
    private TipoMovimiento tipoMovimiento;

    @NotNull
    @Positive
    private Integer cantidad;

    private String motivo;

    private Long usuarioId;

    private String referenciaDocumento;
}
