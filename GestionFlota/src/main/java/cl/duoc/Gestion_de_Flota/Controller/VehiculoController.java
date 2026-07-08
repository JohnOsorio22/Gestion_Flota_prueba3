package cl.duoc.Gestion_de_Flota.Controller;

import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import cl.duoc.Gestion_de_Flota.Service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "Gestión del parque vehicular de FastTrack Courier")
public class VehiculoController {

    private static final Logger logger = LoggerFactory.getLogger(VehiculoController.class);
    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar un nuevo vehículo",
               description = "Agrega un vehículo a la flota. La patente debe ser única.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehículo registrado exitosamente",
                     content = @Content(schema = @Schema(implementation = Vehiculo.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o patente duplicada"),
        @ApiResponse(responseCode = "422", description = "Validación de negocio fallida")
    })
    @PostMapping
    public ResponseEntity<Vehiculo> crear(@Valid @RequestBody Vehiculo vehiculo) {
        logger.info("Solicitud de creación de vehículo con patente: {}", vehiculo.getPatente());
        Vehiculo nuevo = service.crear(vehiculo);
        logger.info("Vehículo creado exitosamente con ID: {}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @Operation(summary = "Listar todos los vehículos activos",
               description = "Retorna el listado completo de vehículos activos (excluye eliminados).")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos activos")
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listar() {
        logger.debug("Listando todos los vehículos activos");
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Listar vehículos disponibles para asignación",
               description = "Retorna los vehículos activos y con disponible=true.")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<Vehiculo>> listarDisponibles() {
        logger.debug("Listando vehículos disponibles");
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @Operation(summary = "Obtener un vehículo por ID",
               description = "Busca un vehículo activo por su ID. Retorna 404 si no existe o fue eliminado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> buscarPorId(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long id) {
        logger.debug("Buscando vehículo por ID: {}", id);
        return service.buscarPorId(id)
                .map(vehiculo -> {
                    logger.debug("Vehículo encontrado: {}", vehiculo.getPatente());
                    return ResponseEntity.ok(vehiculo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar datos de un vehículo",
               description = "Actualiza parcialmente los campos del vehículo. Solo se modifican los campos enviados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehículo actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Patente duplicada u otros errores de validación"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizar(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Vehiculo vehiculo) {
        logger.info("Solicitud de actualización de vehículo ID: {}", id);
        return service.actualizar(id, vehiculo)
                .map(v -> {
                    logger.info("Vehículo actualizado: {}", v.getPatente());
                    return ResponseEntity.ok(v);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cambiar disponibilidad de un vehículo",
               description = "Marca el vehículo como disponible (true) o no disponible (false).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<Vehiculo> cambiarDisponibilidad(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado de disponibilidad", example = "false")
            @RequestParam boolean disponible) {
        logger.info("Cambiando disponibilidad del vehículo ID {} a: {}", id, disponible);
        return service.cambiarDisponibilidad(id, disponible)
                .map(v -> {
                    logger.info("Disponibilidad actualizada para vehículo ID: {}", id);
                    return ResponseEntity.ok(v);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar vehículo (Soft Delete)",
               description = "Marca el vehículo como inactivo y no disponible. El registro permanece en la BD.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehículo eliminado lógicamente"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long id) {
        logger.info("Solicitud de eliminación de vehículo ID: {}", id);
        if (service.eliminar(id)) {
            logger.info("Vehículo eliminado (soft delete): {}", id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Vehículo eliminado (soft delete). ID: " + id);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        }
        logger.warn("Intento de eliminar vehículo inexistente: {}", id);
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Contar vehículos disponibles",
               description = "Retorna la cantidad de vehículos activos y disponibles para asignación.")
    @ApiResponse(responseCode = "200", description = "Cantidad de vehículos disponibles")
    @GetMapping("/count/disponibles")
    public ResponseEntity<Map<String, Long>> contarDisponibles() {
        long count = service.contarDisponibles();
        Map<String, Long> response = new HashMap<>();
        response.put("disponibles", count);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar vehículos por marca",
               description = "Retorna todos los vehículos activos de una marca específica.")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos de la marca")
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Vehiculo>> listarPorMarca(
            @Parameter(description = "Marca del vehículo", example = "Toyota")
            @PathVariable String marca) {
        logger.debug("Listando vehículos de marca: {}", marca);
        return ResponseEntity.ok(service.listarPorMarca(marca));
    }
}
