
package Dao;

import java.sql.Connection;
import java.util.List;
/**
 *
 * @author Maximiliano Rao
 * 
 * Esta es una interfaz genérica que define métodos comunes para trabajar con cualquier entidad.
 * Sirve como base para evitar repetir código en distintas clases DAO (como CredencialAccesoDAO o UsuarioDAO).
 * @param <T>
 */
public interface GenericDAO<T> {
    void crear(T entidad, Connection conn) throws Exception;
    void actualizar(T entidad, Connection conn) throws Exception;
    void eliminar(Long id, Connection conn) throws Exception;
    T leer(Long id, Connection conn) throws Exception;
    List<T> leerTodos(Connection conn) throws Exception;

}
