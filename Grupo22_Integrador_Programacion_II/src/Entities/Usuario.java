package Entities;

import java.time.LocalDateTime;

/**
 * @author Maximiliano Rao
 * 
 * Entidad que representa un Usuario en el sistema.
 * Hereda de Base para obtener id y eliminado.
 *
 * Relación con CredencialAcceso (1→1 unidireccional):
 * - Un Usuario tiene exactamente 1 CredencialAcceso
 * - La relación es unidireccional: Usuario conoce a CredencialAcceso,
 *   pero CredencialAcceso NO conoce a Usuario
 *
 * Tabla BD: Usuarios
 * Campos:
 * - id bigint auto_increment,
 * - eliminado boolean default(false),
 * - username varchar(30) unique not null,
 * - email varchar(120) unique not null,
 * - activo boolean not null default(false),
 * - fechaRegistro datetime default current_timestamp not null,
 * - credencial bigint unique not null (FK a CredencialAcceso)
 */
public class Usuario extends Base {
    
    /**
     * Nombre de usuario único en el sistema.
     * Requerido, no puede ser null.
     * Máximo 30 caracteres.
     */
    private String username;
    
    /**
     * Correo electrónico único del usuario.
     * Requerido, no puede ser null.
     * Máximo 120 caracteres.
     */
    private String email;
    
    /**
     * Indica si el usuario está activo en el sistema.
     * Requerido, no puede ser null.
     * Por defecto, los usuarios nuevos están inactivos (false).
     */
    private boolean activo;
    
    /**
     * Fecha y hora de registro del usuario.
     * Requerido, no puede ser null.
     * Se establece automáticamente al crear el usuario.
     */
    private LocalDateTime fechaRegistro;
    
    /**
     * Referencia a la credencial de acceso del usuario.
     * Relación 1→1 unidireccional con CredencialAcceso.
     * Requerido, no puede ser null.
     * Un usuario debe tener siempre una credencial asociada.
     */
    private CredencialAcceso credencial;

    /**
     * Constructor completo para reconstruir un Usuario desde la base de datos.
     * Usado por UsuarioDAO al mapear ResultSet.
     *
     * @param id ID del usuario en la BD
     * @param username nombre de usuario único
     * @param email correo electrónico único
     * @param activo estado de activación del usuario
     * @param fechaRegistro fecha de registro
     * @param credencial objeto CredencialAcceso asociado
     * @param eliminado flag de eliminado lógico
     */
    public Usuario(Long id, String username, String email, boolean activo, LocalDateTime fechaRegistro, CredencialAcceso credencial, boolean eliminado) {
        super(id, eliminado);
        this.username = username;
        this.email = email;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.credencial = credencial;
    }
    
    /**
     * Constructor por defecto para crear un Usuario nuevo.
     * El ID será asignado por la BD al insertar.
     * El flag eliminado se inicializa en false por Base.
     * Los demás campos deben ser establecidos mediante setters.
     */
    public Usuario() {
        super();
    }

    
    /**
     * Obtiene el nombre de usuario.
     * @return username del usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     * Validación: UsuarioServiceImpl verifica que no sea null/vacío
     * y que tenga máximo 30 caracteres.
     *
     * @param username nuevo nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return email del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico.
     * Validación: UsuarioServiceImpl verifica formato válido,
     * que no sea null/vacío y máximo 120 caracteres.
     *
     * @param email nuevo correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Verifica si el usuario está activo.
     * @return true si el usuario está activo, false en caso contrario
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de activación del usuario.
     *
     * @param activo true para activar, false para desactivar
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene la fecha de registro del usuario.
     * @return fecha y hora de registro
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha de registro.
     * Validación: UsuarioServiceImpl verifica que no sea null.
     *
     * @param fechaRegistro nueva fecha de registro
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Obtiene la credencial de acceso asociada al usuario.
     * @return objeto CredencialAcceso
     */
    public CredencialAcceso getCredencial() {
        return credencial;
    }

    /**
     * Establece la credencial de acceso del usuario.
     * Validación: UsuarioServiceImpl verifica que no sea null.
     *
     * @param credencial objeto CredencialAcceso a asociar
     */
    public void setCredencial(CredencialAcceso credencial) {
        this.credencial = credencial;
    }

    /**
     * Representación en String del Usuario.
     * NO incluye información sensible de la credencial.
     * Incluye información básica del usuario y referencia a si tiene credencial.
     *
     * @return representación legible del usuario
     */
    @Override
    public String toString() {
        return "Usuario{" + "id=" + getId() +", username='" + username + '\'' +", email='" + email + '\'' +", activo=" + activo +", fechaRegistro=" + fechaRegistro +",\n tieneCredencial=" + (credencial != null) +", credencialId=" + (credencial != null ? credencial.getId() : "null") +", eliminado=" + isEliminado() + "}";
    }
}