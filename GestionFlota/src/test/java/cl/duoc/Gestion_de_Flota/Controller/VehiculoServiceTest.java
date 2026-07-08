package cl.duoc.Gestion_de_Flota.Controller;

import cl.duoc.Gestion_de_Flota.Exception.FlotaException;
import cl.duoc.Gestion_de_Flota.Model.Vehiculo;
import cl.duoc.Gestion_de_Flota.Service.VehiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    @DisplayName("POST /api/vehiculos - debe crear un vehículo y retornar 201")
    void crear_debeRetornar201() throws Exception {
        when(service.crear(any(Vehiculo.class))).thenReturn(vehiculoBase);

        mockMvc.perform(post("/api/vehiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(vehiculoBase)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patente").value("ABCD12"));
    }

    @Test
    @DisplayName("GET /api/vehiculos - debe retornar lista de vehículos activos")
    void listar_debeRetornar200ConLista() throws Exception {
        when(service.listar()).thenReturn(List.of(vehiculoBase));

        mockMvc.perform(get("/api/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patente").value("ABCD12"));
    }

    @Test
    @DisplayName("GET /api/vehiculos/disponibles - debe retornar solo disponibles")
    void listarDisponibles_debeRetornar200() throws Exception {
        when(service.listarDisponibles()).thenReturn(List.of(vehiculoBase));

        mockMvc.perform(get("/api/vehiculos/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true));
    }

    @Test
    @DisplayName("GET /api/vehiculos/{id} - debe retornar 200 si existe")
    void buscarPorId_debeRetornar200SiExiste() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(Optional.of(vehiculoBase));

        mockMvc.perform(get("/api/vehiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/vehiculos/{id} - debe retornar 404 si no existe")
    void buscarPorId_debeRetornar404SiNoExiste() throws Exception {
        when(service.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/vehiculos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/vehiculos/{id} - debe actualizar y retornar 200")
    void actualizar_debeRetornar200() throws Exception {
        when(service.actualizar(eq(1L), any(Vehiculo.class))).thenReturn(Optional.of(vehiculoBase));

        mockMvc.perform(put("/api/vehiculos/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(vehiculoBase)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/vehiculos/{id} - debe retornar 404 si no existe")
    void actualizar_debeRetornar404SiNoExiste() throws Exception {
        when(service.actualizar(eq(99L), any(Vehiculo.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/vehiculos/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(vehiculoBase)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/vehiculos/{id}/disponibilidad - debe retornar 200")
    void cambiarDisponibilidad_debeRetornar200() throws Exception {
        when(service.cambiarDisponibilidad(1L, false)).thenReturn(Optional.of(vehiculoBase));

        mockMvc.perform(patch("/api/vehiculos/1/disponibilidad")
                        .param("disponible", "false"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/vehiculos/{id} - debe retornar 200 si se elimina")
    void eliminar_debeRetornar200SiExiste() throws Exception {
        when(service.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/vehiculos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/vehiculos/{id} - debe retornar 404 si no existe")
    void eliminar_debeRetornar404SiNoExiste() throws Exception {
        when(service.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/vehiculos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/vehiculos - debe retornar 400 con detalle de error si la patente ya existe")
    void crear_debeRetornar400SiPatenteDuplicada() throws Exception {
        when(service.crear(any(Vehiculo.class)))
                .thenThrow(new FlotaException("Ya existe un vehículo con patente: ABCD12"));

        mockMvc.perform(post("/api/vehiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(vehiculoBase)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ya existe un vehículo con patente: ABCD12"))
                .andExpect(jsonPath("$.path").value("/api/vehiculos"))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    @DisplayName("POST /api/vehiculos - debe retornar 400 si el cuerpo no pasa las validaciones")
    void crear_debeRetornar400SiValidacionFalla() throws Exception {
        Vehiculo invalido = new Vehiculo();
        invalido.setPatente("");
        invalido.setMarca("");
        invalido.setModelo("");

        mockMvc.perform(post("/api/vehiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/vehiculos - debe retornar 500 con detalle de error ante una falla inesperada")
    void listar_debeRetornar500SiOcurreErrorInesperado() throws Exception {
        when(service.listar()).thenThrow(new RuntimeException("Fallo inesperado de base de datos"));

        mockMvc.perform(get("/api/vehiculos"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    @DisplayName("GET /api/vehiculos/marca/{marca} - debe retornar vehículos de esa marca")
    void listarPorMarca_debeRetornar200() throws Exception {
        when(service.listarPorMarca("Toyota")).thenReturn(List.of(vehiculoBase));

        mockMvc.perform(get("/api/vehiculos/marca/Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Toyota"));
    }

    @Test
    @DisplayName("GET /api/vehiculos/disponibles/count - debe retornar la cantidad de disponibles")
    void contarDisponibles_debeRetornar200() throws Exception {
        when(service.contarDisponibles()).thenReturn(3L);

        mockMvc.perform(get("/api/vehiculos/disponibles/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}