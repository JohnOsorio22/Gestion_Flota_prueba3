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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "Gestión del parque vehicular de FastTrack Courier")
public class VehiculoController {

    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar un nuevo vehículo",
            description = "Agrega un vehículo a la flota. La patente debe ser única y con formato válido.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehículo registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = Vehiculo.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, formato de patente incorrecto o patente duplicada")
    })
    @PostMapping
    public ResponseEntity<Vehiculo> crear(@Valid @RequestBody Vehiculo vehiculo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(vehiculo));
    }

    @Operation(summary = "Listar todos los vehículos activos",
            description = "Retorna el listado completo de vehículos activos (excluye eliminados).")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos activos")
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Listar vehículos disponibles para asignación",
            description = "Retorna los vehículos activos y con disponible=true.")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<Vehiculo>> listarDisponibles() {
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
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar datos de un vehículo",
            description = "Actualiza parcialmente los campos del vehículo. Solo se modifican los campos enviados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Patente duplicada, formato inválido u otros errores de validación"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizar(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long id,
            @RequestBody Vehiculo vehiculo) {
        return service.actualizar(id, vehiculo)
                .map(ResponseEntity::ok)
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
        return service.cambiarDisponibilidad(id, disponible)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar vehículo (Soft Delete)",
            description = "Marca el vehículo como inactivo y no disponible. El registro permanece en la BD.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo eliminado lógicamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long id) {
        if (service.eliminar(id)) {
            return ResponseEntity.ok("Vehículo eliminado (soft delete). ID: " + id);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Listar vehículos por marca",
            description = "Retorna los vehículos activos que coincidan con la marca (sin distinguir mayúsculas/minúsculas).")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos de la marca indicada")
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Vehiculo>> listarPorMarca(
            @Parameter(description = "Marca del vehículo", example = "Toyota")
            @PathVariable String marca) {
        return ResponseEntity.ok(service.listarPorMarca(marca));
    }

    @Operation(summary = "Contar vehículos disponibles",
            description = "Retorna la cantidad total de vehículos activos y disponibles.")
    @ApiResponse(responseCode = "200", description = "Cantidad de vehículos disponibles")
    @GetMapping("/disponibles/count")
    public ResponseEntity<Long> contarDisponibles() {
        return ResponseEntity.ok(service.contarDisponibles());
    }
}