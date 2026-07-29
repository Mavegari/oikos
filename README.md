# Oikos

API REST para la gestión de finanzas personales, diseñada como sistema multiplataforma: un único backend que sirve datos a clientes de móvil, web y escritorio.

El nombre viene del griego *oîkos* (οἶκος), "hogar" o "administración del hogar", raíz de la palabra *economía*.

> **Estado del proyecto:** en desarrollo activo. El backend con autenticación y el núcleo financiero (cuentas, categorías y transacciones) está implementado y probado. Los clientes y el despliegue en la nube están planificados. Ver [Estado actual](#estado-actual) para el detalle de qué está hecho y qué no.

---

## Visión

Oikos nace de una idea sencilla: tener las finanzas personales sincronizadas en cualquier dispositivo, con un backend propio como única fuente de verdad. La lógica de negocio vive centralizada en la API, y los clientes (móvil, web y escritorio) son consumidores ligeros que comparten los mismos datos.

Un principio de diseño recorre todo el proyecto: **los datos derivados no se almacenan, se calculan**. El saldo de una cuenta, por ejemplo, nunca se guarda como un campo; se obtiene sumando sus transacciones en el momento de consultarlo. Esto garantiza que el saldo siempre sea coherente con los movimientos reales.

---

## Estado actual

### Implementado

- **Autenticación y autorización** con JWT (registro, login, tokens firmados con HS256).
- **Gestión de usuarios** con contraseñas hasheadas mediante BCrypt.
- **Cuentas** (efectivo, banco, tarjeta) con saldo calculado dinámicamente.
- **Categorías** de ingreso y gasto.
- **Transacciones** con relaciones a cuenta y categoría, importes con precisión monetaria y cálculo de saldo en tiempo real.
- **Autorización a nivel de recurso**: cada usuario solo accede a sus propios datos, garantizado en la capa de consulta.
- **Manejo centralizado de errores** que traduce excepciones a respuestas HTTP con el código y el formato adecuados.
- **Entorno reproducible** con Docker para la base de datos y gestión de credenciales por variables de entorno.

### Planificado

- **Presupuestos** (límite por categoría y mes, con alertas al superarlo).
- **Panel de resumen** con datos agregados para visualizaciones (gasto por categoría, evolución mensual, balance global).
- **Clientes multiplataforma**: aplicación Android (Kotlin / Jetpack Compose), web (React + TypeScript) y escritorio (Compose Multiplatform).
- **Despliegue en la nube** con demo pública en vivo.

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4 (Spring Web, Spring Data JPA, Spring Security, Validation) |
| Base de datos | PostgreSQL 17 |
| Autenticación | JSON Web Tokens (JJWT) + BCrypt |
| Contenedores | Docker / Docker Compose |
| Build | Maven (con wrapper incluido) |

---

## Arquitectura

El backend sigue una organización por dominios, y dentro de cada dominio, una separación por capas:

- **Controller** — recibe las peticiones HTTP, valida el formato y delega. Sin lógica de negocio.
- **Service** — la lógica de negocio: reglas, validaciones de propiedad y cálculos.
- **Repository** — el acceso a datos mediante Spring Data JPA.
- **Entity** — el modelo de dominio mapeado a las tablas.
- **DTO** — los objetos de entrada y salida de la API, separados de las entidades para no exponer datos internos (como los hashes de contraseña) y para dar a cada cliente exactamente lo que necesita.

### Modelo de datos

```
User (1) ──< (N) Account
User (1) ──< (N) Category
User (1) ──< (N) Transaction

Account  (1) ──< (N) Transaction
Category (1) ──< (N) Transaction
```

Cada entidad de negocio pertenece a un usuario. Una transacción conecta una cuenta y una categoría, y su importe se almacena siempre en positivo: el tipo (ingreso o gasto) determina si suma o resta al saldo. Los saldos negativos —deudas o números rojos— emergen de forma natural del cálculo `ingresos − gastos`, sin necesidad de almacenar importes negativos.

---

## Puesta en marcha (local)

### Requisitos previos

- **Java 17** o superior (JDK).
- **Docker Desktop** en ejecución.
- No es necesario instalar PostgreSQL ni Maven por separado: la base de datos corre en un contenedor y Maven se ejecuta mediante el wrapper incluido (`mvnw`).

### 1. Clonar el repositorio

```bash
git clone https://github.com/Mavegari/oikos.git
cd oikos
```

### 2. Configurar las variables de entorno

El proyecto lee las credenciales de un archivo `.env` que **no se incluye en el repositorio** por seguridad. Se proporciona una plantilla en `.env.example`. Cópiala y rellénala:

```bash
cp .env.example .env
```

Edita el `.env` con tus valores. Para desarrollo local puedes usar:

```
POSTGRES_DB=oikos
POSTGRES_USER=oikos_user
POSTGRES_PASSWORD=oikos_pass
JWT_SECRET=<una_clave_larga_y_aleatoria_en_base64>
```

Para generar una clave JWT segura (256 bits):

```bash
openssl rand -base64 32
```

### 3. Levantar la base de datos

```bash
docker compose up -d
```

Esto arranca un contenedor de PostgreSQL con la configuración del `.env`. Puedes comprobar que está en marcha con `docker ps`.

### 4. Arrancar la aplicación

En Windows:

```bash
.\mvnw.cmd spring-boot:run
```

En Linux / macOS:

```bash
./mvnw spring-boot:run
```

La primera vez descargará las dependencias. Cuando veas `Started FinanceApplication`, la API estará disponible en `http://localhost:8080`.

Al arrancar, el esquema de la base de datos se crea automáticamente a partir de las entidades.

---

## Uso de la API

Todos los endpoints, salvo el registro y el login, requieren un token JWT en la cabecera `Authorization: Bearer <token>`.

### Autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/auth/register` | Registrar un nuevo usuario |
| `POST` | `/api/auth/login` | Iniciar sesión y obtener el token |

### Recursos (requieren autenticación)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET`, `POST` | `/api/accounts` | Listar / crear cuentas |
| `GET`, `PUT`, `DELETE` | `/api/accounts/{id}` | Ver / editar / eliminar una cuenta |
| `GET`, `POST` | `/api/categories` | Listar / crear categorías |
| `GET`, `PUT`, `DELETE` | `/api/categories/{id}` | Ver / editar / eliminar una categoría |
| `GET`, `POST` | `/api/transactions` | Listar / crear transacciones |
| `GET`, `PUT`, `DELETE` | `/api/transactions/{id}` | Ver / editar / eliminar una transacción |

### Ejemplo: registro

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@ejemplo.com", "password": "contraseña123"}'
```

### Ejemplo: login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@ejemplo.com", "password": "contraseña123"}'
```

Devuelve un token que se usa en el resto de peticiones.

---

## Licencia

Proyecto personal con fines de aprendizaje y portfolio.
