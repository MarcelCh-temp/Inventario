package vetnova.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proveedor_id", nullable = false)
    private Long proveedorId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    /** Sucursal que solicita el pedido (referencia externa). */
    private Long sucursalId;

    /** Usuario de bodega que generó el pedido (referencia externa a Autenticacion). */
    private Long usuarioId;

    @Column(length = 500)
    private String observaciones;

    private LocalDateTime fechaPedido;
    private LocalDateTime fechaRecepcion;

    @PrePersist
    public void prePersist() {
        this.fechaPedido = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoPedido.PENDIENTE;
    }
}
