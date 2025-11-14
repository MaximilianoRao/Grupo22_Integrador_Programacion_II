
package Dao;


import Entities.CredencialAcceso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Maximiliano Rao
 * 
 * **
 * Data Access Object para la entidad CredencialAcceso.
 * Gestiona todas las operaciones de persistencia de credenciales en la base de datos.
 *
 * Características:
 * - Implementa GenericDAO<CredencialAcceso> para operaciones CRUD estándar
 * - Usa PreparedStatements en TODAS las consultas (protección contra SQL injection)
 * - Implementa soft delete (eliminado=TRUE, no DELETE físico)
 * - NO maneja relaciones (CredencialAcceso es entidad independiente)
 * - Soporta transacciones mediante insertTx() (recibe Connection externa)
 *
 * Diferencias con UsuarioDAO:
 * - Más simple: NO tiene LEFT JOINs (CredencialAcceso no tiene relaciones cargadas)
 * - NO tiene búsquedas especializadas (solo CRUD básico)
 * - Todas las queries filtran por eliminado=FALSE (soft delete)
 *
 * Patrón: DAO con try-with-resources para manejo automático de recursos JDBC
 */
public class CredencialAccesoDAO implements GenericDAO<CredencialAcceso>{
/**
     * Query de inserción de credencial de acceso.
     * Inserta hash, salt, ultimoCambio requiereReset, eliminado
     * El id es AUTO_INCREMENT y se obtiene con RETURN_GENERATED_KEYS.
     *
     */
    private static final String INSERT_SQL = "INSERT INTO credencialacceso (hashPassword, salt, ultimoCambio, requiereReset, eliminado) VALUES (?, ?, ?, ?, ?)";

    /**
     * Query de actualización de credencial de acceso.
     * Actualiza todos los campos por id.
     * NO actualiza el flag eliminado (solo se modifica en soft delete).
     *
     * 
     */
    private static final String UPDATE_SQL = "UPDATE credencialacceso SET hashPassword = ?, salt = ?, ultimoCambio = ?, requiereReset = ?  WHERE id = ?";

    /**
     * Query de soft delete.
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     * Preserva integridad referencial y datos históricos.
     *
     * ⚠️ PELIGRO: Este método NO verifica si hay usuarios asociadas.
     * Puede dejar FKs huérfanas
     * 
     */
    private static final String DELETE_SQL = "UPDATE credencialacceso SET eliminado = TRUE WHERE id = ?";

    /**
     * Query para obtener credencial de acceso por ID.
     * Solo retorna credenciales activas (eliminado=FALSE).
     * 
     */
    private static final String SELECT_BY_ID_SQL = "SELECT id, eliminado, hashPassword, salt, ultimoCambio, requiereReset FROM credencialacceso WHERE id = ? AND eliminado = FALSE";

    /**
     * Query para obtener todos las credenciales activas.
     * Solo retorna credenciales activas (eliminado=FALSE).
     * 
     */
    private static final String SELECT_ALL_SQL = "SELECT id, eliminado, hashPassword, salt, ultimoCambio, requiereReset FROM credencialacceso WHERE eliminado = FALSE";
    
    /**
     * Query busqueda exacta de una credencial y cuenta las ocurrencias.
     * Solo cuenta credenciales activas (eliminado=FALSE).
     * 
     */
    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM Usuarios WHERE credencial = ? AND eliminado = false";

     /**
     * Inserta una Credencial de acceso dentro de una transacción existente.
     *
     *
     * @param credencial CredencialAcceso a insertar
     * @param conn Conexión transaccional (NO se cierra en este método)
     * @throws Exception Si falla la inserción
     */
    @Override
    public CredencialAcceso crear(CredencialAcceso credencial, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setCredencialParameters(stmt, credencial);
            stmt.executeUpdate();
            setGeneratedId(stmt, credencial);
        }
        
        return credencial;
    }
    
    /**
     * Actualiza una credencial existente en la base de datos.
     * 
     *
     * Validaciones:
     * - Si rowsAffected == 0 → La credencial no existe o ya está eliminado
     *
     *
     * @param credencial CredencialAcceso con los datos actualizados (id debe ser > 0)
     * @throws SQLException Si la credencial no existe o hay error de BD
     */

    @Override
    public void actualizar(CredencialAcceso credencial, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
        
            stmt.setString(1, credencial.getHashPassword());
            stmt.setString(2, credencial.getSalt());
            stmt.setTimestamp(3, Timestamp.valueOf(credencial.getUltimoCambio()));
            stmt.setBoolean(4, credencial.isRequiereReset());
            stmt.setLong(5, credencial.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se pudo actualizar la credencial de acceso con ID: " + credencial.getId());
            }
        }
    }

    /**
     * Elimina lógicamente una credencial de acceso (soft delete).
     * Marca eliminado=TRUE sin borrar físicamente la fila.
     *
     * Validaciones:
     * - Si rowsAffected == 0 → La credencial no existe o ya está eliminado
     *
     * ⚠️ PELIGRO: Este método NO verifica si hay usuarios asociadas.
     *
     *
     * ALTERNATIVA SEGURA: UsuarioServiceImpl.eliminarCredencialDeUsuario()
     *
     * Este método se mantiene para casos donde:
     * - Se está seguro de que la credencial NO tiene personas asociadas
     * - Se quiere eliminar credenciales en lotes (administración)
     *
     * @param id ID de la credencial a eliminar
     * @throws SQLException Si la credencial no existe o hay error de BD
     */
    @Override
    public void eliminar(Long id, Connection conn) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró credencia de acceso con ID: " + id);
            }
        }
    }

     /**
     * Obtiene un credencia de acceso por su ID.
     * Solo retorna credecn activos (eliminado=FALSE).
     *
     * @param id ID de la credencia de acceso a buscar
     * @return CredencialAcceso encontrada, o null si no existe o está eliminado
     * @throws SQLException Si hay error de BD
     */
    @Override
    public CredencialAcceso leer(Long id, Connection conn) throws Exception {
         try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Obtiene todos las credenciales activas (eliminado=FALSE).
     *
     * Nota: Usa Statement (no PreparedStatement) porque no hay parámetros.
     *
     *
     * @return Lista de credenciales de acceso activas (puede estar vacía)
     * @throws SQLException Si hay error de BD
     */

    @Override
    public List<CredencialAcceso> leerTodos(Connection conn) throws Exception {
        List<CredencialAcceso> credenciales = new ArrayList<>();
          try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            
            while (rs.next()) {
                credenciales.add(mapearResultSet(rs));
            }
        }
        
        return credenciales;
    }
    
     /**
     * Setea los parámetros de CredencialAcceso en un PreparedStatement.
     * Método auxiliar usado por crear()
     *
     * Parámetros seteados:
     * 1. calle (String)
     * 2. numero (String)
     *
     * @param stmt PreparedStatement con INSERT_SQL
     * @param domicilio Domicilio con los datos a insertar
     * @throws SQLException Si hay error al setear parámetros
     */
    private void setCredencialParameters(PreparedStatement stmt, CredencialAcceso credencial) throws SQLException {
         stmt.setString(1, credencial.getHashPassword());
         stmt.setString(2, credencial.getSalt());
         stmt.setTimestamp(3, Timestamp.valueOf(credencial.getUltimoCambio()));
         stmt.setBoolean(4, credencial.isRequiereReset());
         stmt.setBoolean(5, credencial.isEliminado());
    }

    /**
     * Obtiene el ID autogenerado por la BD después de un INSERT.
     * Asigna el ID generado al objeto CredencialAcceso.
     *
     * IMPORTANTE: Este método es crítico para mantener la consistencia:
     * - Después de insertar, el objeto CredencialAcceso debe tener su ID real de la BD
     
     *
     * @param stmt PreparedStatement que ejecutó el INSERT con RETURN_GENERATED_KEYS
     * @param credencial Objeto CredencialAcceso a actualizar con el ID generado
     * @throws SQLException Si no se pudo obtener el ID generado (indica problema grave)
     */
    private void setGeneratedId(PreparedStatement stmt, CredencialAcceso credencial) throws SQLException {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                credencial.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La inserción de la credencial de acceso falló, no se obtuvo ID generado");
            }
        }
    }
    
     /**
     * Mapea un ResultSet a un objeto CredencialAcceso.
     * Método privado auxiliar para evitar duplicación de código.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @return objeto CredencialAcceso construido
     * @throws SQLException si hay error al leer el ResultSet
     */
    private CredencialAcceso mapearResultSet(ResultSet rs) throws SQLException {
        return new CredencialAcceso(
            rs.getString("hashPassword"),
            rs.getString("salt"),
            rs.getTimestamp("ultimoCambio").toLocalDateTime(),
            rs.getBoolean("requiereReset"),
            rs.getBoolean("eliminado"),
            rs.getLong("id")
        );
    }
    
     /**
     * Verifica si una credencial está siendo usada por algún usuario.
     *
     * @param credencialId ID de la credencial a verificar
     * @param conexion conexión JDBC
     * @return true si está en uso, false en caso contrario
     * @throws SQLException si ocurre un error
     */
    public boolean estaEnUso(Long credencialId, Connection conexion) throws SQLException {
        
        
        try (PreparedStatement ps = conexion.prepareStatement(SELECT_COUNT)) {
            ps.setLong(1, credencialId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    
}


