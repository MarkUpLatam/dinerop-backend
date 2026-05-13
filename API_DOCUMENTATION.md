# Dinerop Backend — API Documentation

> Base URL: `https://api.dinerop.com` (production) / `http://localhost:8080` (local)  
> Auth: JWT Bearer Token — `Authorization: Bearer <token>`

---

## Table of Contents

1. [Auth](#1-auth)
2. [Internal Auth](#2-internal-auth)
3. [Cooperatives](#3-cooperatives)
4. [Internal Cooperatives](#4-internal-cooperatives)
5. [Credits](#5-credits)
6. [Internal Credits](#6-internal-credits)
7. [Onboarding — Cliente](#7-onboarding--cliente)
8. [Onboarding — Cooperativa](#8-onboarding--cooperativa)
9. [Security & CORS](#9-security--cors)

---

## 1. Auth

**Base path:** `/api/auth`  
**Auth required:** No

---

### POST `/api/auth/login`

Autentica un usuario y retorna un JWT.

**Request body:**
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

**Response `200`:**
```json
{
  "accessToken": "eyJhbGciOi...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "role": "CLIENT | COOPERATIVE",
    "status": "PENDING_ACTIVATION | ACTIVE | BLOCKED"
  }
}
```

---

### GET `/api/auth/activate`

Activa una cuenta mediante token de activación.

**Query params:**

| Param   | Type   | Required |
|---------|--------|----------|
| `token` | String | ✅       |

**Response `200`:** `"Cuenta activada correctamente"`

---

### POST `/api/auth/complete-registration`

Completa el registro de un usuario pre-registrado.

**Request body:**
```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Response `200`:** `"Registro completado correctamente"`

---

### POST `/api/auth/password/forgot`

Inicia el flujo de recuperación de contraseña.

**Request body:**
```json
{
  "email": "user@example.com"
}
```

**Response `200`:** `"Si el correo existe, se enviaron instrucciones"`

> ℹ️ Siempre retorna el mismo mensaje por seguridad.

---

### POST `/api/auth/password/reset`

Restablece la contraseña usando el token recibido por email.

**Request body:**
```json
{
  "token": "reset-token-aqui",
  "newPassword": "nuevaPassword123"
}
```

> `newPassword` mínimo 8 caracteres.

**Response `200`:** `"Contraseña actualizada"`

---

## 2. Internal Auth

**Base path:** `/api/internal/auth`  
**Auth required:** No (uso interno)

---

### POST `/api/internal/auth/pre-register`

Pre-registra un usuario asignándole un rol y generando un token de activación.

**Request body:**
```json
{
  "email": "user@example.com",
  "role": "CLIENT | COOPERATIVE"
}
```

**Response `200`:**
```json
{
  "activationToken": "token-o-null-si-ya-estaba-activo"
}
```

---

## 3. Cooperatives

**Base path:** `/api/cooperatives`  
**Auth required:** No

---

### GET `/api/cooperatives`

Retorna todas las cooperativas.

**Response `200`:** Array de objetos cooperativa.

---

### GET `/api/cooperatives/{id}`

Retorna una cooperativa por ID.

**Path variables:**

| Param | Type | Required |
|-------|------|----------|
| `id`  | Long | ✅       |

**Response `200`:** Objeto cooperativa.

---

### GET `/api/cooperatives/by-city`

Filtra cooperativas por ciudad.

**Query params:**

| Param  | Type   | Required |
|--------|--------|----------|
| `city` | String | ✅       |

**Response `200`:** Array de cooperativas.

---

### GET `/api/cooperatives/by-province`

Filtra cooperativas por provincia.

**Query params:**

| Param      | Type   | Required |
|------------|--------|----------|
| `province` | String | ✅       |

**Response `200`:** Array de cooperativas.

---

### POST `/api/cooperatives`

Crea una nueva cooperativa.

**Request body:**
```json
{
  "nombre": "Cooperativa Ejemplo",
  "ciudad": "Quito",
  "provincia": "Pichincha",
  "direccion": "Av. Ejemplo 123",
  "telefono": "0991234567",
  "paginaWeb": "https://ejemplo.com",
  "logoUrl": "https://cdn.ejemplo.com/logo.png",
  "calificacion": 4.5,
  "montoMaximoCredito": 50000.00
}
```

**Response `200`:** Objeto cooperativa creada.

---

## 4. Internal Cooperatives

**Base path:** `/internal/cooperatives`  
**Auth required:** No (uso interno)

---

### GET `/internal/cooperatives/by-city`

Busca cooperativas por ciudad (vista interna).

**Query params:**

| Param  | Type   | Required |
|--------|--------|----------|
| `city` | String | ✅       |

**Response `200`:** Array de `InternalCooperativeDto`.

---

### GET `/internal/cooperatives/{id}`

Retorna una cooperativa por ID (vista interna).

**Path variables:**

| Param | Type | Required |
|-------|------|----------|
| `id`  | Long | ✅       |

**Response `200`:** `InternalCooperativeDto`.

---

### GET `/internal/cooperatives/eligible`

Retorna cooperativas elegibles según ciudad y monto mínimo.

**Query params:**

| Param       | Type       | Required |
|-------------|------------|----------|
| `city`      | String     | ✅       |
| `minAmount` | BigDecimal | ✅       |

**Response `200`:** Array de `InternalCooperativeDto`.

---

### GET `/internal/cooperatives/{id}/rates/{tipoCredito}`

Retorna la tasa activa de una cooperativa para un tipo de crédito.

**Path variables:**

| Param          | Type   | Required |
|----------------|--------|----------|
| `id`           | Long   | ✅       |
| `tipoCredito`  | String | ✅       |

**Response `200`:**
```json
{
  "cooperativaId": 1,
  "tipoCredito": "CONSUMO",
  "tasaAnual": 15.50
}
```

---

## 5. Credits

**Base path:** `/api/credits`

---

### POST `/api/credits/public-request`

Crea una solicitud de crédito pública (sin autenticación).

**Auth required:** No

**Request body:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "identification": "1234567890",
  "email": "juan@example.com",
  "phone": "0991234567",
  "province": "Pichincha",
  "city": "Quito",
  "amount": 10000.00,
  "plazoMeses": 24,
  "type": "CREDITO | INVERSION",
  "creditType": "CONSUMO"
}
```

**Response `200`:**
```json
{
  "requestId": 42,
  "message": "Solicitud enviada correctamente"
}
```

---

### POST `/api/credits/me`

Crea una solicitud de crédito para el cliente autenticado (ya registrado en la plataforma).
A diferencia de la solicitud pública, esta se **distribuye inmediatamente** a las cooperativas elegibles de la ciudad indicada (no requiere completar onboarding previamente).

**Auth required:** Yes — `CLIENT` role

**Request body:**
```json
{
  "monto": 5000.00,
  "type": "CREDITO | INVERSION",
  "creditType": "CONSUMO",
  "plazoMeses": 12,
  "province": "Pichincha",
  "city": "Quito"
}
```

> - `creditType` debe enviarse cuando `type == CREDITO` y debe ser `null` cuando `type == INVERSION`.
> - `plazoMeses` opcional; si se envía debe estar entre `1` y `360`.
> - `province` y `city` son usados para identificar las cooperativas elegibles a las que se distribuirá la solicitud.

**Response `200`:**
```json
{
  "requestId": 10,
  "message": "Solicitud creada correctamente"
}
```

**Errores comunes:**

| Código / Mensaje | Causa |
|---|---|
| `EXISTING_ACTIVE_REQUEST` | El cliente ya tiene una solicitud en estado `CREADA` activa. |
| `CREDIT_TYPE_REQUIRED_FOR_CREDITO` | `type == CREDITO` y no se envió `creditType`. |
| `CREDIT_TYPE_NOT_ALLOWED_FOR_INVERSION` | `type == INVERSION` y se envió `creditType`. |
| `INVALID_PLAZO_MESES` | `plazoMeses` fuera del rango 1–360. |

**Comportamiento interno:**
- Si existen cooperativas elegibles para la ciudad y monto, se crean filas `solicitud_cooperativa` (estado `ENVIADA`) y la solicitud pasa a `ENVIADA`.
- Si no hay cooperativas elegibles, la solicitud queda en `CREADA` (no se distribuye).

---

### GET `/api/credits/me`

Retorna las solicitudes de crédito del cliente autenticado.

**Auth required:** Yes

**Response `200`:**
```json
[
  {
    "solicitudId": 10,
    "estado": "PENDIENTE",
    "monto": 5000.00,
    "tipo": "CREDITO",
    "fechaSolicitud": "2026-04-01T10:00:00",
    "cantidadSolicitudesEnviadas": 3
  }
]
```

---

### GET `/api/credits/cooperative/me/requests`

Lista las solicitudes de crédito recibidas por la cooperativa autenticada.

**Auth required:** Yes — `COOPERATIVE` role

**Response `200`:** Array de solicitudes asignadas a la cooperativa.

---

### PUT `/api/credits/cooperative/me/requests/{solicitudId}/decision`

Registra la decisión de la cooperativa sobre una solicitud (pre-aprobar o rechazar).

**Auth required:** Yes — `COOPERATIVE` role

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Request body:**
```json
{
  "decision": "PRE_APROBAR | RECHAZAR"
}
```

**Response `200`:**
```json
{
  "message": "Decisión registrada correctamente"
}
```

---

### PUT `/api/credits/cooperative/me/requests/{solicitudId}/solicitar-garante`

Solicita un garante para una solicitud de crédito.

**Auth required:** Yes — `COOPERATIVE` role

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Request body:** Ninguno.

**Response `200`:**
```json
{
  "message": "Solicitud de garante registrada correctamente"
}
```

---

### GET `/api/credits/me/{solicitudId}/pre-approved`

Lista las cooperativas que han pre-aprobado una solicitud del cliente.

**Auth required:** Yes

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Response `200`:**
```json
[
  {
    "cooperativaId": 1,
    "nombreCooperativa": "CoopEjemplo",
    "estado": "PRE_APROBADA",
    "fechaActualizacion": "2026-04-05T14:30:00",
    "monto": 5000.00,
    "plazoMeses": 12,
    "tipoCredito": "CONSUMO",
    "tasaAnual": 15.50,
    "cuotaMensual": 452.00,
    "totalPagar": 5424.00,
    "interesTotal": 424.00
  }
]
```

---

### PUT `/api/credits/me/{solicitudId}/cooperatives/{cooperativaId}/accept`

El cliente acepta la pre-aprobación de una cooperativa específica.

**Auth required:** Yes

**Path variables:**

| Param          | Type | Required |
|----------------|------|----------|
| `solicitudId`  | Long | ✅       |
| `cooperativaId`| Long | ✅       |

**Response `200`:**
```json
{
  "message": "Solicitud aceptada correctamente"
}
```

---

## 6. Internal Credits

**Base path:** `/internal/credits`  
**Auth required:** No (uso interno)

---

### POST `/internal/credits/link-client`

Vincula un cliente a sus solicitudes previas (por email).

**Request body:**
```json
{
  "email": "user@example.com",
  "clientId": 5
}
```

**Response `200`:**
```json
{
  "updated": 2
}
```

---

### POST `/internal/credits/completed`

Notifica que el onboarding de un cliente fue completado.

**Request body:**
```json
{
  "clientId": 5
}
```

**Response:** `200 OK` o `400 Bad Request`.

---

### PUT `/internal/credits/solicitudes/{solicitudId}/estado`

Actualiza el estado de una solicitud para una cooperativa específica.

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Request body:**
```json
{
  "cooperativaId": 1,
  "nuevoEstado": "PRE_APROBADA"
}
```

**Response:** `200 OK`.

---

## 7. Onboarding — Cliente

**Base path:** `/api/onboarding/cliente`  
**Auth required:** Yes — `CLIENT` role

---

### POST `/api/onboarding/cliente/solicitante`

Crea una solicitud de onboarding con los datos del solicitante.

**Response status:** `201 Created`

**Request body:**
```json
{
  "destinoCredito": "Compra de vivienda",
  "solicitante": { "...PersonaOnboardingRequest" },
  "conyuge": { "...PersonaOnboardingRequest (opcional)" }
}
```

**`PersonaOnboardingRequest` fields:**

| Campo               | Type                        | Required |
|---------------------|-----------------------------|----------|
| `nombres`           | String                      | ✅       |
| `apellidos`         | String                      | ✅       |
| `cedula`            | String                      | ✅       |
| `fechaNacimiento`   | LocalDate                   | ✅       |
| `estadoCivil`       | String                      | ✅       |
| `ocupacion`         | String                      | ❌       |
| `empresaTrabajo`    | String                      | ❌       |
| `telefono`          | String                      | ❌       |
| `tieneConyuge`      | Boolean                     | ✅       |
| `direccion`         | DireccionRequest            | ✅       |
| `actividadEconomica`| ActividadEconomicaRequest   | ❌       |
| `ingresoEgreso`     | IngresoEgresoRequest        | ✅       |
| `referencias`       | List\<ReferenciaRequest\>   | ❌       |

**Response `201`:**
```json
{
  "id": 1,
  "estado": "EN_PROCESO",
  "destinoCredito": "Compra de vivienda",
  "fechaCreacion": "2026-04-13T08:00:00"
}
```

---

### POST `/api/onboarding/cliente/garante/completar`

Completa la información del garante para una solicitud activa.

**Response status:** `201 Created`

**Request body:** `PersonaOnboardingRequest` (ver campos arriba).

**Response `201`:** `SolicitudOnboardingResponse`.

---

### GET `/api/onboarding/cliente/{solicitudId}`

Retorna la información completa de onboarding de una solicitud.

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Response `200`:**
```json
{
  "solicitudId": 1,
  "destinoCredito": "Compra de vivienda",
  "estado": "EN_PROCESO",
  "personas": [ "...List<PersonaOnboardingResponse>" ]
}
```

---

### GET `/api/onboarding/cliente/formulario-status`

Retorna el estado del formulario del cliente autenticado.

**Response `200`:**
```json
{
  "formularioCompleto": true,
  "estadoFormulario": "COMPLETO"
}
```

---

## 8. Onboarding — Cooperativa

**Base path:** `/api/onboarding/cooperativa`  
**Auth required:** Yes — `COOPERATIVE` role

---

### POST `/api/onboarding/cooperativa/{solicitudId}/solicitar-garante`

La cooperativa solicita un garante para una solicitud de onboarding.

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Response `200`:** `OnboardingUnificadoResponse`.

---

### GET `/api/onboarding/cooperativa/{solicitudId}`

Retorna la información de onboarding de una solicitud (vista cooperativa).

**Path variables:**

| Param        | Type | Required |
|--------------|------|----------|
| `solicitudId`| Long | ✅       |

**Response `200`:** `OnboardingUnificadoResponse`.

---

## 9. Security & CORS

### Endpoints públicos (sin auth)

| Patrón                       | Descripción                        |
|------------------------------|------------------------------------|
| `/api/auth/**`               | Todos los endpoints de autenticación |
| `/api/cooperatives/**`       | Listado y búsqueda de cooperativas |
| `POST /api/credits/public-request` | Solicitud pública de crédito |
| `GET /api/credits/me`        | Ver mis créditos                   |
| `/v3/api-docs/**`            | OpenAPI spec                       |
| `/swagger-ui/**`             | Swagger UI                         |
| `/actuator/**`               | Spring Actuator                    |

### Endpoints protegidos

| Patrón                                  | Rol requerido  |
|-----------------------------------------|----------------|
| `POST /api/credits/me`                  | `CLIENT`       |
| `/api/credits/cooperative/me/**`        | `COOPERATIVE`  |
| `/api/onboarding/cliente/**`            | `CLIENT`       |
| `/api/onboarding/cooperativa/**`        | `COOPERATIVE`  |

### CORS — Orígenes permitidos

- `http://localhost:5173`
- `http://localhost:5174`
- `https://www.dinerop.com`
- `https://dinerop.com`
- `https://markup-landing.vercel.app`
- `https://dinerup-app.vercel.app`

**Métodos:** `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`  
**Headers:** `*`  
**Credentials:** `true`  
**Session policy:** Stateless (JWT)

---

## Resumen de endpoints

| Módulo                  | Total |
|-------------------------|-------|
| Auth                    | 5     |
| Internal Auth           | 1     |
| Cooperatives (público)  | 5     |
| Internal Cooperatives   | 4     |
| Credits                 | 8     |
| Internal Credits        | 3     |
| Onboarding Cliente      | 4     |
| Onboarding Cooperativa  | 2     |
| **Total**               | **32**|
