
package Service;

import Config.DatabaseConnection;
import Entities.CredencialAcceso;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
    
    protected boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
        
   
     /**
     * Valida los datos de una credencial.
     *
     * @param credencial Credencial a validar
     * @throws IllegalArgumentException si algún dato es inválido
     */
    protected void validarCredencial(CredencialAcceso credencial) {
        if (credencial == null) {
            throw new IllegalArgumentException("La credencial no puede ser null");
        }
        
        // Validar hashPassword
        if (credencial.getHashPassword() == null || credencial.getHashPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("El hash de la contraseña no puede estar vacío");
        }
        if (credencial.getHashPassword().length() > 255) {
            throw new IllegalArgumentException("El hash no puede tener más de 255 caracteres");
        }
        
        // Validar salt
        if (credencial.getSalt() == null || credencial.getSalt().trim().isEmpty()) {
            throw new IllegalArgumentException("El salt no puede estar vacío");
        }
        if (credencial.getSalt().length() > 64) {
            throw new IllegalArgumentException("El salt no puede tener más de 64 caracteres");
        }
        
        // Validar ultimoCambio
        if (credencial.getUltimoCambio() != null) {
        // Validar que la fecha no sea futura
        if (credencial.getUltimoCambio().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de último cambio no puede ser futura");
        }
        }
    }
    
}
