package vetnova.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "sucursal_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    /** Referencia externa al microservicio de Agendamiento y Sucursales. */
    @Column(name = "sucursal_id", nullable = false)
    private Long sucursalId;

    @Builder.Default
    @Column(nullable = false)
    private Integer cantidadDisponible = 0;

    /** Unidades reservadas por carritos web aun no pagados; no se descuentan del stock real. */
    @Builder.Default
    @Column(nullable = false)
    private Integer cantidadReservada = 0;

    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    public void actualizarFecha() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
