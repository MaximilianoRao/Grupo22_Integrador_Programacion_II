
package Service;

import Config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author alfre
 */

/**
 * Clase base para los servicios de la aplicación.
 *
 * 
 * Centraliza el código repetitivo de:
 * 
 *     Obtener una {@link Connection} desde {@link Config.DatabaseConnection}.
 *     Iniciar una transacción (setAutoCommit(false)).
 *     Confirmar la transacción (commit) si todo salió bien.
 *     Revertir la transacción (rollback) ante cualquier error.
 *     Restaurar el auto-commit y cerrar la conexión.
 *     Validaciones comunes (no nulo, no vacío, email válido).
 * 
 * La consigna del TFI dice que la capa Service debe “Abrir transacción:
 * setAutoCommit(false) … commit() si todo OK; rollback() ante cualquier error;
 * Restablecer autoCommit(true) y cerrar recursos.”, así que este es el lugar
 * donde lo hacemos una sola vez.
 * 
 */
public abstract class AbstractService {
    /**
     * Obtiene una nueva conexión a la base de datos usando la clase de config.
     *
     * @return conexión abierta a la BD
     * @throws SQLException si no se puede conectar
     */
    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    
    /**
     * Inicia una transacción deshabilitando el auto-commit.
     *
     * @param conn conexión sobre la que se trabajará
     * @throws SQLException si no se puede cambiar el modo de auto-commit
     */
    protected void beginTransaction(Connection conn) throws SQLException {
        if (conn != null && conn.getAutoCommit()) {
            conn.setAutoCommit(false);
        }
    }
    
    /**
     * Confirma (commit) la transacción actual.
     *
     * @param conn conexión sobre la que se está trabajando
     * @throws SQLException si ocurre un error al confirmar
     */
    protected void commitTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }
    
    /**
     * Revierte (rollback) la transacción actual.
     *
     * No relanzamos la excepción porque normalmente este método se llama desde
     * un bloque {@code catch} y no queremos tapar la causa real.
     *
     * @param conn conexión sobre la que se está trabajando
     */
    protected void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al hacer rollback: " + e.getMessage());
            }
        }
    }

    /**
     * Restaura el auto-commit a {@code true} y cierra la conexión.
     * Este método debería llamarse SIEMPRE en el {@code finally}.
     *
     * @param conn conexión a cerrar
     */
    protected void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    // ==========================================================
    // ============== VALIDACIONES COMUNES ======================
    // ==========================================================

    /**
     * Valida que un string no sea nulo ni vacío.
     *
     * @param value     valor a validar
     * @param fieldName nombre del campo (para el mensaje de error)
     * @throws IllegalArgumentException si el string está vacío
     */
    protected void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " no puede estar vacío");
        }
    }
    
    /**
     * Valida que un objeto no sea nulo.
     *
     * @param obj       objeto a validar
     * @param fieldName nombre del campo (para el mensaje de error)
     * @throws IllegalArgumentException si el objeto es nulo
     */
    protected void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " no puede ser nulo");
        }
    }
    
    /**
     * Valida el formato básico de un email.
     *
     * @param email email a validar
     * @throws IllegalArgumentException si el email es nulo o no coincide con el patrón
     */
    protected void validateEmail(String email) {
        if (email == null
                || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
    }
}
