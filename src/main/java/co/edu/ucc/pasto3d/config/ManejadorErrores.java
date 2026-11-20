package co.edu.ucc.pasto3d.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

/**
 * Todas las respuestas de error de la API tienen la misma forma: {"error": "mensaje en español"}.
 * Así el frontend siempre puede mostrarle al usuario un motivo claro.
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> estado(ResponseStatusException e) {
        String motivo = e.getReason() != null ? e.getReason() : "No se pudo completar la solicitud.";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", motivo));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e) {
        var campo = e.getBindingResult().getFieldError();
        String mensaje = campo == null ? "Los datos enviados no son válidos."
                : "El campo «" + campo.getField() + "» " + traducir(campo.getCode());
        return error(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> tipoInvalido(MethodArgumentTypeMismatchException e) {
        return error(HttpStatus.BAD_REQUEST, "El valor «" + e.getValue() + "» no es válido para " + e.getName() + ".");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> metodo(HttpRequestMethodNotSupportedException e) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "Esta ruta no acepta " + e.getMethod() + ".");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> noExiste(NoResourceFoundException e) {
        return error(HttpStatus.NOT_FOUND, "La ruta solicitada no existe.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> inesperado(Exception e) {
        // Otras excepciones propias de Spring MVC (415, 406, cabecera faltante…) ya traen su código HTTP.
        if (e instanceof ErrorResponse er && er.getStatusCode().is4xxClientError())
            return error(er.getStatusCode(), "La solicitud no es válida.");
        log.error("Error no controlado", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado. Intenta de nuevo en un momento.");
    }

    private static String traducir(String codigo) {
        if (codigo == null) return "no es válido.";
        return switch (codigo) {
            case "NotBlank", "NotNull", "NotEmpty" -> "es obligatorio.";
            case "Size" -> "es demasiado largo.";
            case "Email" -> "debe ser un correo válido.";
            default -> "no es válido.";
        };
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatusCode estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("error", mensaje));
    }
}
