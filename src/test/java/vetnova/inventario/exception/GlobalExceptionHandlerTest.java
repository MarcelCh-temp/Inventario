package vetnova.inventario.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler (Inventario) - pruebas unitarias")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("ResourceNotFoundException se traduce a 404 NOT_FOUND")
    void manejaResourceNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("No se encontró el producto"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMensaje()).contains("producto");
    }

    @Test
    @DisplayName("StockInsuficienteException se traduce a 409 CONFLICT")
    void manejaStockInsuficiente() {
        ResponseEntity<ErrorResponse> response = handler.handleStockInsuficiente(
                new StockInsuficienteException("No hay stock"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("SkuDuplicadoException se traduce a 409 CONFLICT")
    void manejaSkuDuplicado() {
        ResponseEntity<ErrorResponse> response = handler.handleSkuDuplicado(
                new SkuDuplicadoException("SKU repetido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("TokenInvalidoException se traduce a 401 UNAUTHORIZED")
    void manejaTokenInvalido() {
        ResponseEntity<ErrorResponse> response = handler.handleTokenInvalido(
                new TokenInvalidoException("Token vencido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("MethodArgumentNotValidException se traduce a 400 con el detalle de cada campo")
    void manejaErroresDeValidacion() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("productoRequest", "nombre", "El nombre es obligatorio");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidacion(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetalles()).containsExactly("El nombre es obligatorio");
    }

    @Test
    @DisplayName("IllegalArgumentException se traduce a 400 BAD_REQUEST")
    void manejaIllegalArgument() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Argumento inválido"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Cualquier otra excepción se traduce a 500 INTERNAL_SERVER_ERROR")
    void manejaExcepcionGeneral() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMensaje()).contains("boom");
    }
}
