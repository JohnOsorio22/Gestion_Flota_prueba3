package cl.duoc.Gestion_de_Flota.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health Check", description = "Verificación de estado del microservicio")
public class HealthController {

    @Operation(summary = "Verificar estado del servicio",
            description = "Retorna el estado actual del microservicio de Gestión de Flota")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Gestión de Flota");
        status.put("version", "1.0.0");
        status.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        status.put("port", 16000);
        status.put("database", "Oracle (prod) / H2 (test)");

        return ResponseEntity.ok(status);
    }
}