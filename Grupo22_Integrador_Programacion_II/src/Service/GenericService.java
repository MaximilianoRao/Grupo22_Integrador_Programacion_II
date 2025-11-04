
package Service;

import java.util.List;

/**
 *
 * @author Maximiliano Rao
 * 
 * 
 */

/**
 * Interfaz genérica para la capa de servicios.
 *
 * Todas las clases de servicio que trabajen sobre una entidad (por ejemplo
 * {@code UsuarioServiceImpl} o {@code CredencialAccesoServiceImpl}) deben
 * implementar este contrato. De esta forma, el menú o cualquier otra capa
 * sabe que siempre tendrá disponibles las mismas operaciones básicas.
 *
 * @param <T>  tipo de la entidad (por ej. {@code Usuario})
 * @param <ID> tipo del identificador (por ej. {@code Long})
 *
 * Basado en la consigna del TFI: “GenericService: insertar, actualizar,
 * eliminar, getById, getAll”.
 */
public interface GenericService<T, ID> {
    
    /**
     * Inserta una nueva entidad en el sistema.
     *
     * @param entity entidad a insertar (no debe ser {@code null})
     * @return entidad insertada (con ID si lo genera la BD)
     * @throws Exception si hay errores de validación o de acceso a datos
     */
    T insertar(T entity) throws Exception;
    
    /**
     * Obtiene una entidad por su identificador único.
     *
     * @param id identificador de la entidad
     * @return entidad encontrada o {@code null} si no existe
     * @throws Exception si ocurre un error al acceder a la base
     */
    T obtenerPorId(ID id) throws Exception;
    
    /**
     * Lista todas las entidades disponibles.
     *
     * @return lista de entidades (puede ser vacía)
     * @throws Exception si ocurre un error al acceder a la base
     */
    List<T> obtenerTodos() throws Exception;
    
    /**
     * Actualiza los datos de una entidad existente.
     *
     * @param entity entidad con los cambios ya aplicados
     * @throws Exception si la entidad no existe o hay error en la BD
     */
    void actualizar(T entity) throws Exception;
    
    /**
     * Elimina (o marca como eliminada) una entidad por su ID.
     * El tipo de baja (lógica / física) lo decide cada implementación.
     *
     * @param id identificador de la entidad
     * @throws Exception si no se puede eliminar
     */
    void eliminar(ID id) throws Exception;
}
