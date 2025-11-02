
package Service;

import Dao.CredencialAccesoDAO;
import Entities.CredencialAcceso;
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
 * Servicio para la entidad {@link CredencialAcceso}.
 *
 * Esta capa es la que se asegura de las reglas específicas de la credencial,
 * por ejemplo:
 * 
 *     que el {@code hashPassword} no venga vacío;
 *     que se registre la fecha de último cambio;
 *     que el flag {@code requiereReset} tenga un valor;
 *     y que TODO eso se haga dentro de una transacción, tal como pide el TFI.
 */

public class CredencialAccesoServiceImpl extends AbstractService implements GenericService<CredencialAcceso, Long>{
    
    private final CredencialAccesoDAO credencialDAO;

    public CredencialAccesoServiceImpl(CredencialAccesoDAO credencialDAO) {
        this.credencialDAO = credencialDAO;
    }
    
    @Override
    public CredencialAcceso insertar(CredencialAcceso cred) throws Exception {
        validateNotNull(cred, "credencial");
        validateNotEmpty(cred.getHashPassword(), "hashPassword");

        // si no viene fecha de último cambio, la ponemos ahora
        if (cred.getUltimoCambio() == null) {
            cred.setUltimoCambio(LocalDateTime.now());
        }

        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);

            // TODO: CredencialAcceso creada = credencialDAO.crear(cred, conn);
            CredencialAcceso creada = cred;

            commitTransaction(conn);
            return creada;
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public CredencialAcceso obtenerPorId(Long id) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // TODO: return credencialDAO.leer(id, conn);
            return null;
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public List<CredencialAcceso> obtenerTodos() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            // TODO: return credencialDAO.leerTodos(conn);
            return List.of();
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public void actualizar(CredencialAcceso cred) throws Exception {
        validateNotNull(cred, "credencial");
        validateNotNull(cred.getId(), "id de credencial");

        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);

            // TODO: credencialDAO.actualizar(cred, conn);

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

            // TODO: credencialDAO.eliminar(id, conn);

            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }
}
