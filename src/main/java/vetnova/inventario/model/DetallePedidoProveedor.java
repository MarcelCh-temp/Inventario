package vetnova.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedidoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidadSolicitada;

    @Builder.Default
    private Integer cantidadRecibida = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;
}
