package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vetnova.inventario.model.CategoriaProducto;
import vetnova.inventario.model.TipoUso;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaProducto categoria;
    private TipoUso tipoUso;
    private BigDecimal precio;
    private String codigoSku;
    private String unidadMedida;
    private Integer stockMinimo;
    private Boolean activo;
}
