package vetnova.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Body que envia el microservicio de Ventas para validar disponibilidad antes de aceptar una compra. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificarStockRequest {

    @NotNull
    private Long sucursalId;

    @NotEmpty(message = "Debe incluir al menos un item a verificar")
    private List<@Valid ItemVerificacionStock> items;
}
