
package Config;

import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 * @author Maximiliano Rao
 * 
 * 
 */
public class TransactionManager implements AutoCloseable  {
    private Connection conn;
    private boolean transactionActive;

    public TransactionManager(Connection conn) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        this.conn = conn;
        this.transactionActive = false;
    }

    public Connection getConnection() {
        return conn;
    }

    public void startTransaction() throws SQLException {
        if (conn == null) {
            throw new SQLException("No se puede iniciar la transacción: conexión no disponible");
        }
        if (conn.isClosed()) {
            throw new SQLException("No se puede iniciar la transacción: conexión cerrada");
        }
        conn.setAutoCommit(false);
        transactionActive = true;
    }

    public void commit() throws SQLException {
        if (conn == null) {
            throw new SQLException("Error al hacer commit: no hay conexión establecida");
        }
        if (!transactionActive) {
            throw new SQLException("No hay una transacción activa para hacer commit");
        }
        conn.commit();
        transactionActive = false;
    }

    public void rollback() {
        if (conn != null && transactionActive) {
            try {
                conn.rollback();
                transactionActive = false;
            } catch (SQLException e) {
                System.err.println("Error durante el rollback: " + e.getMessage());
            }
        }
    }

    @Override
    public void close() {
        if (conn != null) {
            try {
                if (transactionActive) {
                    rollback();
                }
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public boolean isTransactionActive() {
        return transactionActive;
    }
}
