# FastTrack Courier - Microservicio de Gestión de Flota Vehicular

## Descripción del Proyecto

**FastTrack Courier - Gestión de Flota** es un microservicio responsable de la administración del parque vehicular de la empresa logística FastTrack Courier. Este servicio permite gestionar vehículos, controlar su disponibilidad y realizar operaciones CRUD con soft delete.

### Contexto del Dominio
El microservicio resuelve la necesidad de:
- **Registro de vehículos**: Alta de nuevos vehículos con patente única
- **Control de disponibilidad**: Marcar vehículos como disponibles o no disponibles
- **Soft Delete**: Eliminación lógica de vehículos manteniendo el historial
- **Consulta de flota**: Listado de vehículos activos y disponibles

---

## Equipo de Desarrollo

| Nombre | Rol |
|--------|-----|
| Sebastián Saavedra | Desarrollador Full Stack |
| Benjamin Benavides | Desarrollador Backend |
| John Osorio | Desarrollador Backend |

---

## Arquitectura del Microservicio
┌─────────────────────────────────────────────────────────────────┐
│ Gestión de Flota (Puerto 16000) │
├─────────────────────────────────────────────────────────────────┤
│ │
│ ┌──────────────┐ ┌──────────────────┐ ┌──────────────┐ │
│ │ Controller │───▶│ Service │───▶│ Repository │ │
│ │ (REST API) │ │ (Lógica Negocio)│ │ (JPA/Hiber) │ │
│ └──────────────┘ └──────────────────┘ └──────────────┘ │
│ │ │ │ │
│ ▼ ▼ ▼ │
│ ┌──────────────┐ ┌──────────────────┐ ┌──────────────┐ │
│ │ DTO/Model │ │ Validaciones │ │ Oracle DB │ │
│ │ (Entidades) │ │ de Negocio │ │ (vehiculos) │ │
│ └──────────────┘ └──────────────────┘ └──────────────┘ │
└─────────────────────────────────────────────────────────────────┘

text

### Comunicación con otros Microservicios
Este microservicio es consumido por el **Microservicio de Rastreo Logístico** (puerto 28000) mediante REST, exponiendo sus endpoints para consulta de vehículos.

---

## Documentación Swagger

### Local
- **Swagger UI**: [http://localhost:16000/swagger-ui.html](http://localhost:16000/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:16000/api-docs](http://localhost:16000/api-docs)

### Remota (Producción)
- **Swagger UI**: [https://flota.fasttrack.cl/swagger-ui.html](https://flota.fasttrack.cl/swagger-ui.html)
- **OpenAPI JSON**: [https://flota.fasttrack.cl/api-docs](https://flota.fasttrack.cl/api-docs)

---

## Rutas Principales (Endpoints)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/vehiculos` | Registrar un nuevo vehículo |
| GET | `/api/vehiculos` | Listar todos los vehículos activos |
| GET | `/api/vehiculos/disponibles` | Listar vehículos disponibles |
| GET | `/api/vehiculos/{id}` | Obtener vehículo por ID |
| PUT | `/api/vehiculos/{id}` | Actualizar datos del vehículo |
| PATCH | `/api/vehiculos/{id}/disponibilidad` | Cambiar disponibilidad |
| DELETE | `/api/vehiculos/{id}` | Eliminación lógica (soft delete) |
| GET | `/api/vehiculos/count/disponibles` | Contar vehículos disponibles |
| GET | `/api/vehiculos/marca/{marca}` | Listar vehículos por marca |
| GET | `/health` | Verificar estado del microservicio |

---

### Ejemplos de Requests

#### Crear un vehículo
```bash
curl -X POST http://localhost:16000/api/vehiculos \
  -H "Content-Type: application/json" \
  -d '{  
    "patente": "ABCD12",
    "marca": "Toyota",
    "modelo": "Hilux"
  }'

Listar vehículos disponibles
bash
curl -X GET http://localhost:16000/api/vehiculos/disponibles
Cambiar disponibilidad
bash
curl -X PATCH http://localhost:16000/api/vehiculos/1/disponibilidad?disponible=false
Eliminar vehículo (soft delete)
bash
curl -X DELETE http://localhost:16000/api/vehiculos/1
Contar vehículos disponibles
bash
curl -X GET http://localhost:16000/api/vehiculos/count/disponibles
Listar vehículos por marca
bash
curl -X GET http://localhost:16000/api/vehiculos/marca/Toyota

## RESUMEN DE UBICACIONES

| Microservicio | Ruta del README |
|---------------|-----------------|
| **Rastreo Logístico (CASO 4)** | `Casoo4/README.md` |
| **Gestión de Flota (CASO 1)** | `GestionFlota/README.md` |
| **Principal (Raíz del proyecto)** | `README.md` |

