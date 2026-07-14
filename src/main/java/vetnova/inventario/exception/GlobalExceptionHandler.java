package vetnova.inventario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(SkuDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleSkuDuplicado(SkuDuplicadoException ex) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleTokenInvalido(TokenInvalidoException ex) {
        return construir(HttpStatus.UNAUTHORIZED, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .toList();
        return construir(HttpStatus.BAD_REQUEST, "Error de validación en los datos enviados", detalles);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado: " + ex.getMessage(), null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje, List<String> detalles) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .mensaje(mensaje)
                .detalles(detalles)
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
