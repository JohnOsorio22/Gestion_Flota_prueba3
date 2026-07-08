package cl.duoc.Gestion_de_Flota.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FlotaException.class)
    public ResponseEntity<ErrorResponse> manejarFlotaException(FlotaException ex, HttpServletRequest request) {
        String traceId = generarTraceId();
        log.warn("[{}] Regla de negocio violada en {} {}: {}",
                traceId, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request, traceId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = generarTraceId();
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[{}] Error de validación en {} {}: {}",
                traceId, request.getMethod(), request.getRequestURI(), errores);
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Errores de validación: " + errores, request, traceId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarException(Exception ex, HttpServletRequest request) {
        String traceId = generarTraceId();
        log.error("[{}] Error interno no controlado en {} {}",
                traceId, request.getMethod(), request.getRequestURI(), ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + ex.getMessage(), request, traceId);
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(HttpStatus status, String mensaje,
                                                             HttpServletRequest request, String traceId) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                traceId
        );
        return ResponseEntity.status(status).body(body);
    }

    private String generarTraceId() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        return traceId;
    }
}