import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.;
import jakarta.validation.constraints.;
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
    @Pattern(regexp = "^[A-Z0-9]{6,8}$", 
             message = "La patente solo debe contener letras mayúsculas y números (ej: ABCD12)")
    @Schema(description = "Patente única del vehículo", example = "ABCD12")
    private String patente;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "La marca es obligatoria")
    @Size(min = 2, max = 50, message = "La marca debe tener entre 2 y 50 caracteres")
    @Schema(description = "Marca del vehículo", example = "Toyota")
    private String marca;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El modelo es obligatorio")
    @Size(min = 2, max = 50, message = "El modelo debe tener entre 2 y 50 caracteres")
    @Schema(description = "Modelo del vehículo", example = "Hilux")
    private String modelo;

    @Column(nullable = false)
    @NotNull(message = "La disponibilidad es obligatoria")
    @Schema(description = "Indica si el vehículo está disponible para asignación", 
            example = "true")
    private Boolean disponible = true;

    @Column(nullable = false)
    @NotNull(message = "El estado activo es obligatorio")
    @Schema(description = "Indica si el vehículo está activo (soft delete)", 
            example = "true")
    private Boolean activo = true;
}
