package vetnova.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Espejo de la respuesta que entrega GET /api/auth/validar en el microservicio de Autenticacion. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionTokenDTO {
    private boolean valido;
    private Long usuarioId;
}
