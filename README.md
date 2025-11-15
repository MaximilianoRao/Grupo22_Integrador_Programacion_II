# TECNICATURA UNIVERSITARIA EN PROGRAMACIÓN PROGRAMACIÓN II

### TRABAJO FINAL INTEGRADOR

#### Sistema de Gestión de Usuarios y Credenciales de Acceso

**Integrantes:**
**- Barandiarán, Francisco**
**- De Inocenti, Alfredo**
**- Olivera, Favio**
**- Rao, Maximiliano**


### Descripción del Proyecto

Este Trabajo Práctico Integrador tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos aprendidos durante el cursado de Programación 2. El proyecto consiste en desarrollar un sistema completo de gestión de usuarios y credenciales de acceso que permita realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre estas entidades, implementando una arquitectura robusta y profesional.


## 1. Dominio y justificación
**Dominio:** Gestión de usuarios y credenciales (base de cualquier login/registro).
**Motivos:**
- Relevancia práctica (web, mobile, empresariales).
- Complejidad didáctica adecuada: relación 1→1, soft delete, transacciones, validaciones (unicidad).
- Enfoque en seguridad (hash + salt, reset, auditoría básica).
- Escalable a roles/permisos, sesiones, OAuth/JWT.

**Casos de uso:** alta de usuario con credencial, autenticación (validación), cambio de contraseña, activar/desactivar, eliminación lógica, consultas de auditoría.<br>
**Alcance acotado:** sin cifrado real (hash simulado), sin tokens/sesiones, sin roles ni recuperación por email.

## 2. Decisiones de Diseño

### 2.1 Relación 1→1 Usuario–CredencialAcceso (FK única)
- **Racional:** 1 usuario ↔ 1 credencial; tablas independientes; orden natural de creación (credencial→usuario); soft delete por separado; lectura legible mediante FK única (no PK compartida).<br>
- **Trade-off:** requiere JOIN para vistas completas.

#### Esquema (resumen)
```sql
CredencialAcceso(id PK, hashPassword, salt, ultimoCambio, requiereReset, eliminado)
Usuarios(id PK, username UNIQUE, email UNIQUE, activo, fechaRegistro, credencial UNIQUE FK→CredencialAcceso(id), eliminado)
```

### 2.2 Soft delete (vs hard delete)
- **Elegido:** `eliminado=TRUE`en Usuario y Credencial (preserva histórico, permite auditoría, evita cascadas destructivas).<br>
- **Base común:** clase `Base { id, eliminado }`.

### 2.3 Validaciones de datos (resumen)
- `username`: único, ≤30, alfanumérico/_
- `email`: único, ≤120, formato válido
- `hashPassword`: ≤255
- `salt`: ≤64
- Regla 1→1 obligatoria (usuario siempre con credencial válida).

### 2.4 Orden de Operaciones Crítico
**Crear usuario**: (1) crear credencial → (2) asignar FK → (3) crear usuario → (4) commit.

**Eliminar usuario**: marcar eliminado en Usuario y en su Credencial (misma transacción).

## 3. Arquitectura del Sistema

### Representación Gráfica
```
═══════════════════════════════════════════════════
  🖥️  CAPA 4: PRESENTACIÓN (Main)
  ▪ Interacción con usuario
  ▪ Validación de formato de entrada
═══════════════════════════════════════════════════
                    ⬇️
═══════════════════════════════════════════════════
  🧠  CAPA 3: LÓGICA DE NEGOCIO (Service)
  ▪ Validaciones de negocio
  ▪ Gestión de transacciones
═══════════════════════════════════════════════════
                    ⬇️
═══════════════════════════════════════════════════
  🗄️  CAPA 2: ACCESO A DATOS (DAO)
  ▪ Ejecución de queries SQL
  ▪ Mapeo ResultSet → Objetos
═══════════════════════════════════════════════════
                    ⬇️
═══════════════════════════════════════════════════
  📦  CAPA 1: MODELOS (Entities)
  ▪ Representación de entidades del dominio
  ▪ Relaciones entre objetos
═══════════════════════════════════════════════════
                    ⬇️
            💾  MySQL Database
```

## 4. Gestión de Persistencia y transacciones

**Restricciones implementadas:**
- `UNIQUE` en username, email, credencial (garantiza 1→1)
- `NOT NULL` en campos obligatorios
- `DEFAULT` para campos con valores iniciales
- `ON DELETE CASCADE` NO usado (soft delete manual)

**Transaccionalidad (patrón)**: begin → operaciones (misma conexión) → commit; ante error → rollback.

**Seguridad SQL**: todo con PreparedStatement (evita inyecciones).<br>
**Conexión**: DatabaseConnection carga el driver y expone getConnection(); try-with-resources en operaciones no transaccionales.

## 5. Reglas de Negocio Principales
- **RN-001: Unicidad de Username**
- **RN-002: Unicidad de Email**
- **RN-003: Relación 1→1 Obligatoria**
- **RN-004: No Eliminar Credencial en Uso**
- **RN-005: Cambio de Contraseña Resetea Flag**
- **RN-006: Soft Delete en Cascada**

## 6. Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 17 o superior |
| MySQL | 8.0 o superior |
| Sistema Operativo | Windows, Linux o macOS |

## 7. Instalación

### 1. Configurar Conexión

Por defecto conecta a:
- **Host**: localhost:3306
- **Base de datos**: dbtpi3
- **Usuario**: root
- **Contraseña**: 123456

### 2. Configurar Base de Datos

Ejecutar el siguiente script SQL de creación de la base de datos:
`01_PerfilSeguridad.sql`
Cargar datos de prueba en base de datos con el siguiente script SQL:
`02_PerfilSeguridad_datos.sql`

### 3 Ejecución

### Desde IDE
1. Abrir proyecto en IntelliJ IDEA o Eclipse
2. Ejecutar clase `Main.Main`

## Verificar Conexión
### Desde IDE
1. Abrir proyecto en IntelliJ IDEA o Eclipse
2. Ejecutar clase `Main.TestConexion`

Salida esperada:
```
✅ Conexión establecida con Éxito.
```

## 8. Uso del Sistema

### Menú Principal
Seleccionar la opción deseada del menu principal y completar los datos solicitados en caso de que corresponda, para salir se debe precionar 0.

```
========= MENU =========
| 1.  Crear Usuario con Credencial       │
│ 2.  Listar todos los Usuarios          │
│ 3.  Buscar Usuario por ID              │
│ 4.  Buscar Usuario por Username        │
│ 5.  Buscar Usuario por Email           │
│ 6.  Actualizar Usuario                 │
│ 7.  Eliminar Usuario                   │
│ 8.  Activar Usuario                    │
│ 9.  Desactivar Usuario                 |
│ 10. Crear Credencial (independiente)   │
│ 11. Listar todas las Credenciales      │
│ 12. Buscar Credencial por ID           │
│ 13. Actualizar Credencial              │
│ 14. Eliminar Credencial                │
│ 15. Cambiar Password de Credencial     │
| 0.  Salir                              | 

```
## 9. Conceptos de Programación 2 Demostrados

| Concepto | Implementación en el Proyecto |
|----------|-------------------------------|
| **Herencia** | Clase abstracta `Base` heredada por `Usuario` y `CredencialAcceso` |
| **Polimorfismo** | Interfaces `GenericDAO<T>` y `GenericService<T>` |
| **Encapsulamiento** | Atributos privados con getters/setters en todas las entidades |
| **Abstracción** | Interfaces que definen contratos sin implementación |
| **JDBC** | Conexión, PreparedStatements, ResultSets, transacciones |
| **DAO Pattern** | `UsuarioDAO`, `CredencialAccesoDAO` abstraen el acceso a datos |
| **Service Layer** | Lógica de negocio separada en `UsuarioServiceImpl`, `CredencialAccesoServiceImpl` |
| **Exception Handling** | Try-catch en todas las capas, propagación controlada |
| **Resource Management** | Try-with-resources para AutoCloseable (Connection, Statement, ResultSet) |

## 10. Referencias y Recursos Utilizados

### 10.1 Herramientas Utilizadas

**Desarrollo:**
- **IDE:** NetBeans

**Base de Datos:**
- **Cliente:** MySQL Workbench

**Control de Versiones:**
- **Git:** Git
- **GitHub:** Repositorio del proyecto (https://github.com/MaximilianoRao/Grupo22_Integrador_Programacion_II)

**Diagramación:**
- **UML:** UMLetino

**Documentación:**
- **Markdown:** Para README.md

### 10.2 Uso de Inteligencia Artificial

**Declaración de Transparencia:**

En el desarrollo de este proyecto se utilizó **Claude AI (Anthropic)** y **ChatGPT (OpenAI):** como 
herramienta de asistencia para:

1. **Consultas técnicas específicas:**
   - Sintaxis de JDBC
   - Mejores prácticas de manejo de excepciones

2. **Revisión de código:**
   - Detección de posibles SQL injection
   - Sugerencias de optimización

3. **Documentación:**
   - Redacción de este informe (organización y claridad)
   - Ejemplos de código para explicaciones

**Otras herramientas IA consultadas:**
- **GitHub Copilot:** Sugerencias de autocompletado (desactivado para lógica crítica)


## 11. Enlace video explicativo


---

**Versión**: 1.0
**Java**: 17+
**MySQL**: 8.x
**Proyecto Educativo** - Trabajo Práctico Integrador de Programación 2
