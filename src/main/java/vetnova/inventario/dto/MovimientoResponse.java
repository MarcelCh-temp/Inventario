package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.inventario.model.TipoMovimiento;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponse {
    private Long id;
    private Long productoId;
    private Long sucursalId;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private String motivo;
    private Long usuarioId;
    private String referenciaDocumento;
    private LocalDateTime fechaMovimiento;
}
