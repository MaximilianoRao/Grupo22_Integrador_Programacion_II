
package Service;

import Dao.UsuarioDAO;
import Entities.Usuario;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

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
    
    /**
     * Inyecta el DAO concreto.
     * En el main o en algún factory se le va a pasar la implementación real.
     *
     * @param usuarioDAO DAO de usuario (no debe ser null)
     */
    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }
    
    @Override
    public Usuario insertar(Usuario usuario) throws Exception {
        // ====== VALIDACIONES DE NEGOCIO ======
        validateNotNull(usuario, "usuario");
        validateNotEmpty(usuario.getUsername(), "username");
        validateEmail(usuario.getEmail());

        // seteo automático de la fecha de registro
        usuario.setFechaRegistro(LocalDateTime.now());

        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);

            // TODO: cuando esté el DAO:
            // Usuario creado = usuarioDAO.crear(usuario, conn);

            // por ahora devolvemos el mismo usuario para compilar
            Usuario creado = usuario;

            commitTransaction(conn);
            return creado;
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public Usuario obtenerPorId(Long id) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // TODO: return usuarioDAO.leer(id, conn);
            return null;
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // TODO: return usuarioDAO.leerTodos(conn);
            return List.of();
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public void actualizar(Usuario usuario) throws Exception {
        validateNotNull(usuario, "usuario");
        validateNotNull(usuario.getId(), "id de usuario");

        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);

            // TODO: usuarioDAO.actualizar(usuario, conn);

            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public void eliminar(Long id) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);

            // TODO: usuarioDAO.eliminar(id, conn);

            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
}
