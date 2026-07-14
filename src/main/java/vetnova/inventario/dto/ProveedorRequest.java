package vetnova.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String rut;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
}
