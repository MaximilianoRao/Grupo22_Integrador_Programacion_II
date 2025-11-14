
package Service;

import Dao.UsuarioDAO;
import Entities.Usuario;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import Dao.CredencialAccesoDAO;
import Entities.CredencialAcceso;

/**
 *
 * @author Maximiliano Rao
 * 
 * 
 */

/**
 * Servicio de negocio para la entidad {@link Usuario}.
 *
 * Se encarga de:
 *     Validar los datos obligatorios del usuario (username, email, etc.).
 *     Orquestar la transacción para crear/actualizar usuario.
 *     Llamar al {@link UsuarioDAO} pasándole SIEMPRE la misma conexión para
 *     que se pueda manejar la relación 1→1 con {@code CredencialAcceso} en la
 *     misma transacción.
 *
 * Implementa {@link GenericService} porque así lo pide la consigna del TFI.
 */
public class UsuarioServiceImpl extends AbstractService implements GenericService<Usuario>{
    private final UsuarioDAO usuarioDAO;
    private final CredencialAccesoDAO credencialAccesoDAO;
    
    /**
     * Inyecta el DAO concreto.
     * En el main o en algún factory se le va a pasar la implementación real.
     *
     * @param usuarioDAO DAO de usuario (no debe ser null)
     * @param credencialServiceImpl Servicio de credencial para operaciones coordinadas.
     * @throws IllegalArgumentException si alguna dependencia es null
     */
    public UsuarioServiceImpl(UsuarioDAO usuarioDAO, CredencialAccesoDAO credencialAccesoDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException("usuarioDAO no puede ser null");
        }
        if (credencialAccesoDAO == null) {
            throw new IllegalArgumentException("credencialServiceImpl no puede ser null");
        }
        
        this.usuarioDAO = usuarioDAO;
        this.credencialAccesoDAO = credencialAccesoDAO;
    }
    
    @Override
    public Usuario insertar(Usuario entidad) throws Exception {
        throw new UnsupportedOperationException(
            "Use crearUsuarioConCredencial() para crear usuarios. " +
            "Un usuario siempre debe tener una credencial asociada."
        );
    }
    
    /**
     * Crea un usuario nuevo junto con su credencial.
     * Operación transaccional que garantiza la creación de ambos o ninguno.
     *
     * @param usuario datos del usuario (sin ID)
     * @param credencial datos de la credencial (sin ID)
     * @return usuario creado con ID asignado y credencial asociada
     * @throws Exception si hay error de validación o BD
     */
    public Usuario crearUsuarioConCredencial(Usuario usuario, CredencialAcceso credencial) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);
            // 2. Validaciones
        validarUsuario(usuario);
        validarCredencial(credencial);
        
        if (usuarioDAO.existeUsername(usuario.getUsername(), conn)) {
            throw new IllegalArgumentException("El username ya existe: " + usuario.getUsername());
        }
        if (usuarioDAO.existeEmail(usuario.getEmail(), conn)) {
            throw new IllegalArgumentException("El email ya existe: " + usuario.getEmail());
        }
        
        // 3. Inicializar fechas
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
        }
        if (credencial.getUltimoCambio() == null) {
            credencial.setUltimoCambio(LocalDateTime.now());
        }
        
        // 4. SECUENCIA TRANSACCIONAL (en la MISMA conexión):
        // a) Crear credencial primero
        credencial = credencialAccesoDAO.crear(credencial, conn);
        
        // b) Asignar credencial al usuario (ahora tiene ID)
        usuario.setCredencial(credencial);
        
        // c) Crear usuario
        usuario = usuarioDAO.crear(usuario, conn);
        
        // 5. Commit de TODO junto
        commitTransaction(conn);
        
        return usuario;
        } catch (Exception e) {
        // 6. Rollback de TODO si algo falla
        rollbackTransaction(conn);
        throw new Exception("Error al crear usuario con credencial: " + e.getMessage(), e);
        } finally {
        closeConnection(conn);
        }
        
    }
        
    
    @Override
    public Usuario obtenerPorId(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        Connection conn = null;
        try {
            conn = getConnection();
            return usuarioDAO.leer(id, conn);
            
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return usuarioDAO.leerTodos(conn);

        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public void actualizar(Usuario usuario) throws Exception {
        
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);
            if (usuario.getId() == null || usuario.getId() <= 0) {
                throw new IllegalArgumentException("El usuario debe tener un ID válido para actualizar");
            }
            // Validar la entidad
            validarUsuario(usuario);
            
            // Verificar que existe
            Usuario existente = usuarioDAO.leer(usuario.getId(), conn);
            if (existente == null) {
                throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getId());
            }
            
            // Validar unicidad de username (excepto el mismo usuario)
            Usuario usuarioPorUsername = usuarioDAO.buscarPorUsername(usuario.getUsername(), conn);
            if (usuarioPorUsername != null && !usuarioPorUsername.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("El username ya existe: " + usuario.getUsername());
            }
            
            // Validar unicidad de email (excepto el mismo usuario)
            Usuario usuarioPorEmail = usuarioDAO.buscarPorEmail(usuario.getEmail(), conn);
            if (usuarioPorEmail != null && !usuarioPorEmail.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("El email ya existe: " + usuario.getEmail());
            }
            
            usuarioDAO.actualizar(usuario, conn);
            
            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new Exception("Error al actualizar usuario: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }
    
    
     /**
     * Elimina una credencial de acceso de forma SEGURA actualizando primero la FK del usuario.
     * Este es el método RECOMENDADO para eliminar credenciales.
     *
     * Flujo transaccional SEGURO:
     * 1. Obtiene el usuario por ID y valida que exista
     * 2. Verifica que la credencial pertenezca a ese usuario
     * 3. Desasocia la credencial del usuario
     * 4. Actualiza el usuario en BD (credencial = NULL)
     * 5. Elimina la credencial (ahora no hay FKs apuntando a él)
     *
     *
     *
     * @param usuarioId ID del usuario dueño del domicilio
     * @throws IllegalArgumentException Si los IDs son <= 0, el usuario no existe, o la credencial no pertenece a la persona
     * @throws Exception Si hay error de BD
     */
    @Override
    public void eliminar(Long usuarioId) throws Exception {
        
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);

            if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("Los IDs deben ser mayores a 0");
            }

            Usuario usuario = usuarioDAO.leer(usuarioId, conn);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrada con ID: " + usuarioId);
            }

            if (usuario.getCredencial() == null) {
                throw new IllegalArgumentException("La credencial no pertenece a esta persona");
            }
            Long credencialId = usuario.getCredencial().getId();
            
            // SECUENCIA TRANSACCIONAL: eliminar usuario → eliminar credencial
            // (Ambos soft delete)
            usuarioDAO.eliminar(usuarioId, conn);
            credencialAccesoDAO.eliminar(credencialId,conn);
            
            
            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
    
    /**
     * Busca un usuario por su username.
     *
     * @param username nombre de usuario a buscar
     * @return usuario encontrado o null
     * @throws Exception si hay error en BD
     */
    public Usuario buscarPorUsername(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        
        Connection conn = null;
        try {
            conn = getConnection();
            return usuarioDAO.buscarPorUsername(username, conn);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email correo electrónico a buscar
     * @return usuario encontrado o null
     * @throws Exception si hay error en BD
     */
    public Usuario buscarPorEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        
        Connection conn = null;
        try {
            conn = getConnection();
            return usuarioDAO.buscarPorEmail(email, conn);
        } finally {
            closeConnection(conn);
        }
    }
    
    /**
     * Activa un usuario (cambia activo a true).
     *
     * @param usuarioId ID del usuario a activar
     * @return true si se activó correctamente
     * @throws Exception si hay error
     */
    public void activarUsuario(Long usuarioId) throws Exception {
        cambiarEstadoActivacion(usuarioId, true);
    }

     /**
     * Desactiva un usuario (cambia activo a false).
     *
     * @param usuarioId ID del usuario a desactivar
     * @return true si se desactivó correctamente
     * @throws Exception si hay error
     */
    
    public void desactivarUsuario(Long usuarioId) throws Exception {
        cambiarEstadoActivacion(usuarioId, false);
    }

    /**
     * Cambia el estado de activación de un usuario.
     */
    private void cambiarEstadoActivacion(Long usuarioId, boolean activo) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);
            
            if (usuarioId == null || usuarioId <= 0) {
                throw new IllegalArgumentException("El ID debe ser mayor a 0");
            }
            
            Usuario usuario = usuarioDAO.leer(usuarioId, conn);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
            }
            
            usuario.setActivo(activo);
            usuarioDAO.actualizar(usuario, conn);
            
            
            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new Exception("Error al cambiar estado de activación: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    
     /**
     * Valida los datos de un usuario.
     */
    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        
        // Validar username
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        if (usuario.getUsername().length() > 30) {
            throw new IllegalArgumentException("El username no puede tener más de 30 caracteres");
        }
        // Validar formato de username (solo letras, números y guion bajo)
        if (!usuario.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("El username solo puede contener letras, números y guion bajo");
        }
        
        // Validar email
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (usuario.getEmail().length() > 120) {
            throw new IllegalArgumentException("El email no puede tener más de 120 caracteres");
        }
        if (!validateEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El formato del email es inválido");
        }
        
        // Validar fechaRegistro
        if (usuario.getFechaRegistro() != null) {
        if (usuario.getFechaRegistro().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de registro no puede ser futura");
        }
        }
        
        // Validar credencial (regla 1→1)
        if (usuario.getCredencial() == null) {
            throw new IllegalArgumentException("El usuario debe tener una credencial asociada (relación 1→1)");
        }
    }
    
  
    
}
