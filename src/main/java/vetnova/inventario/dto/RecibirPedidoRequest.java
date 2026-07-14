package vetnova.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecibirPedidoRequest {

    @NotEmpty(message = "Debe indicar la cantidad recibida de al menos un producto")
    private List<@Valid RecepcionDetalleItem> items;
}
