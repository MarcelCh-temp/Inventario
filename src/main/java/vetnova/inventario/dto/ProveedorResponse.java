package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorResponse {
    private Long id;
    private String nombre;
    private String rut;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;
}
