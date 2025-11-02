
package Entities;

import java.time.LocalDateTime;

/**
 *
 * @author Maximiliano Rao
 * 
 * 
 */
/**
 * Entidad que representa a un usuario del sistema.
 *
 * Según la consigna del TFI, esta es la clase **A** de la relación 1→1
 * (Usuario → CredencialAcceso). Es decir: el usuario tiene una sola
 * credencial asociada. :contentReference[oaicite:2]{index=2}
 * * Hereda de {@link Base} para tener:
 *     {@code id} (Long)
 *     {@code eliminado} (baja lógica)
 */
public class Usuario extends Base{
    /** nombre de usuario único (NOT NULL, máx. 30) */
    private String username;

    /** email único (NOT NULL, máx. 120) */
    private String email;

    /** indica si el usuario está activo */
    private boolean activo;

    /** fecha y hora en que se registró el usuario */
    private LocalDateTime fechaRegistro;

    /** relación 1→1: un usuario tiene 1 credencial */
    private CredencialAcceso credencial;
    
    // =========================
    // ===== CONSTRUCTORES =====
    // =========================

    /**
     * Constructor por defecto.
     * Se usa cuando vamos a crear un usuario nuevo desde la app.
     */
    public Usuario() {
        super(); // eliminado = false (viene de Base) :contentReference[oaicite:3]{index=3}
        this.activo = true; // por defecto lo dejamos activo
    }
    
    /**
     * Constructor completo, útil para reconstruir desde la BD.
     */
    public Usuario(Long id, boolean eliminado,String username,String email,boolean activo,LocalDateTime fechaRegistro,CredencialAcceso credencial) {
        super(id, eliminado); // setea id y eliminado de la clase base :contentReference[oaicite:4]{index=4}
        this.username = username;
        this.email = email;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.credencial = credencial;
    }
    
    // =========================
    // ===== GETTERS/SETTERS ===
    // =========================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public CredencialAcceso getCredencial() {
        return credencial;
    }

    public void setCredencial(CredencialAcceso credencial) {
        this.credencial = credencial;
    }
    
    // =========================
    // ===== toString() ========
    // =========================

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + getId() +       // viene de Base → ya existe getId() :contentReference[oaicite:5]{index=5}
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", fechaRegistro=" + fechaRegistro +
                ", eliminado=" + isEliminado() +
                '}';
    }
}
