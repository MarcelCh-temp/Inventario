package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Long sucursalId;
    private Integer cantidadDisponible;
    private Integer cantidadReservada;
    private LocalDateTime ultimaActualizacion;
}
