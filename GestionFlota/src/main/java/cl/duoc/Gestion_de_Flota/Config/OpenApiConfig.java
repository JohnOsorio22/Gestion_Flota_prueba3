package cl.duoc.Gestion_de_Flota.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flotaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FastTrack Courier – API de Gestión de Flota")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión del parque vehicular de FastTrack Courier.
                                
                                **Características principales:**
                                - CRUD completo de vehículos con soft delete
                                - Control de disponibilidad de vehículos
                                - Integración B2B: documentación técnica estándar OpenAPI 3.0
                                - Consumido por el microservicio de Rastreo Logístico (puerto 28000)
                                """)
                        .contact(new Contact()
                                .name("Equipo FastTrack – DUOC UC")
                                .email("soporte@fasttrack.cl"))
                        .license(new License()
                                .name("Uso interno FastTrack Courier")))
                .servers(List.of(
                        new Server().url("http://localhost:16000").description("Servidor local"),
                        new Server().url("https://flota.fasttrack.cl").description("Producción")
                ));
    }
}
