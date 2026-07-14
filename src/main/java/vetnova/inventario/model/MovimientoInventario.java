package vetnova.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "sucursal_id")
    private Long sucursalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 255)
    private String motivo;

    /** Referencia externa al usuario (microservicio de Autenticacion) que ejecuto el movimiento. */
    private Long usuarioId;

    @Column(length = 100)
    private String referenciaDocumento;

    private LocalDateTime fechaMovimiento;

    @PrePersist
    public void prePersist() {
        this.fechaMovimiento = LocalDateTime.now();
    }
}
