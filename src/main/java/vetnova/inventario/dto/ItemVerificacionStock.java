package vetnova.inventario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representa un (producto, cantidad) dentro de una solicitud de verificacion de stock. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVerificacionStock {

    @NotNull
    private Long productoId;

    @NotNull
    @Positive
    private Integer cantidad;
}
