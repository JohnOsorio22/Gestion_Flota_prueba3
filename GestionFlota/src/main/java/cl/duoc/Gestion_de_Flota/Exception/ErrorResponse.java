package cl.duoc.Gestion_de_Flota.Exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Estructura estándar de error retornada por la API")
public record ErrorResponse(

        @Schema(description = "Fecha y hora en que ocurrió el error")
        LocalDateTime timestamp,

        @Schema(description = "Código de estado HTTP", example = "400")
        int status,

        @Schema(description = "Nombre del error HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensaje descriptivo del error", example = "Ya existe un vehículo con patente: ABCD12")
        String message,

        @Schema(description = "Ruta del recurso donde ocurrió el error", example = "/api/vehiculos")
        String path,

        @Schema(description = "Identificador único del error para trazabilidad en logs", example = "a1b2c3d4")
        String traceId
) {
}