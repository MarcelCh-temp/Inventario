package vetnova.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(length = 12)
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @Column(length = 100)
    @NotBlank(message = "El contacto es obligatorio")
    private String contacto;

    @Column(length = 20)
    @NotNull(message = "El teléfono es obligatorio")
    private String telefono;

    @Column(length = 150)
    @Email(message = "El email no tiene el formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Column(length = 255)
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Builder.Default
    private Boolean activo = true;

    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) this.activo = true;
    }
}
