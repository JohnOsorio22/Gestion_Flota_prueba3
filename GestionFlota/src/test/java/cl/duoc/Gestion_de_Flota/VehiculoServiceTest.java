package cl.duoc.Gestion_de_Flota;

import cl.duoc.Gestion_de_Flota.Exception.FlotaException;
import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import cl.duoc.Gestion_de_Flota.Repository.VehiculoRepository;
import cl.duoc.Gestion_de_Flota.Service.VehiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository repository;

    @InjectMocks
    private VehiculoService service;

    private Vehiculo vehiculoBase;

    @BeforeEach
    void setUp() {
        vehiculoBase = new Vehiculo();
        vehiculoBase.setId(1L);
        vehiculoBase.setPatente("ABCD12");
        vehiculoBase.setMarca("Toyota");
        vehiculoBase.setModelo("Hilux");
        vehiculoBase.setDisponible(true);
        vehiculoBase.setActivo(true);
    }



    @Test
    @DisplayName("crear() – debe guardar vehículo con disponible=true y activo=true")
    void crear_debeGuardarConEstadoCorrecto() {
        when(repository.existsByPatente("ABCD12")).thenReturn(false);
        when(repository.save(any(Vehiculo.class))).thenReturn(vehiculoBase);

        Vehiculo resultado = service.crear(vehiculoBase);

        assertThat(resultado.getDisponible()).isTrue();
        assertThat(resultado.getActivo()).isTrue();
        verify(repository).save(vehiculoBase);
    }

    @Test
    @DisplayName("crear() – debe lanzar excepción si la patente ya existe")
    void crear_debeLanzarExcepcionConPatenteDuplicada() {
        when(repository.existsByPatente("ABCD12")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(vehiculoBase))
                .isInstanceOf(FlotaException.class)
                .hasMessageContaining("ABCD12");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("listar() – debe retornar solo vehículos activos")
    void listar_debeRetornarSoloActivos() {
        when(repository.findByActivoTrue()).thenReturn(List.of(vehiculoBase));

        List<Vehiculo> resultado = service.listar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    @Test
    @DisplayName("listarDisponibles() – debe retornar solo vehículos activos y disponibles")
    void listarDisponibles_debeRetornarSoloDisponibles() {
        when(repository.findByActivoTrueAndDisponibleTrue()).thenReturn(List.of(vehiculoBase));

        List<Vehiculo> resultado = service.listarDisponibles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getDisponible()).isTrue();
    }


    @Test
    @DisplayName("buscarPorId() – debe retornar el vehículo si existe y está activo")
    void buscarPorId_debeRetornarVehiculo() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(vehiculoBase));

        Optional<Vehiculo> resultado = service.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("buscarPorId() – debe retornar vacío si no existe")
    void buscarPorId_debeRetornarVacioSiNoExiste() {
        when(repository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        Optional<Vehiculo> resultado = service.buscarPorId(99L);

        assertThat(resultado).isEmpty();
    }


    @Test
    @DisplayName("actualizar() – debe actualizar campos y retornar vehículo modificado")
    void actualizar_debeActualizarCampos() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(vehiculoBase));
        when(repository.save(any(Vehiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        Vehiculo cambios = new Vehiculo();
        cambios.setModelo("Land Cruiser");

        Optional<Vehiculo> resultado = service.actualizar(1L, cambios);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getModelo()).isEqualTo("Land Cruiser");
    }

    @Test
    @DisplayName("actualizar() – debe retornar vacío si el vehículo no existe")
    void actualizar_debeRetornarVacioSiNoExiste() {
        when(repository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        Optional<Vehiculo> resultado = service.actualizar(99L, new Vehiculo());

        assertThat(resultado).isEmpty();
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("actualizar() – debe lanzar excepción si la nueva patente ya está en uso")
    void actualizar_debeLanzarExcepcionConPatenteDuplicada() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(vehiculoBase));
        when(repository.existsByPatente("WXYZ99")).thenReturn(true);

        Vehiculo cambios = new Vehiculo();
        cambios.setPatente("WXYZ99");

        assertThatThrownBy(() -> service.actualizar(1L, cambios))
                .isInstanceOf(FlotaException.class)
                .hasMessageContaining("WXYZ99");
    }



    @Test
    @DisplayName("eliminar() – debe marcar activo=false y disponible=false")
    void eliminar_debeMarcaInactivoYNoDisponible() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(vehiculoBase));
        when(repository.save(any(Vehiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean resultado = service.eliminar(1L);

        assertThat(resultado).isTrue();
        assertThat(vehiculoBase.getActivo()).isFalse();
        assertThat(vehiculoBase.getDisponible()).isFalse();
    }

    @Test
    @DisplayName("eliminar() – debe retornar false si el vehículo no existe")
    void eliminar_debeRetornarFalseSiNoExiste() {
        when(repository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        boolean resultado = service.eliminar(99L);

        assertThat(resultado).isFalse();
        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("cambiarDisponibilidad() – debe actualizar el flag disponible")
    void cambiarDisponibilidad_debeActualizarFlag() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(vehiculoBase));
        when(repository.save(any(Vehiculo.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Vehiculo> resultado = service.cambiarDisponibilidad(1L, false);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDisponible()).isFalse();
    }

    @Test
    @DisplayName("cambiarDisponibilidad() – debe retornar vacío si el vehículo no existe")
    void cambiarDisponibilidad_debeRetornarVacioSiNoExiste() {
        when(repository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        Optional<Vehiculo> resultado = service.cambiarDisponibilidad(99L, true);

        assertThat(resultado).isEmpty();
    }
}
