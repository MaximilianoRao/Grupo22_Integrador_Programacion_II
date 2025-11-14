
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

public class CredencialAccesoServiceImpl extends AbstractService implements GenericService<CredencialAcceso>{
    
    private final CredencialAccesoDAO credencialDAO;

    public CredencialAccesoServiceImpl(CredencialAccesoDAO credencialDAO) {
        this.credencialDAO = credencialDAO;
    }
    
    @Override
    public void insertar(CredencialAcceso cred) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);
            validarCredencial(cred);
            // Inicializar campos si es necesario
            if (cred.getUltimoCambio() == null) {
                cred.setUltimoCambio(LocalDateTime.now());
            }

            credencialDAO.crear(cred, conn);
            

            commitTransaction(conn);
         
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new Exception("Error al insertar credencial: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public CredencialAcceso obtenerPorId(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0");
        }
        Connection conn = null;
        try {
            conn = getConnection();
            return credencialDAO.leer(id, conn);
        } 
        finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public List<CredencialAcceso> obtenerTodos() throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            return credencialDAO.leerTodos(conn);
        } finally {
            closeConnection(conn);
        }
    }
    
    @Override
    public void actualizar(CredencialAcceso cred) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);
            // Validar que tiene ID
            if (cred.getId() == null || cred.getId() <= 0) {
                throw new IllegalArgumentException("La credencial debe tener un ID válido para actualizar");
            }
            
            // Validar la entidad
            validarCredencial(cred);
            // Verificar que existe
            CredencialAcceso existente = credencialDAO.leer(cred.getId(), conn);
            if (existente == null) {
                throw new IllegalArgumentException("Credencial no encontrada con ID: " + cred.getId());
            }
            credencialDAO.actualizar(cred, conn);

            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new Exception("Error al actualizar credencial: " + e.getMessage(), e);
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
            // Validar ID
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("El ID debe ser mayor a 0");
            }
            CredencialAcceso existente = credencialDAO.leer(id, conn);
            if (existente == null) {
                throw new IllegalArgumentException("Credencial no encontrada con ID: " + id);
            }
             // IMPORTANTE: Verificar si está en uso
            if (credencialDAO.estaEnUso(id, conn)) {
            throw new IllegalStateException(
                "No se puede eliminar la credencial porque está asociada a un usuario. " +
                "Elimine primero el usuario o use el método de eliminación de usuario que maneja ambos."
            );
            }
            credencialDAO.eliminar(id, conn);
            
            

            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new Exception("Error al eliminar credencial: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }
    
    /**
     * Cambia la contraseña de una credencial existente.
     * Actualiza el hash, salt y fecha de último cambio.
     *
     * @param credencialId ID de la credencial
     * @param nuevoHash nuevo hash de contraseña
     * @param nuevoSalt nuevo salt
     * @throws Exception si hay error
     */
    public void cambiarPassword(Long credencialId, String nuevoHash, String nuevoSalt) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            beginTransaction(conn);
            
            // Validar parámetros
            if (credencialId == null || credencialId <= 0) {
                throw new IllegalArgumentException("El ID debe ser mayor a 0");
            }
            if (nuevoHash == null || nuevoHash.trim().isEmpty()) {
                throw new IllegalArgumentException("El nuevo hash no puede estar vacío");
            }
            if (nuevoSalt == null || nuevoSalt.trim().isEmpty()) {
                throw new IllegalArgumentException("El nuevo salt no puede estar vacío");
            }
            
            // Leer credencial existente
            CredencialAcceso credencial = credencialDAO.leer(credencialId, conn);
            if (credencial == null) {
                throw new IllegalArgumentException("Credencial no encontrada con ID: " + credencialId);
            }
            
            // Actualizar datos
            credencial.setHashPassword(nuevoHash);
            credencial.setSalt(nuevoSalt);
            credencial.setUltimoCambio(LocalDateTime.now());
            credencial.setRequiereReset(false); // Ya cambió la contraseña
            
            // Guardar cambios
            credencialDAO.actualizar(credencial, conn);
            
            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw new Exception("Error al cambiar password: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }
    
    /**
     * Valida los datos de una credencial.
     *
     * @param credencial Credencial a validar
     * @throws IllegalArgumentException si algún dato es inválido
     */
    private void validarCredencial(CredencialAcceso credencial) {
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
