
package Main;

import Entities.CredencialAcceso;
import Entities.Usuario;
import Service.UsuarioServiceImpl;
import Service.CredencialAccesoServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Maximiliano Rao
 * 
 *
 * Controlador de las operaciones del menú.
 * Gestiona toda la lógica de interacción con el usuario.
 * 
 * Responsabilidades:
 * - Capturar entrada del usuario
 * - Validar entrada básica
 * - Invocar servicios de negocio
 * - Mostrar resultados y mensajes
 * 
 * Patrón: Controller (MVC)
 * Arquitectura: Main → Service → DAO → Models
 */

public class MenuHandler {
    private final UsuarioServiceImpl usuarioService;
    private final CredencialAccesoServiceImpl credencialService;
    private final InputValidator validator;
    
    
    
    public MenuHandler(UsuarioServiceImpl usuarioService, 
                      CredencialAccesoServiceImpl credencialService,
                      InputValidator validator) {
        this.usuarioService = usuarioService;
        this.credencialService = credencialService;
        this.validator = validator;
    }
    
     /**
     * Crea un usuario nuevo con su credencial.
     */
    public void crearUsuarioConCredencial() {
        try {
            MenuDisplay.mostrarEncabezado("CREAR USUARIO");
            
            // Datos del usuario
            String username = validator.leerString("Username: ");
            String email = validator.leerEmail("Email: ");
            boolean activo = validator.leerBoolean("¿Usuario activo?");
            
            // Datos de la credencial
            System.out.println("\n--- Credencial de Acceso ---");
            String hashPassword = validator.leerString("Hash de contraseña: ");
            String salt = validator.leerString("Salt: ");
            boolean requiereReset = validator.leerBoolean("¿Requiere reset de contraseña?");
            
            // Crear objetos
            CredencialAcceso credencial = new CredencialAcceso();
            credencial.setHashPassword(hashPassword);
            credencial.setSalt(salt);
            credencial.setUltimoCambio(LocalDateTime.now());
            credencial.setRequiereReset(requiereReset);
            
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setActivo(activo);
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setCredencial(credencial);
            
            
            
            // Guardar
            Usuario usuarioCreado = usuarioService.crearUsuarioConCredencial(usuario, credencial);
            
            MenuDisplay.mostrarExito("Usuario creado exitosamente con ID: " + usuarioCreado.getId());
            System.out.println(usuarioCreado);
            
        } catch (IllegalArgumentException e) {
            MenuDisplay.mostrarError("Validación: " + e.getMessage());
        } catch (Exception e) {
            MenuDisplay.mostrarError("No se pudo crear el usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Lista todos los usuarios.
     */
    public void listarUsuarios() {
        try {
            MenuDisplay.mostrarEncabezado("LISTA DE USUARIOS");
            
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            
            if (usuarios.isEmpty()) {
                MenuDisplay.mostrarAdvertencia("No hay usuarios registrados.");
            } else {
                System.out.println("\nTotal de usuarios: " + usuarios.size());
                MenuDisplay.mostrarSeparador();
                
                for (Usuario u : usuarios) {
                    System.out.println("\nID: " + u.getId());
                    System.out.println("Username: " + u.getUsername());
                    System.out.println("Email: " + u.getEmail());
                    System.out.println("Activo: " + (u.isActivo() ? "Sí" : "No"));
                    System.out.println("Fecha Registro: " + u.getFechaRegistro());
                    System.out.println("Credencial ID: " + u.getCredencial().getId());
                    MenuDisplay.mostrarSeparador();
                }
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("No se pudo listar usuarios: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Busca un usuario por ID.
     */
    public void buscarUsuarioPorId() {
        try {
            MenuDisplay.mostrarEncabezado("BUSCAR USUARIO POR ID");
            
            Long id = validator.leerLong("Ingrese ID del usuario: ");
            Usuario usuario = usuarioService.obtenerPorId(id);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con ID: " + id);
            } else {
                System.out.println("\n" + usuario);
                MenuDisplay.mostrarExito("Usuario encontrado.");
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al buscar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    
     /**
     * Busca un usuario por username.
     */
    public void buscarUsuarioPorUsername() {
        try {
            MenuDisplay.mostrarEncabezado("BUSCAR USUARIO POR USERNAME");
            
            String username = validator.leerString("Ingrese username: ");
            Usuario usuario = usuarioService.buscarPorUsername(username);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con username: " + username);
            } else {
                System.out.println("\n" + usuario);
                MenuDisplay.mostrarExito("Usuario encontrado.");
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al buscar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Busca un usuario por email.
     */
    public void buscarUsuarioPorEmail() {
        try {
            MenuDisplay.mostrarEncabezado("BUSCAR USUARIO POR EMAIL");
            
            String email = validator.leerEmail("Ingrese email: ");
            Usuario usuario = usuarioService.buscarPorEmail(email);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con email: " + email);
            } else {
                System.out.println("\n" + usuario);
                MenuDisplay.mostrarExito("Usuario encontrado.");
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al buscar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Actualiza un usuario existente.
     */
    public void actualizarUsuario() {
        try {
            MenuDisplay.mostrarEncabezado("ACTUALIZAR USUARIO");
            
            Long id = validator.leerLong("Ingrese ID del usuario a actualizar: ");
            Usuario usuario = usuarioService.obtenerPorId(id);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con ID: " + id);
                validator.pausar();
                return;
            }
            
            System.out.println("\nDatos actuales:");
            System.out.println(usuario);
            System.out.println("\nIngrese los nuevos datos (Enter para mantener):");
            
            // Leer nuevos datos
            String nuevoUsername = validator.leerStringOpcional("Nuevo username [" + usuario.getUsername() + "]: ");
            if (!nuevoUsername.isEmpty()) {
                usuario.setUsername(nuevoUsername);
            }
            
            String nuevoEmail = validator.leerStringOpcional("Nuevo email [" + usuario.getEmail() + "]: ");
            if (!nuevoEmail.isEmpty()) {
                usuario.setEmail(nuevoEmail);
            }
            
            if (validator.confirmar("¿Cambiar estado de activación?")) {
                boolean nuevoActivo = validator.leerBoolean("¿Usuario activo?");
                usuario.setActivo(nuevoActivo);
            }
            
            // Confirmar actualización
            if (validator.confirmar("\n¿Confirma la actualización?")) {
                usuarioService.actualizar(usuario);
                MenuDisplay.mostrarExito("Usuario actualizado correctamente.");
            } else {
                MenuDisplay.mostrarAdvertencia("Actualización cancelada.");
            }
            
        } catch (IllegalArgumentException e) {
            MenuDisplay.mostrarError("Validación: " + e.getMessage());
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al actualizar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Elimina un usuario (soft delete).
     */
    public void eliminarUsuario() {
        try {
            MenuDisplay.mostrarEncabezado("ELIMINAR USUARIO");
            
            Long id = validator.leerLong("Ingrese ID del usuario a eliminar: ");
            Usuario usuario = usuarioService.obtenerPorId(id);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con ID: " + id);
                validator.pausar();
                return;
            }
            
            System.out.println("\nUsuario a eliminar:");
            System.out.println(usuario);
            
            MenuDisplay.mostrarAdvertencia("\n⚠ ATENCIÓN: Esta operación también eliminará la credencial asociada.");
            
            if (validator.confirmar("\n¿Está seguro que desea eliminar este usuario?")) {
                usuarioService.eliminar(id);
                MenuDisplay.mostrarExito("Usuario eliminado correctamente (soft delete).");
            } else {
                MenuDisplay.mostrarAdvertencia("Eliminación cancelada.");
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al eliminar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Activa un usuario.
     */
    public void activarUsuario() {
        try {
            MenuDisplay.mostrarEncabezado("ACTIVAR USUARIO");
            
            Long id = validator.leerLong("Ingrese ID del usuario a activar: ");
            Usuario usuario = usuarioService.obtenerPorId(id);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con ID: " + id);
                validator.pausar();
                return;
            }
            usuarioService.activarUsuario(id);
            MenuDisplay.mostrarExito("Usuario activado correctamente.");
            System.out.println(usuario);
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al activar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
     /**
     * Desactiva un usuario.
     */
    public void desactivarUsuario() {
        try {
            MenuDisplay.mostrarEncabezado("DESACTIVAR USUARIO");
            
            Long id = validator.leerLong("Ingrese ID del usuario a desactivar: ");
            Usuario usuario = usuarioService.obtenerPorId(id);
            
            if (usuario == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró usuario con ID: " + id);
                validator.pausar();
                return;
            }
            usuarioService.desactivarUsuario(id);
            MenuDisplay.mostrarExito("Usuario desactivado correctamente.");
            System.out.println(usuario);
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al desactivar usuario: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    
     /**
     * Crea una credencial independiente (huérfana).
     * NOTA: Normalmente no debería usarse, pero se incluye para cumplir con CRUD completo.
     */
    public void crearCredencialIndependiente() {
        try {
            MenuDisplay.mostrarEncabezado("CREAR CREDENCIAL INDEPENDIENTE");
            MenuDisplay.mostrarAdvertencia("⚠ Una credencial sin usuario asociado es inútil.");
            MenuDisplay.mostrarAdvertencia("⚠ Considere usar 'Crear Usuario' en su lugar.");
            
            if (!validator.confirmar("\n¿Desea continuar de todas formas?")) {
                MenuDisplay.mostrarAdvertencia("Operación cancelada.");
                validator.pausar();
                return;
            }
            
            String hashPassword = validator.leerString("Hash de contraseña: ");
            String salt = validator.leerString("Salt: ");
            boolean requiereReset = validator.leerBoolean("¿Requiere reset de contraseña?");
            
            CredencialAcceso credencial = new CredencialAcceso();
            credencial.setHashPassword(hashPassword);
            credencial.setSalt(salt);
            credencial.setUltimoCambio(LocalDateTime.now());
            credencial.setRequiereReset(requiereReset);
            
            CredencialAcceso credencialCreada = credencialService.insertar(credencial);
            
            MenuDisplay.mostrarExito("Credencial creada con ID: " + credencialCreada.getId());
            System.out.println(credencialCreada);
            
        } catch (IllegalArgumentException e) {
            MenuDisplay.mostrarError("Validación: " + e.getMessage());
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al crear credencial: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Lista todas las credenciales.
     */
    public void listarCredenciales() {
        try {
            MenuDisplay.mostrarEncabezado("LISTA DE CREDENCIALES");
            
            List<CredencialAcceso> credenciales = credencialService.obtenerTodos();
            
            if (credenciales.isEmpty()) {
                MenuDisplay.mostrarAdvertencia("No hay credenciales registradas.");
            } else {
                System.out.println("\nTotal de credenciales: " + credenciales.size());
                MenuDisplay.mostrarSeparador();
                
                for (CredencialAcceso c : credenciales) {
                    System.out.println(c);
                    MenuDisplay.mostrarSeparador();
                }
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al listar credenciales: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Busca una credencial por ID.
     */
    public void buscarCredencialPorId() {
        try {
            MenuDisplay.mostrarEncabezado("BUSCAR CREDENCIAL POR ID");
            
            Long id = validator.leerLong("Ingrese ID de la credencial: ");
            CredencialAcceso credencial = credencialService.obtenerPorId(id);
            
            if (credencial == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró credencial con ID: " + id);
            } else {
                System.out.println("\n" + credencial);
                MenuDisplay.mostrarExito("Credencial encontrada.");
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al buscar credencial: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    
    /**
     * Actualiza una credencial existente.
     */
    public void actualizarCredencial() {
        try {
            MenuDisplay.mostrarEncabezado("ACTUALIZAR CREDENCIAL");
            
            Long id = validator.leerLong("Ingrese ID de la credencial a actualizar: ");
            CredencialAcceso credencial = credencialService.obtenerPorId(id);
            
            if (credencial == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró credencial con ID: " + id);
                validator.pausar();
                return;
            }
            
            System.out.println("\nDatos actuales:");
            System.out.println(credencial);
            System.out.println("\nIngrese los nuevos datos (Enter para mantener):");
            
            String nuevoHash = validator.leerStringOpcional("Nuevo hash [mantener actual]: ");
            if (!nuevoHash.isEmpty()) {
                credencial.setHashPassword(nuevoHash);
            }
            
            String nuevoSalt = validator.leerStringOpcional("Nuevo salt [mantener actual]: ");
            if (!nuevoSalt.isEmpty()) {
                credencial.setSalt(nuevoSalt);
            }
            
            if (validator.confirmar("¿Cambiar requiere reset?")) {
                boolean nuevoReset = validator.leerBoolean("¿Requiere reset?");
                credencial.setRequiereReset(nuevoReset);
            }
            
            credencial.setUltimoCambio(LocalDateTime.now());
            
            if (validator.confirmar("\n¿Confirma la actualización?")) {
                credencialService.actualizar(credencial);
                    MenuDisplay.mostrarExito("Credencial actualizada correctamente.");
            } else {
                MenuDisplay.mostrarAdvertencia("Actualización cancelada.");
            }
            
        } catch (IllegalArgumentException e) {
            MenuDisplay.mostrarError("Validación: " + e.getMessage());
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al actualizar credencial: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    /**
     * Elimina una credencial (soft delete).
     */
    public void eliminarCredencial() {
        try {
            MenuDisplay.mostrarEncabezado("ELIMINAR CREDENCIAL");
            
            Long id = validator.leerLong("Ingrese ID de la credencial a eliminar: ");
            CredencialAcceso credencial = credencialService.obtenerPorId(id);
            
            if (credencial == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró credencial con ID: " + id);
                validator.pausar();
                return;
            }
            
            System.out.println("\nCredencial a eliminar:");
            System.out.println(credencial);
            
            MenuDisplay.mostrarAdvertencia("\n⚠ ATENCIÓN: No se puede eliminar si está asociada a un usuario.");
            
            if (validator.confirmar("\n¿Está seguro que desea eliminar esta credencial?")) {
                credencialService.eliminar(id);
                MenuDisplay.mostrarExito("Credencial eliminada correctamente.");
            } else {
                MenuDisplay.mostrarAdvertencia("Eliminación cancelada.");
            }
            
        } catch (IllegalStateException e) {
            MenuDisplay.mostrarError("No se puede eliminar: " + e.getMessage());
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al eliminar credencial: " + e.getMessage());
        }
        
        validator.pausar();
    }
    
    
    /**
     * Cambia la contraseña de una credencial.
     */
    public void cambiarPassword() {
        try {
            MenuDisplay.mostrarEncabezado("CAMBIAR PASSWORD DE CREDENCIAL");
            
            Long id = validator.leerLong("Ingrese ID de la credencial: ");
            CredencialAcceso credencial = credencialService.obtenerPorId(id);
            
            if (credencial == null) {
                MenuDisplay.mostrarAdvertencia("No se encontró credencial con ID: " + id);
                validator.pausar();
                return;
            }
            
            System.out.println("\nCredencial actual:");
            System.out.println(credencial);
            
            String nuevoHash = validator.leerString("\nNuevo hash de contraseña: ");
            String nuevoSalt = validator.leerString("Nuevo salt: ");
            
            if (validator.confirmar("\n¿Confirma el cambio de contraseña?")) {
               credencialService.cambiarPassword(id, nuevoHash, nuevoSalt);
               MenuDisplay.mostrarExito("Contraseña cambiada correctamente.");
               MenuDisplay.mostrarExito("Fecha de último cambio actualizada.");
               MenuDisplay.mostrarExito("Flag 'requiere reset' desactivado.");
            } else {
                MenuDisplay.mostrarAdvertencia("Cambio cancelado.");
            }
            
        } catch (Exception e) {
            MenuDisplay.mostrarError("Error al cambiar contraseña: " + e.getMessage());
        }
        
        validator.pausar();
    }
}
