package vetnova.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.TipoUso;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La categoria es obligatoria")
    private CategoriaProducto categoria;

    @NotNull(message = "El tipo de uso es obligatorio")
    private TipoUso tipoUso;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    private String codigoSku;

    private String unidadMedida;

    private Integer stockMinimo;
}
