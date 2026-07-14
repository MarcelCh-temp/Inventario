package vetnova.inventario.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vetnova.inventario.dto.ValidacionTokenDTO;

/**
 * Cliente REST hacia el microservicio de Autenticacion (puerto 8081).
 * Lo usa Inventario para validar que el usuario que ejecuta un movimiento de bodega
 * (entrada, salida, traslado, ajuste) realmente existe y su sesion es valida.
 */
@Component
@Slf4j
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String authBaseUrl;

    public AuthClient(RestTemplate restTemplate, @Value("${vetnova.auth.url}") String authBaseUrl) {
        this.restTemplate = restTemplate;
        this.authBaseUrl = authBaseUrl;
    }

    /**
     * Consulta a Autenticacion si un token de sesion sigue siendo valido.
     * Si el servicio de Autenticacion no responde, se asume token invalido (fail-safe)
     * en lugar de tumbar la operacion de bodega con una excepcion no controlada.
     */
    public boolean tokenEsValido(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            String url = UriComponentsBuilder.fromUriString(authBaseUrl + "/api/auth/validar")
                    .queryParam("token", token)
                    .toUriString();
            ValidacionTokenDTO respuesta = restTemplate.getForObject(url, ValidacionTokenDTO.class);
            return respuesta != null && respuesta.isValido();
        } catch (RestClientException ex) {
            log.warn("No fue posible validar el token contra el microservicio de Autenticacion: {}", ex.getMessage());
            return false;
        }
    }
}
