package vetnova.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaProducto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUso tipoUso;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(unique = true, length = 50)
    private String codigoSku;

    @Column(length = 20)
    private String unidadMedida;

    private Integer stockMinimo;

    @Builder.Default
    private Boolean activo = true;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activo == null) this.activo = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
