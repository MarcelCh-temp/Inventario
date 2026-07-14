package vetnova.inventario.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import vetnova.inventario.dto.ValidacionTokenDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthClient (Inventario) - pruebas unitarias con 100% de cobertura")
class AuthClientTest {

    @Mock private RestTemplate restTemplate;
    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClient(restTemplate, "http://localhost:8081");
    }

    @Test @DisplayName("Devuelve true cuando Autenticación confirma el token")
    void devuelveTrue() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(new ValidacionTokenDTO(true, 1L));
        assertThat(authClient.tokenEsValido("tok")).isTrue();
    }

    @Test @DisplayName("Devuelve false cuando Autenticación responde valido=false")
    void devuelveFalseCuandoInvalido() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(new ValidacionTokenDTO(false, null));
        assertThat(authClient.tokenEsValido("tok-vencido")).isFalse();
    }

    @Test @DisplayName("Devuelve false cuando la respuesta es null (respuesta != null → false)")
    void devuelveFalseCuandoRespuestaNula() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);
        assertThat(authClient.tokenEsValido("tok")).isFalse();
    }

    @Test @DisplayName("Devuelve false (fail-safe) si Autenticación no responde")
    void devuelveFalseSiFalla() {
        when(restTemplate.getForObject(anyString(), any())).thenThrow(new ResourceAccessException("timeout"));
        assertThat(authClient.tokenEsValido("tok")).isFalse();
    }

    @Test @DisplayName("Devuelve false sin llamar al servicio si el token es null, vacío o solo espacios")
    void devuelveFalseSiTokenVacio() {
        assertThat(authClient.tokenEsValido(null)).isFalse();
        assertThat(authClient.tokenEsValido("")).isFalse();
        assertThat(authClient.tokenEsValido("   ")).isFalse();
        verify(restTemplate, never()).getForObject(anyString(), any());
    }
}
