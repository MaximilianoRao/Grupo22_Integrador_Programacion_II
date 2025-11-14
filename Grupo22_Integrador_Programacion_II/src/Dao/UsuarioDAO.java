
package Dao;

import java.sql.*;
import Entities.Usuario;
import java.sql.Connection;
import Entities.CredencialAcceso;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Maximiliano Rao
 * Data Access Object para la entidad Usuario.
 * Gestiona todas las operaciones de persistencia de Usuario en la base de datos.
 *
 * Características:
 * - Implementa GenericDAO<Usuario> para operaciones CRUD estándar
 * - Usa PreparedStatements en TODAS las consultas (protección contra SQL injection)
 * - Implementa soft delete (eliminado=TRUE, no DELETE físico)
 * - Proporciona búsquedas especializadas 
 *
 * Patrón: DAO con try-with-resources para manejo automático de recursos JDBC
 * 
 */
public class UsuarioDAO implements GenericDAO<Usuario> {
     /**
     * Query de inserción de Usuario.
     * Inserta username, email, activo, fechaRegistro, eliminado y FK credencial.
     * El id es AUTO_INCREMENT y se obtiene con RETURN_GENERATED_KEYS.
     */
    private static final String INSERT_SQL = "INSERT INTO Usuarios (username, email, activo, fechaRegistro, credencial, eliminado) VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * Query de actualización de Usuario.
     * Actualiza username, email, activo, fechaRegistro y FK credencial por id.
     * NO actualiza el flag eliminado (solo se modifica en soft delete).
     */
    private static final String UPDATE_SQL = "UPDATE Usuarios SET username = ?, email = ?, activo = ?, fechaRegistro = ?, credencial = ? WHERE id = ?";

    /**
     * Query de soft delete.
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     * Preserva integridad referencial y datos históricos.
     */
    private static final String DELETE_SQL = "UPDATE Usuarios SET eliminado = TRUE WHERE id = ?";
    
    
    // Queries con JOIN para traer toda la información
    private static final String SELECT_BASE = 
        "SELECT u.id AS usuario_id, u.username, u.email, u.activo, u.fechaRegistro, " +
        "u.eliminado AS usuario_eliminado, " +
        "c.id AS credencial_id, c.hashPassword, c.salt, c.ultimoCambio, " +
        "c.requiereReset, c.eliminado AS credencial_eliminado " +
        "FROM Usuarios u " +
        "JOIN CredencialAcceso c ON u.credencial = c.id ";

    /**
     * Query para obtener Usuario por ID.
     * 
     * Solo retorna usuarios activas (eliminado=FALSE).
     *
     * Campos del ResultSet:
     * - Persona: id, nombre, apellido, dni, domicilio_id
     * - Domicilio (puede ser NULL): dom_id, calle, numero
     */
    private static final String SELECT_BY_ID_SQL = SELECT_BASE + "WHERE u.id = ? AND u.eliminado = false";;

    /**
     * Query para obtener todos los usuarios activos.
     * Filtra por eliminado=FALSE
     */
    private static final String SELECT_ALL_SQL = SELECT_BASE + "WHERE u.eliminado = false";

    /**
     * Query de búsqueda exacta por username.
     * Solo usuarios activos (eliminado=FALSE).
     */
    private static final String SEARCH_BY_USERNAME_SQL = SELECT_BASE + "WHERE u.username = ? AND u.eliminado = false";

    /**
     * Query de búsqueda por email con LIKE.
     * Permite búsqueda flexible.
     * Usa % antes y después del filtro: LIKE '%filtro%'
     * Solo usuarios activos (eliminado=FALSE).
     */
    private static final String SEARCH_BY_EMAIL_SQL = SELECT_BASE + "WHERE u.email = ? AND u.eliminado = false";
    /**
     * Query que cuenta la cantidad de coincidencias exactas con username
     * Solo usuarios activos (eliminado=FALSE).
     */
    private static final String SELECT_COUNT_USERNAME = "SELECT COUNT(*) FROM Usuarios WHERE username = ? AND eliminado = false";
    
    /**
     * Query que cuenta la cantidad de coincidencias exactas con EMAIL
     * Solo usuarios activos (eliminado=FALSE).
    */
    private static final String SELECT_COUNT_EMAIL = "SELECT COUNT(*) FROM Usuarios WHERE email = ? AND eliminado = false";
    

    /**
     * DAO de credencial de acceso (actualmente no usado, pero disponible para operaciones futuras).
     * Inyectado en el constructor por si se necesita coordinar operaciones.
     */
    private final CredencialAccesoDAO credencialDAO;

    /**
     * Constructor con inyección de DomicilioDAO.
     * Valida que la dependencia no sea null (fail-fast).
     *
     * @param credencialDAO DAO de CredencialAcceso
     * @throws IllegalArgumentException si credencialDAO es null
     */
    public UsuarioDAO(CredencialAccesoDAO credencialDAO) {
        if (credencialDAO == null) {
            throw new IllegalArgumentException("CredencialAccesoDAO no puede ser null");
        }
        this.credencialDAO = credencialDAO;
    }
    
    /**
     * Inserta un usuario dentro de una transacción existente.
     *
     *
     * @param usuario Usuario a insertar
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @throws Exception Si falla la inserción
     */

    @Override
    public Usuario crear(Usuario usuario, Connection conn) throws Exception {
         try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getEmail());
            stmt.setBoolean(3, usuario.isActivo());
            stmt.setTimestamp(4, Timestamp.valueOf(usuario.getFechaRegistro()));
            stmt.setLong(5, usuario.getCredencial().getId()); // FK a CredencialAcceso
            stmt.setBoolean(6, usuario.isEliminado());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("Error al crear Usuario, ninguna fila afectada.");
            }
            
            // Obtener el ID generado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Error al crear Usuario, no se obtuvo el ID.");
                }
            }
        }
        
        return usuario;
    }

     /**
     * Actualiza un usuario existente en la base de datos.
     * Actualiza username, email, activo, fechaRegistro, FK credencial.
     *
     * Validaciones:
     * - Si rowsAffected == 0 → El usuario no existe o ya está eliminada
     *
    
     *
     * @param usuario Usuario con los datos actualizados (id debe ser > 0)
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @throws SQLException Si la persona no existe o hay error de BD
     */
    
    @Override
    public void actualizar(Usuario usuario, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getEmail());
            stmt.setBoolean(3, usuario.isActivo());
            stmt.setTimestamp(4, Timestamp.valueOf(usuario.getFechaRegistro()));
            stmt.setLong(5, usuario.getCredencial().getId());
            stmt.setLong(6, usuario.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el usuario con ID: " + usuario.getId());
            }
        }
    }

    /**
     * Elimina lógicamente un usuario (soft delete).
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     *
     * Validaciones:
     * - Si rowsAffected == 0 → El usuario no existe o ya está eliminada
     *
     *
     * @param id ID de la persona a eliminar
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @throws SQLException Si la persona no existe o hay error de BD
     */
    
    @Override
    public void eliminar(Long id, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró usuario con ID: " + id);
            }
        }
    }

    /**
     * Obtiene un usuario por su ID.
     * Incluye su credencial de acceso asociada.
     *
     * @param id ID del usuario a buscar
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @return Usuario encontrada con su credencial, o null si no existe o está eliminada
     * @throws Exception Si hay error de BD (captura SQLException y re-lanza con mensaje descriptivo)
     */
    @Override
    public Usuario leer(Long id, Connection conn) throws Exception {
          try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        
         }catch (SQLException e) {
            throw new Exception("Error al obtener persona por ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios activos (eliminado=FALSE).
     * Incluye sus credenciales.
     *
     * Nota: Usa Statement (no PreparedStatement) porque no hay parámetros.
     *
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @return Lista de usuarios activos con sus credenciales
     * @throws Exception Si hay error de BD
     */
    @Override
    public List<Usuario> leerTodos(Connection conn) throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapearResultSet(rs));
            }
        }catch (SQLException e) {
            throw new Exception("Error al obtener todos los usuarios: " + e.getMessage(), e);
        }
        
        return usuarios;
    }
    
    /**
     * Busca usuarios por username con búsqueda flexible (LIKE).
     * 
     *
     * Patrón de búsqueda: LIKE '%filtro%' en username
     * Búsqueda case-sensitive en MySQL (depende de la collation de la BD).
     *
     *
     * @param username Username a buscar (no puede estar vacío)
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @return Usuario con ese username, o null si no existe o está eliminada
     * @throws IllegalArgumentException Si el filtro está vacío
     * @throws SQLException Si hay error de BD
     */
    public Usuario buscarPorUsername(String username, Connection conn) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El filtro de búsqueda no puede estar vacío");
        }

        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_USERNAME_SQL)) {

            stmt.setString(1, username.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   return mapearResultSet(rs);
                }
            }
        }
        return null;
    }
    
    
      /**
     * Busca un usuario por su email (único).
     *
     * @param email correo electrónico a buscar
     * @param conn conexión JDBC
     * @return usuario encontrado o null si no existe
     * @throws SQLException si ocurre un error
     */
    public Usuario buscarPorEmail(String email, Connection conn) throws SQLException {
        
        
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_EMAIL_SQL)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Mapea un ResultSet a un objeto Usuario.
     * Carga la credencial asociada usando CredencialAccesoDAO.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @param conexion Connection para cargar la credencial
     * @return objeto Usuario construido con su credencial
     * @throws SQLException si hay error al leer el ResultSet o cargar la credencial
    */
    
    private Usuario mapearResultSet(ResultSet rs) throws SQLException {
        
        CredencialAcceso credencial = new CredencialAcceso(
            rs.getString("hashPassword"),
            rs.getString("salt"),
            rs.getTimestamp("ultimoCambio").toLocalDateTime(),
            rs.getBoolean("requiereReset"),
            rs.getBoolean("credencial_eliminado"),
            rs.getLong("credencial_id")
        );
        
        // Mapear Usuario con su credencial
        Usuario usuario = new Usuario(
            rs.getLong("usuario_id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getBoolean("activo"),
            rs.getTimestamp("fechaRegistro").toLocalDateTime(),
            credencial,
            rs.getBoolean("usuario_eliminado")
        );
        
        
        return usuario;
    }
    
     /**
     * Verifica si existe un usuario con el username dado.
     *
     * @param username nombre de usuario a verificar
     * @param conexion conexión JDBC
     * @return true si existe, false en caso contrario
     * @throws SQLException si ocurre un error
     */
    public boolean existeUsername(String username, Connection conexion) throws SQLException {
        
        try (PreparedStatement stmt = conexion.prepareStatement(SELECT_COUNT_USERNAME)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
     /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email correo a verificar
     * @param conexion conexión JDBC
     * @return true si existe, false en caso contrario
     * @throws SQLException si ocurre un error
     */
    public boolean existeEmail(String email, Connection conexion) throws SQLException {
        
        
        try (PreparedStatement stmt = conexion.prepareStatement(SELECT_COUNT_EMAIL)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

}
