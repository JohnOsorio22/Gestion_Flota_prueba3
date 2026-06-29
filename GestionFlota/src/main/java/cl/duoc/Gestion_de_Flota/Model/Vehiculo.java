package cl.duoc.Gestion_de_Flota.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehiculos")
@Schema(description = "Entidad que representa un vehículo de la flota")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID autogenerado del vehículo", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    @NotBlank(message = "La patente es obligatoria")
    @Size(min = 6, max = 8, message = "La patente debe tener entre 6 y 8 caracteres")
    @Schema(description = "Patente única del vehículo", example = "ABCD12")
    private String patente;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "La marca es obligatoria")
    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String marca;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El modelo es obligatorio")
    @Schema(description = "Modelo del vehículo", example = "Hilux")
    private String modelo;

    @Column(nullable = false)
    @Schema(description = "Indica si el vehículo está disponible para asignación", example = "true")
    private Boolean disponible = true;

    @Column(nullable = false)
    @Schema(description = "Indica si el vehículo está activo (soft delete)", example = "true")
    private Boolean activo = true;
}
