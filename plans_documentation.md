# 📋 Módulo de Planes

## Descripción

El módulo de Planes gestiona los planes de suscripción disponibles en la plataforma y las suscripciones de usuarios a estos planes. Está compuesto por dos entidades principales: **Plans** y **Users-Plans**.

---

## 🏗️ Arquitectura del Módulo

### Estructura de Componentes

```
planes/
├── controller/
│   ├── PlanController.java
│   └── UsersPlansController.java
├── service/
│   ├── PlanService.java
│   └── UsersPlansService.java
├── repository/
│   ├── PlanRepository.java
│   └── UsersPlansRepository.java
├── model/
│   ├── Plan.java
│   └── UsersPlans.java
└── dto/
    ├── PlanDto.java
    ├── CreatePlanRequest.java
    ├── UpdatePlanRequest.java
    ├── UsersPlanDto.java
    └── CreateUsersPlanRequest.java
```

### Diagrama de Relaciones

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│    User     │         │ Users_Plans  │         │    Plan     │
├─────────────┤         ├──────────────┤         ├─────────────┤
│ id (PK)     │◄────────│ id (PK)      │────────►│ id (PK)     │
│ email       │  1   *  │ user_id (FK) │  *   1  │ name        │
│ password    │         │ plan_id (FK) │         │ maxInstances│
│ role        │         │ status       │         │ priceId     │
│ fullName    │         │ startDate    │         │ description │
└─────────────┘         │ endDate      │         │ state       │
                        └──────────────┘         └─────────────┘
```

---

## 💾 Modelos de Datos

### 1. Plan (Entity)

Representa un plan de suscripción disponible en la plataforma.

```java
@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;                    // Nombre único del plan
    
    @Column(nullable = false)
    private int maxInstances;               // Máximo de instancias permitidas
    
    @Column(nullable = false)
    private int priceIdMercadoPago;         // ID de precio en MercadoPago
    
    @Column(columnDefinition = "TEXT")
    private String description;             // Descripción del plan
    
    @Column(nullable = false)
    private String state;                   // "ACTIVE" o "INACTIVE"
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;        // Fecha de creación
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;        // Fecha de última actualización
    
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UsersPlans> usersPlans;     // Relación con suscripciones
}
```

**Campos:**
- `id`: Identificador único del plan
- `name`: Nombre único del plan (ej: "Plan Basic", "Plan Premium")
- `maxInstances`: Número máximo de instancias que permite el plan
- `priceIdMercadoPago`: ID del precio configurado en MercadoPago
- `description`: Descripción detallada del plan
- `state`: Estado del plan - valores posibles:
  - `ACTIVE`: Plan activo y disponible para suscripción
  - `INACTIVE`: Plan deshabilitado
- `createdAt`: Timestamp de creación (auto-generado)
- `updatedAt`: Timestamp de última actualización (auto-actualizado)

### 2. UsersPlans (Entity)

Tabla intermedia que relaciona usuarios con planes suscritos.

```java
@Entity
@Table(name = "users_plans")
public class UsersPlans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                      // Usuario suscrito (FK)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;                      // Plan suscrito (FK)
    
    @Column(nullable = false)
    private String status;                  // Estado de la suscripción
    
    @Column(name = "start_date")
    private Date startDate;                 // Fecha de inicio
    
    @Column(name = "end_date")
    private Date endDate;                   // Fecha de finalización
}
```

**Campos:**
- `id`: Identificador único de la suscripción
- `user`: Referencia al usuario suscrito (Foreign Key)
- `plan`: Referencia al plan suscrito (Foreign Key)
- `status`: Estado de la suscripción - valores posibles:
  - `ACTIVE`: Suscripción activa
  - `INACTIVE`: Suscripción cancelada o expirada
- `startDate`: Fecha de inicio de la suscripción
- `endDate`: Fecha de finalización de la suscripción

### 3. DTOs (Data Transfer Objects)

#### PlanDto
```java
public class PlanDto {
    private Long id;
    private String name;
    private int maxInstances;
    private String description;
    private String state;
}
```

#### CreatePlanRequest
```java
public class CreatePlanRequest {
    private String name;
    private int maxInstances;
    private int priceIdMercadoPago;
    private String description;
    private String state;
}
```

#### UpdatePlanRequest
```java
public class UpdatePlanRequest {
    private String name;
    private Integer maxInstances;
    private Integer priceIdMercadoPago;
    private String description;
    private String state;
}
```

#### UsersPlanDto
```java
public class UsersPlanDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private Long planId;
    private String planName;
    private int planMaxInstances;
    private String status;
    private Date startDate;
    private Date endDate;
}
```

#### CreateUsersPlanRequest
```java
public class CreateUsersPlanRequest {
    private Long userId;
    private Long planId;
    private String status;
    private Date startDate;
    private Date endDate;
}
```

---

## 🔌 API Endpoints

### Plans API

**Base URL:** `/api/plans`

| Método | Endpoint | Descripción | Rol Requerido | Respuesta |
|--------|----------|-------------|---------------|-----------|
| POST | `/` | Crear nuevo plan | ADMIN | 201 Created |
| GET | `/` | Listar planes activos | Público | 200 OK |
| GET | `/{id}` | Obtener plan por ID | Público | 200 OK |
| PUT | `/{id}` | Actualizar plan | ADMIN | 200 OK |
| DELETE | `/{id}` | Eliminar plan | ADMIN | 204 No Content |

#### 1. Crear Plan

Crea un nuevo plan de suscripción en el sistema.

**Request:**
```http
POST /api/plans
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "name": "Plan Premium",
  "maxInstances": 10,
  "priceIdMercadoPago": 12345,
  "description": "Plan premium con 10 instancias incluidas",
  "state": "ACTIVE"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Plan Premium",
  "maxInstances": 10,
  "description": "Plan premium con 10 instancias incluidas",
  "state": "ACTIVE"
}
```

**Validaciones:**
- `name` es obligatorio y debe ser único
- `maxInstances` debe ser mayor a 0
- `priceIdMercadoPago` es obligatorio
- `state` por defecto es "ACTIVE"

#### 2. Listar Planes Activos

Retorna todos los planes con estado ACTIVE.

**Request:**
```http
GET /api/plans
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Plan Basic",
    "maxInstances": 3,
    "description": "Plan básico con 3 instancias",
    "state": "ACTIVE"
  },
  {
    "id": 2,
    "name": "Plan Premium",
    "maxInstances": 10,
    "description": "Plan premium con 10 instancias",
    "state": "ACTIVE"
  },
  {
    "id": 3,
    "name": "Plan Enterprise",
    "maxInstances": 50,
    "description": "Plan empresarial con 50 instancias",
    "state": "ACTIVE"
  }
]
```

#### 3. Obtener Plan por ID

Obtiene los detalles de un plan específico.

**Request:**
```http
GET /api/plans/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Plan Premium",
  "maxInstances": 10,
  "description": "Plan premium con 10 instancias incluidas",
  "state": "ACTIVE"
}
```

**Response (404 Not Found):**
```json
{
  "message": "Plan not found with id: 99"
}
```

#### 4. Actualizar Plan

Actualiza los datos de un plan existente. Todos los campos son opcionales.

**Request:**
```http
PUT /api/plans/1
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "name": "Plan Premium Plus",
  "maxInstances": 15,
  "description": "Plan premium mejorado con 15 instancias"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Plan Premium Plus",
  "maxInstances": 15,
  "description": "Plan premium mejorado con 15 instancias",
  "state": "ACTIVE"
}
```

#### 5. Eliminar Plan

Elimina permanentemente un plan del sistema.

**Request:**
```http
DELETE /api/plans/1
Authorization: Bearer {admin-token}
```

**Response (204 No Content)**

**Response (404 Not Found):**
```json
{
  "message": "Plan not found with id: 99"
}
```

---

### Users-Plans API

**Base URL:** `/api/users-plans`

| Método | Endpoint | Descripción | Rol Requerido | Respuesta |
|--------|----------|-------------|---------------|-----------|
| POST | `/` | Crear suscripción | USER, ADMIN | 201 Created |
| GET | `/` | Listar todas las suscripciones | ADMIN | 200 OK |
| GET | `/user/{userId}` | Planes de un usuario | USER, ADMIN | 200 OK |
| GET | `/active` | Todas las suscripciones activas | ADMIN | 200 OK |
| GET | `/inactive` | Todas las suscripciones inactivas | ADMIN | 200 OK |
| GET | `/user/{userId}/active` | Planes activos de un usuario | USER, ADMIN | 200 OK |
| PUT | `/{id}` | Actualizar suscripción | ADMIN | 200 OK |
| DELETE | `/{id}` | Eliminar suscripción | ADMIN | 204 No Content |

#### 1. Crear Suscripción

Crea una nueva suscripción de usuario a un plan.

**Request:**
```http
POST /api/users-plans
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 5,
  "planId": 2,
  "status": "ACTIVE",
  "startDate": "2025-01-01T00:00:00.000Z",
  "endDate": "2025-12-31T23:59:59.000Z"
}
```

**Response (201 Created):**
```json
{
  "id": 10,
  "userId": 5,
  "userEmail": "user@example.com",
  "userFullName": "John Doe",
  "planId": 2,
  "planName": "Plan Premium",
  "planMaxInstances": 10,
  "status": "ACTIVE",
  "startDate": "2025-01-01T00:00:00.000Z",
  "endDate": "2025-12-31T23:59:59.000Z"
}
```

**Validaciones:**
- El `userId` debe existir en el sistema
- El `planId` debe existir y estar activo
- `status` por defecto es "ACTIVE"

#### 2. Listar Todas las Suscripciones

Retorna todas las suscripciones del sistema (solo Admin).

**Request:**
```http
GET /api/users-plans
Authorization: Bearer {admin-token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "userEmail": "user1@example.com",
    "userFullName": "John Doe",
    "planId": 2,
    "planName": "Plan Premium",
    "planMaxInstances": 10,
    "status": "ACTIVE",
    "startDate": "2025-01-01T00:00:00.000Z",
    "endDate": "2025-12-31T23:59:59.000Z"
  },
  {
    "id": 2,
    "userId": 8,
    "userEmail": "user2@example.com",
    "userFullName": "Jane Smith",
    "planId": 1,
    "planName": "Plan Basic",
    "planMaxInstances": 3,
    "status": "ACTIVE",
    "startDate": "2025-02-15T00:00:00.000Z",
    "endDate": "2026-02-15T23:59:59.000Z"
  }
]
```

#### 3. Obtener Planes de un Usuario

Retorna todos los planes (activos e inactivos) de un usuario específico.

**Request:**
```http
GET /api/users-plans/user/5
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "userEmail": "user@example.com",
    "userFullName": "John Doe",
    "planId": 2,
    "planName": "Plan Premium",
    "planMaxInstances": 10,
    "status": "ACTIVE",
    "startDate": "2025-01-01T00:00:00.000Z",
    "endDate": "2025-12-31T23:59:59.000Z"
  },
  {
    "id": 5,
    "userId": 5,
    "userEmail": "user@example.com",
    "userFullName": "John Doe",
    "planId": 1,
    "planName": "Plan Basic",
    "planMaxInstances": 3,
    "status": "INACTIVE",
    "startDate": "2024-01-01T00:00:00.000Z",
    "endDate": "2024-12-31T23:59:59.000Z"
  }
]
```

#### 4. Obtener Todas las Suscripciones Activas

Retorna todas las suscripciones con status "ACTIVE" (solo Admin).

**Request:**
```http
GET /api/users-plans/active
Authorization: Bearer {admin-token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "userEmail": "user1@example.com",
    "userFullName": "John Doe",
    "planId": 2,
    "planName": "Plan Premium",
    "planMaxInstances": 10,
    "status": "ACTIVE",
    "startDate": "2025-01-01T00:00:00.000Z",
    "endDate": "2025-12-31T23:59:59.000Z"
  }
]
```

#### 5. Obtener Todas las Suscripciones Inactivas

Retorna todas las suscripciones con status diferente a "ACTIVE" (solo Admin).

**Request:**
```http
GET /api/users-plans/inactive
Authorization: Bearer {admin-token}
```

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "userId": 5,
    "userEmail": "user@example.com",
    "userFullName": "John Doe",
    "planId": 1,
    "planName": "Plan Basic",
    "planMaxInstances": 3,
    "status": "INACTIVE",
    "startDate": "2024-01-01T00:00:00.000Z",
    "endDate": "2024-12-31T23:59:59.000Z"
  }
]
```

#### 6. Obtener Planes Activos de un Usuario

Retorna solo los planes activos de un usuario específico.

**Request:**
```http
GET /api/users-plans/user/5/active
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 5,
    "userEmail": "user@example.com",
    "userFullName": "John Doe",
    "planId": 2,
    "planName": "Plan Premium",
    "planMaxInstances": 10,
    "status": "ACTIVE",
    "startDate": "2025-01-01T00:00:00.000Z",
    "endDate": "2025-12-31T23:59:59.000Z"
  }
]
```

#### 7. Actualizar Suscripción

Actualiza los datos de una suscripción existente.

**Request:**
```http
PUT /api/users-plans/1
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "status": "INACTIVE",
  "endDate": "2025-06-30T23:59:59.000Z"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 5,
  "userEmail": "user@example.com",
  "userFullName": "John Doe",
  "planId": 2,
  "planName": "Plan Premium",
  "planMaxInstances": 10,
  "status": "INACTIVE",
  "startDate": "2025-01-01T00:00:00.000Z",
  "endDate": "2025-06-30T23:59:59.000Z"
}
```

**Response (404 Not Found):**
```json
{
  "message": "User plan not found"
}
```

#### 8. Eliminar Suscripción

Elimina permanentemente una suscripción del sistema.

**Request:**
```http
DELETE /api/users-plans/1
Authorization: Bearer {admin-token}
```

**Response (204 No Content)**

**Response (404 Not Found):**
```json
{
  "message": "User plan not found"
}
```

---

## 🔒 Autorización y Permisos

### Matriz de Permisos

| Endpoint | ADMIN | USER | Público |
|----------|-------|------|---------|
| **Plans** |
| POST `/api/plans` | ✅ | ❌ | ❌ |
| GET `/api/plans` | ✅ | ✅ | ✅ |
| GET `/api/plans/{id}` | ✅ | ✅ | ✅ |
| PUT `/api/plans/{id}` | ✅ | ❌ | ❌ |
| DELETE `/api/plans/{id}` | ✅ | ❌ | ❌ |
| **Users-Plans** |
| POST `/api/users-plans` | ✅ | ✅* | ❌ |
| GET `/api/users-plans` | ✅ | ❌ | ❌ |
| GET `/api/users-plans/user/{userId}` | ✅ | ✅* | ❌ |
| GET `/api/users-plans/active` | ✅ | ❌ | ❌ |
| GET `/api/users-plans/inactive` | ✅ | ❌ | ❌ |
| GET `/api/users-plans/user/{userId}/active` | ✅ | ✅* | ❌ |
| PUT `/api/users-plans/{id}` | ✅ | ❌ | ❌ |
| DELETE `/api/users-plans/{id}` | ✅ | ❌ | ❌ |

*\* Los usuarios solo pueden acceder a sus propios datos*

### Implementación

```java
// PlanController
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<PlanDto> createPlan(@RequestBody CreatePlanRequest request)

// UsersPlansController
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public ResponseEntity<UsersPlanDto> createUserPlan(@RequestBody CreateUsersPlanRequest request)

@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<UsersPlanDto>> getAllUsersPlans()
```

---

## 📊 Base de Datos

### Schema SQL

```sql
-- Tabla de Planes
CREATE TABLE plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    max_instances INT NOT NULL,
    price_id_mercadopago INT NOT NULL,
    description TEXT,
    state VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_state (state),
    INDEX idx_name (name)
);

-- Tabla de Suscripciones Usuario-Plan
CREATE TABLE users_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    start_date DATETIME NULL,
    end_date DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_status (status),
    INDEX idx_user_status (user_id, status)
);
```

### Datos de Ejemplo

```sql
-- Insertar planes de ejemplo
INSERT INTO plans (name, max_instances, price_id_mercadopago, description, state) VALUES
('Plan Basic', 3, 1000, 'Plan básico con 3 instancias', 'ACTIVE'),
('Plan Premium', 10, 5000, 'Plan premium con 10 instancias', 'ACTIVE'),
('Plan Enterprise', 50, 15000, 'Plan empresarial con 50 instancias', 'ACTIVE');

-- Insertar suscripciones de ejemplo
INSERT INTO users_plans (user_id, plan_id, status, start_date, end_date) VALUES
(1, 2, 'ACTIVE', '2025-01-01 00:00:00', '2025-12-31 23:59:59'),
(2, 1, 'ACTIVE', '2025-02-15 00:00:00', '2026-02-15 23:59:59');
```

---

## 🔄 Lógica de Negocio

### Ciclo de Vida de un Plan

1. **Creación** (Admin)
   - Se crea el plan con estado ACTIVE por defecto
   - Se asigna `createdAt` y `updatedAt` automáticamente

2. **Activación/Desactivación** (Admin)
   - Cambiar `state` a ACTIVE/INACTIVE
   - Los planes INACTIVE no aparecen en listados públicos

3. **Modificación** (Admin)
   - Actualizar cualquier campo excepto `id` y `createdAt`
   - Se actualiza `updatedAt` automáticamente

4. **Eliminación** (Admin)
   - Eliminación permanente del plan
   - Elimina en cascada todas las suscripciones relacionadas

### Ciclo de Vida de una Suscripción

1. **Suscripción** (User/Admin)
   - Usuario se suscribe a un plan activo
   - Se crea con status ACTIVE
   - Se definen fechas de inicio y fin

2. **Gestión** (Admin)
   - Modificar status (ACTIVE/INACTIVE)
   - Ajustar fechas de vigencia
   - Cancelar suscripción

3. **Consulta** (User/Admin)
   - Usuarios consultan sus propias suscripciones
   - Admins consultan todas las suscripciones

4. **Eliminación** (Admin)
   - Eliminación permanente de la suscripción

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Admin crea un nuevo plan

```bash
# 1. Login como Admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@crudcloud.com",
    "password": "admin123"
  }'

# Respuesta:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "email": "admin@crudcloud.com",
#   "role": "ADMIN"
# }

# 2. Crear el plan
curl -X POST http://localhost:8080/api/plans \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Plan Enterprise",
    "maxInstances": 50,
    "priceIdMercadoPago": 99999,
    "description": "Plan empresarial con 50 instancias",
    "state": "ACTIVE"
  }'

# Respuesta:
# {
#   "id": 3,
#   "name": "Plan Enterprise",
#   "maxInstances": 50,
#   "description": "Plan empresarial con 50 instancias",
#   "state": "ACTIVE"
# }
```

### Ejemplo 2: Usuario se suscribe a un plan

```bash
# 1. Consultar planes disponibles (público)
curl http://localhost:8080/api/plans

# Respuesta:
# [
#   {
#     "id": 1,
#     "name": "Plan Basic",
#     "maxInstances": 3,
#     "description": "Plan básico",
#     "state": "ACTIVE"
#   },
#   {
#     "id": 2,
#     "name": "Plan Premium",
#     "maxInstances": 10,
#     "description": "Plan premium",
#     "state": "ACTIVE"
#   }
# ]

# 2. Login como Usuario
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "user123"
  }'

# 3. Suscribirse al Plan Premium (id: 2)
curl -X POST http://localhost:8080/api/users-plans \
  -H "Authorization: Bearer {user-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "planId": 2,
    "status": "ACTIVE",
    "startDate": "2025-01-01T00:00:00.000Z",
    "endDate": "2025-12-31T23:59:59.000Z"
  }'

# Respuesta:
# {
#   "id": 10,
#   "userId": 5,
#   "userEmail": "user@example.com",
#   "userFullName": "John Doe",
#   "planId": 2,
#   "planName": "Plan Premium",
#   "planMaxInstances": 10,
#   "status": "ACTIVE",
#   "startDate": "2025-01-01T00:00:00.000Z",
#   "endDate": "2025-12-31T23:59:59.000Z"
# }
```

### Ejemplo 3: Usuario consulta sus planes activos

```bash
curl http://localhost:8080/api/users-plans/user/5/active \
  -H "Authorization: Bearer {user-token}"

# Respuesta:
# [
#   {
#     "id": 10,
#     "userId": 5,
#     "userEmail": "user@example.com",
#     "userFullName": "John Doe",
#     "planId": 2,
#     "planName": "Plan Premium",
#     "planMaxInstances": 10,
#     "status": "ACTIVE",
#     "startDate": "2025-01-01T00:00:00.000Z",
#     "endDate": "2025-12-31T23:59:59.000Z"
#   }
# ]
```

### Ejemplo 4: Admin desactiva un plan

```bash
curl -X PUT http://localhost:8080/api/plans/1 \
  -H "Authorization: Bearer {admin-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "state": "INACTIVE"
  }'

# Respuesta:
# {
#   "id": 1,
#   "name": "Plan Basic",
#   "maxInstances": 3,
#   "description": "Plan básico",
#   "state": "INACTIVE"
# }
```

### Ejemplo 5: Admin cancela una suscripción

```bash
curl -X PUT http://localhost:8080/api/users-plans/10 \
  -H "Authorization: Bearer {admin-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "INACTIVE",
    "endDate": "2025-06-30T23:59:59.000Z"
  }'

# Respuesta:
# {
#   "id": 10,
#   "userId": 5,
#   "userEmail": "user@example.com",
#   "userFullName": "John Doe",
#   "planId": 2,
#   "planName": "Plan Premium",
#   "planMaxInstances": 10,
#   "status": "INACTIVE",
#   "startDate": "2025-01-01T00:00:00.000Z",
#   "endDate": "2025-06-30T23:59:59.000Z"
# }
```

---

## 🔍 Repositorios y Consultas

### PlanRepository

```java
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // Obtener planes por estado
    List<Plan> findByState(String state);
    
    // Buscar plan por nombre
    Plan findByName(String name);
}
```

**Consultas disponibles:**
- `findByState("ACTIVE")` - Obtiene todos los planes activos
- `findByState("INACTIVE")` - Obtiene todos los planes inactivos
- `findByName("Plan Premium")` - Busca un plan específico por nombre

### UsersPlansRepository

```java
@Repository
public interface UsersPlansRepository extends JpaRepository<UsersPlans, Long> {
    // Obtener todos los planes de un usuario
    List<UsersPlans> findByUserId(Long userId);
    
    // Obtener planes de un usuario por estado
    List<UsersPlans> findByUserIdAndStatus(Long userId, String status);
    
    // Obtener todas las suscripciones por estado
    List<UsersPlans> findByStatus(String status);
    
    // Obtener suscripciones inactivas
    List<UsersPlans> findByStatusNot(String status);
}
```

**Consultas disponibles:**
- `findByUserId(5)` - Todos los planes del usuario con id 5
- `findByUserIdAndStatus(5, "ACTIVE")` - Planes activos del usuario 5
- `findByStatus("ACTIVE")` - Todas las suscripciones activas
- `findByStatusNot("ACTIVE")` - Todas las suscripciones inactivas

---

## 🛠️ Servicios

### PlanService

Lógica de negocio para la gestión de planes.

**Métodos principales:**

```java
public class PlanService {
    
    // Crear un nuevo plan
    public PlanDto createPlan(CreatePlanRequest request)
    
    // Obtener todos los planes activos
    public List<PlanDto> getAllPlans()
    
    // Obtener un plan por ID
    public PlanDto getPlanById(Long id)
    
    // Actualizar un plan existente
    public PlanDto updatePlan(Long id, UpdatePlanRequest request)
    
    // Eliminar un plan
    public void deletePlan(Long id)
    
    // Convertir entidad a DTO
    private PlanDto convertToDTO(Plan plan)
}
```

**Características:**
- Manejo automático de timestamps (`createdAt`, `updatedAt`)
- Validación de existencia antes de actualizar/eliminar
- Conversión automática entre entidades y DTOs
- Manejo de excepciones con mensajes descriptivos

### UsersPlansService

Lógica de negocio para la gestión de suscripciones.

**Métodos principales:**

```java
public class UsersPlansService {
    
    // Crear una nueva suscripción
    public UsersPlanDto createUserPlan(CreateUsersPlanRequest request)
    
    // Obtener todas las suscripciones
    public List<UsersPlanDto> getAllUsersPlans()
    
    // Obtener planes de un usuario
    public List<UsersPlanDto> getPlansByUserId(Long userId)
    
    // Obtener todas las suscripciones activas
    public List<UsersPlanDto> getActivePlans()
    
    // Obtener todas las suscripciones inactivas
    public List<UsersPlanDto> getInactivePlans()
    
    // Obtener planes activos de un usuario
    public List<UsersPlanDto> getActivePlansByUser(Long userId)
    
    // Actualizar una suscripción
    public Optional<UsersPlanDto> updateUsersPlan(Long id, UsersPlans updatedPlan)
    
    // Eliminar una suscripción
    public boolean deleteUserPlan(Long id)
    
    // Convertir entidad a DTO con información enriquecida
    private UsersPlanDto convertToDTO(UsersPlans usersPlans)
}
```

**Características:**
- Validación de existencia de usuario y plan antes de crear suscripción
- DTOs enriquecidos con información del usuario y plan
- Métodos de filtrado por estado y usuario
- Operaciones transaccionales con `@Transactional`

---

## ⚠️ Manejo de Errores

### Códigos de Estado HTTP

| Código | Descripción | Cuándo ocurre |
|--------|-------------|---------------|
| 200 OK | Operación exitosa | GET, PUT exitosos |
| 201 Created | Recurso creado | POST exitoso |
| 204 No Content | Eliminación exitosa | DELETE exitoso |
| 400 Bad Request | Datos inválidos | Validación fallida |
| 401 Unauthorized | No autenticado | Token ausente/inválido |
| 403 Forbidden | Sin permisos | Rol insuficiente |
| 404 Not Found | Recurso no existe | ID no encontrado |
| 500 Internal Server Error | Error del servidor | Error inesperado |

### Ejemplos de Errores Comunes

#### 1. Plan no encontrado
```json
{
  "timestamp": "2025-11-12T14:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Plan not found with id: 99",
  "path": "/api/plans/99"
}
```

#### 2. Usuario no autorizado
```json
{
  "timestamp": "2025-11-12T14:30:00.000Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/plans"
}
```

#### 3. Token inválido o expirado
```json
{
  "timestamp": "2025-11-12T14:30:00.000Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token expired or invalid",
  "path": "/api/plans"
}
```

#### 4. Nombre de plan duplicado
```json
{
  "timestamp": "2025-11-12T14:30:00.000Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "could not execute statement; SQL [n/a]; constraint [plans.UK_name]",
  "path": "/api/plans"
}
```

#### 5. Usuario o Plan no existe
```json
{
  "timestamp": "2025-11-12T14:30:00.000Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "User not found with id: 999",
  "path": "/api/users-plans"
}
```

---

## 🧪 Pruebas con Swagger

El módulo de planes está documentado con **OpenAPI/Swagger** y puede ser probado interactivamente.

### Acceso a Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### Endpoints Swagger

- **Plans Controller**: `/api/plans`
- **Users-Plans Controller**: `/api/users-plans`

### Uso de Swagger

1. **Autenticación**
   - Primero hacer login en `/api/auth/login`
   - Copiar el token de la respuesta
   - Click en "Authorize" (botón verde con candado)
   - Ingresar: `Bearer {tu-token}`
   - Click en "Authorize"

2. **Probar Endpoints**
   - Seleccionar el endpoint deseado
   - Click en "Try it out"
   - Completar los parámetros requeridos
   - Click en "Execute"
   - Ver la respuesta

---

## 📈 Flujos de Trabajo

### Flujo 1: Creación de Plan

```
┌─────────┐      ┌────────────┐      ┌─────────┐      ┌──────────┐
│  Admin  │──1──►│ Controller │──2──►│ Service │──3──►│Repository│
└─────────┘      └────────────┘      └─────────┘      └──────────┘
     ▲                                     │                  │
     │                                     │                  │
     └─────────────────6───────────────────┴──────────4───────┘
                                                      │
                                                      ▼
                                                ┌──────────┐
                                                │    DB    │
                                                └──────────┘

1. Admin envía POST /api/plans con datos del plan
2. Controller valida rol ADMIN y envía a Service
3. Service valida datos y llama a Repository
4. Repository guarda en base de datos
5. Se retorna la entidad Plan guardada
6. Se convierte a DTO y retorna al Admin
```

### Flujo 2: Suscripción de Usuario

```
┌──────┐      ┌────────────┐      ┌─────────┐      ┌──────────┐
│ User │──1──►│ Controller │──2──►│ Service │──3──►│UserRepo  │
└──────┘      └────────────┘      └─────────┘      └──────────┘
   ▲                                    │                 │
   │                                    │                 ▼
   │                                    │           ┌──────────┐
   │                                    │       4──►│PlanRepo  │
   │                                    │           └──────────┘
   │                                    │                 │
   │                                    ▼                 │
   │                              ┌──────────┐           │
   │                          5───│UPlanRepo │◄──────────┘
   │                              └──────────┘
   │                                    │
   └────────────────8───────────────────┘
                                        │
                                        ▼
                                  ┌──────────┐
                                  │    DB    │
                                  └──────────┘

1. Usuario envía POST /api/users-plans
2. Controller valida autenticación
3. Service valida que el usuario existe
4. Service valida que el plan existe
5. Se crea la relación UsersPlans
6. Repository guarda en base de datos
7. Se retorna la entidad guardada
8. Se convierte a DTO enriquecido y retorna al usuario
```

### Flujo 3: Consulta de Planes Activos (Público)

```
┌─────────┐      ┌────────────┐      ┌─────────┐      ┌──────────┐
│ Público │──1──►│ Controller │──2──►│ Service │──3──►│Repository│
└─────────┘      └────────────┘      └─────────┘      └──────────┘
     ▲                                     │                  │
     │                                     │                  ▼
     │                                     │            ┌──────────┐
     │                                     │        4───│    DB    │
     │                                     │            └──────────┘
     │                                     │                  │
     │                                     ▼                  │
     │                              ┌──────────┐             │
     │                          5───│List<Plan>│◄────────────┘
     │                              └──────────┘
     │                                     │
     │                              ┌──────────┐
     │                          6───│Convert DTO│
     │                              └──────────┘
     │                                     │
     └─────────────────7───────────────────┘

1. Petición GET /api/plans (sin autenticación)
2. Controller permite acceso público
3. Service solicita planes activos
4. Repository ejecuta findByState("ACTIVE")
5. Retorna lista de planes activos
6. Service convierte a DTOs
7. Controller retorna lista al cliente
```

---

## 📋 Validaciones y Reglas de Negocio

### Validaciones en Plans

| Campo | Regla | Mensaje de Error |
|-------|-------|------------------|
| name | No nulo, único | "Plan name is required and must be unique" |
| maxInstances | Mayor a 0 | "Max instances must be greater than 0" |
| priceIdMercadoPago | No nulo | "Price ID is required" |
| state | "ACTIVE" o "INACTIVE" | Por defecto "ACTIVE" |

### Validaciones en UsersPlans

| Campo | Regla | Mensaje de Error |
|-------|-------|------------------|
| userId | Debe existir en la tabla users | "User not found with id: {id}" |
| planId | Debe existir en la tabla plans | "Plan not found with id: {id}" |
| status | "ACTIVE" o "INACTIVE" | Por defecto "ACTIVE" |
| startDate | Opcional | - |
| endDate | Opcional, debe ser posterior a startDate | - |

### Reglas de Negocio

1. **Planes Únicos**: No pueden existir dos planes con el mismo nombre
2. **Eliminación en Cascada**: Al eliminar un plan, se eliminan todas sus suscripciones
3. **Eliminación en Cascada**: Al eliminar un usuario, se eliminan todas sus suscripciones
4. **Planes Inactivos**: Los planes con estado "INACTIVE" no aparecen en listados públicos
5. **Suscripciones Múltiples**: Un usuario puede tener múltiples suscripciones (activas e inactivas)
6. **Timestamps Automáticos**: createdAt y updatedAt se gestionan automáticamente

---

## 🔐 Configuración de CORS

El módulo de planes tiene configurado CORS para permitir peticiones desde el frontend.

```java
@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanController { ... }

@RestController
@RequestMapping("/api/users-plans")
@CrossOrigin(origins = "*")
public class UsersPlansController { ... }
```

**Nota**: En producción se recomienda especificar los orígenes permitidos explícitamente en lugar de usar `"*"`.

---

## 📦 Dependencias Requeridas

Las siguientes dependencias en `pom.xml` son necesarias para el módulo:

```xml
<!-- JPA y Base de Datos -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Spring Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- OpenAPI/Swagger -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.1.0</version>
</dependency>
```

---

## ⚙️ Configuración

### application.properties

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/crudcloud_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Aplicación
spring.application.name=crudcloud-backend
```

### Configuración de Seguridad

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 📝 Notas Técnicas

### Lazy Loading
Las relaciones `@ManyToOne` en `UsersPlans` utilizan `FetchType.LAZY` para optimizar el rendimiento y evitar cargar datos innecesarios.

### JsonIgnore
La relación `@OneToMany` en `Plan` usa `@JsonIgnore` para evitar recursión infinita en la serialización JSON.

### Transactions
Los métodos de creación, actualización y eliminación en los servicios son transaccionales para garantizar la integridad de los datos.

### Timestamps Automáticos
Los métodos `@PrePersist` y `@PreUpdate` en la entidad `Plan` gestionan automáticamente los timestamps.

### DTOs Enriquecidos
El `UsersPlanDto` incluye información denormalizada del usuario y plan para reducir consultas adicionales en el frontend.

---

## 🎯 Casos de Uso

### 1. Gestión de Planes por Administrador
- Crear planes con diferentes características
- Modificar precios y límites de instancias
- Activar/desactivar planes
- Eliminar planes obsoletos

### 2. Suscripción de Usuarios
- Usuarios consultan planes disponibles
- Usuarios se suscriben a planes específicos
- Usuarios consultan sus planes activos

### 3. Control de Acceso
- Limitar funcionalidades según el plan suscrito
- Validar límites de instancias
- Gestionar vencimientos de suscripciones

### 4. Reportes y Auditoría (Admin)
- Ver todas las suscripciones activas
- Consultar suscripciones inactivas
- Filtrar por usuario o plan
- Generar reportes de uso

---

## 🚀 Mejoras Futuras Sugeridas

### Funcionalidades
- [ ] Paginación en listados
- [ ] Búsqueda y filtros avanzados
- [ ] Historial de cambios en planes
- [ ] Notificaciones de vencimiento
- [ ] Renovación automática de suscripciones
- [ ] Trials gratuitos

### Técnicas
- [ ] Caché con Redis para consultas frecuentes
- [ ] Soft delete en lugar de eliminación física
- [ ] Auditoría completa (quién modificó qué y cuándo)
- [ ] Validación de fechas (endDate > startDate)
- [ ] Eventos de dominio para notificaciones
- [ ] Tests unitarios y de integración

### Integración
- [ ] Integración completa con MercadoPago
- [ ] Webhooks para actualizaciones de pago
- [ ] Sistema de cupones y descuentos
- [ ] Métricas y analytics de uso

---

## 📞 Información de Contacto

Para consultas o reporte de issues relacionados con el módulo de planes, contactar al equipo de desarrollo.