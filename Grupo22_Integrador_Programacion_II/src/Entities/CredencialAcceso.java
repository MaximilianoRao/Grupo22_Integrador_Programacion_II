
package Entities;

import java.time.LocalDateTime;

/**
 *
 * @author Maximiliano Rao
 * 
 * Entidad que representa una CredencialAcceso (contraseña) en el sistema.
 * Hereda de Base para obtener id y eliminado.
 *
 * Relación con Usuario:
 * - Un Usuario puede tener 1 solo 1 CredencialAcceso
 * - Una CredencialAcceso puede estar asociada a un unico Usuario.
 *
 * Tabla BD: CredencialAcceso
 * Campos:
 * - id bigint auto_increment,
 * - eliminado boolean default(false),
 * - hashPassword varchar(255) not null,
 * - salt varchar(64) not null,
 * - ultimoCambio datetime default current_timestamp not null,
 * - requiereReset boolean not null default(false),
 * 
 */
public class CredencialAcceso extends Base {
    /*
    * Contraseña encriptada
    * Requerido, no puede ser null.
    */
    private String hashPassword;
    
    /*
    * Dato aleatorio que se añade a la contraseña antes del hashing.
    * Requerido, no puede ser null.
    */
    private String salt;
     /*
    * Contiene la fecha del ultimo cambio en la contraseña
    * Requerido, no puede ser null.
    */
    private LocalDateTime ultimoCambio;
     /*
    * Booleano que indica si la contraseña requiere cambio.
    * Requerido, no puede ser null.
    * Por defecto, las entidades nuevas NO están requieren reset.
    */
    private boolean requiereReset;

    /**
    * Constructor completo para reconstruir una Credencial de Acceso desde la base de datos.
    * Usado por UsuarioDAO y CredencialAccesoDAO al mapear ResultSet.
    *
    * @param id ID de la CredencialAcceso en la BD
    * @param ultimoCambio fecha de ultimo cambio de contraseña
    * @param salt String utilizado en el hashing
    * @param hastPassword contraseña encriptada
    * @param requiereReset flag de requiere reset.
    * @param eliminado flag de eliminado.
    * 
    */
    public CredencialAcceso(String hashPassword, String salt, LocalDateTime ultimoCambio, boolean requiereReset, boolean eliminado, Long id) {
        super(id, eliminado); 
        this.hashPassword = hashPassword;
        this.salt = salt;
        this.ultimoCambio = ultimoCambio;
        this.requiereReset = requiereReset;
    }
    
    /**
    * Constructor por defecto para crear una CredencialAcceso nueva.
    * El ID será asignado por la BD al insertar.
    * El flag eliminado se inicializa en false por Base.
    */
    public CredencialAcceso() {
        super();
    }
    /**
    * Obtiene la fecha del ultimo cambio.
    * @return Fecha de ultimo cambio de contraseña.
    */
    public LocalDateTime getUltimoCambio() {
        return ultimoCambio;
    }
    
    /**
    * Establece la fecha del último cambio de contraseña
    * Validación: CredencialAccesoServiceImpl verifica que no esté vacío.
    *
    * @param ultimoCambio Nueva fecha para ultimo cambio de contraseña.
    */
    
    public void setUltimoCambio(LocalDateTime ultimoCambio) {
        this.ultimoCambio = ultimoCambio;
    }
    
    /**
    * Obtiene el flag de requiereReset
    * @return Booleano requiere reset.
    */
    public boolean isRequiereReset() {
        return requiereReset;
    }
    
    /**
    * Establece el flag de requiere reset
    * Validación: CredencialAccesoServiceImpl verifica que no esté vacío.
    *
    * @param requiereReset nuevo flag para requiereReset.
    */
    public void setRequiereReset(boolean requiereReset) {
        this.requiereReset = requiereReset;
    }
    
    /**
    * Devuelve el hash de la contraseña almacenada.
    * @return hashPassword (nunca debería ser null en BD)
    */
    public String getHashPassword() {
        return hashPassword;
    }

    /**
    * Establece el hash de la contraseña.
    * @param hashPassword valor ya encriptado (hash)
    */
    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    /**
    * Devuelve el salt usado para el hash.
    * @return salt
    */
    public String getSalt() {
        return salt;
    }

    /**
    * Establece el salt usado para el hash.
    * @param salt valor de salt
    */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "CredencialAcceso{ " + "id=" + getId() + ", ultimoCambio=" + ultimoCambio + ", requiereReset=" + requiereReset + ", eliminado=" + isEliminado() + " }";
    }
    
        
}
