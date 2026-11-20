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
 * Every API error response has the same shape: {"error": "message in Spanish"}.
 * This way the frontend can always show the user a clear reason.
 */
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> status(ResponseStatusException e) {
        String reason = e.getReason() != null ? e.getReason() : "No se pudo completar la solicitud.";
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", reason));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
        var field = e.getBindingResult().getFieldError();
        String message = field == null ? "Los datos enviados no son válidos."
                : "El campo «" + label(field.getField()) + "» " + translate(field.getCode());
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> invalidJson(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> invalidType(MethodArgumentTypeMismatchException e) {
        return error(HttpStatus.BAD_REQUEST, "El valor «" + e.getValue() + "» no es válido para " + e.getName() + ".");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> method(HttpRequestMethodNotSupportedException e) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "Esta ruta no acepta " + e.getMethod() + ".");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(NoResourceFoundException e) {
        return error(HttpStatus.NOT_FOUND, "La ruta solicitada no existe.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> unexpected(Exception e) {
        // Other Spring MVC exceptions (415, 406, missing header…) already carry their HTTP status.
        if (e instanceof ErrorResponse er && er.getStatusCode().is4xxClientError())
            return error(er.getStatusCode(), "La solicitud no es válida.");
        log.error("Unhandled error", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado. Intenta de nuevo en un momento.");
    }

    /** Spanish name of a request field, as the user sees it in the form. */
    private static String label(String field) {
        return switch (field) {
            case "name" -> "nombre";
            case "email" -> "correo";
            case "question" -> "pregunta";
            case "fingerprint" -> "contraseña";
            default -> field;
        };
    }

    /** Spanish message for each validation rule (shown to the user). */
    private static String translate(String code) {
        if (code == null) return "no es válido.";
        return switch (code) {
            case "NotBlank", "NotNull", "NotEmpty" -> "es obligatorio.";
            case "Size" -> "es demasiado largo.";
            case "Email" -> "debe ser un correo válido.";
            default -> "no es válido.";
        };
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
