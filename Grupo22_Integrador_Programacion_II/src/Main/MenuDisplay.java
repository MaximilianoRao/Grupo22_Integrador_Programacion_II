
package Main;

/**
 *
 * @author Maximiliano Rao
 * 
 *
 * Clase utilitaria para mostrar el menú de la aplicación.
 * Solo contiene métodos estáticos de visualización (no tiene estado).
 *
 * Responsabilidades:
 * - Mostrar el menú principal con todas las opciones disponibles
 * - Formatear la salida de forma consistente
 *
 * Patrón: Utility class (solo métodos estáticos, no instanciable)
 *
 * IMPORTANTE: Esta clase NO lee entrada del usuario.
 * Solo muestra el menú. AppMenu es responsable de leer la opción.
 *
 */
public class MenuDisplay {
/**
     * Muestra el menú principal con todas las opciones CRUD.
     *
     * Opciones de Usuario (1-9)
     *
     * Opciones de Domicilios (9-15)
     *
     * Opción de salida:
     * 0. Salir: Termina la aplicación
     *
     * Formato:
     * - Separador visual "========= MENU ========="
     * - Lista numerada clara
     * - Prompt "Ingrese una opcion: " sin salto de línea (espera input)
     *
     * Nota: Los números de opción corresponden al switch en AppMenu.processOption().
     */
    public static void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║     SISTEMA DE GESTIÓN DE USUARIOS  ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n┌─── GESTIÓN DE USUARIOS ────────────────────┐");
        System.out.println("│ 1.  Crear Usuario con Credencial       │");
        System.out.println("│ 2.  Listar todos los Usuarios          │");
        System.out.println("│ 3.  Buscar Usuario por ID              │");
        System.out.println("│ 4.  Buscar Usuario por Username        │");
        System.out.println("│ 5.  Buscar Usuario por Email           │");
        System.out.println("│ 6.  Actualizar Usuario                 │");
        System.out.println("│ 7.  Eliminar Usuario                   │");
        System.out.println("│ 8.  Activar Usuario                    │");
        System.out.println("│ 9.  Desactivar Usuario                 │");
        System.out.println("└────────────────────────────────────────────────┘");
        System.out.println("\n┌─── GESTIÓN DE CREDENCIALES ────────────────┐");
        System.out.println("│ 10. Crear Credencial (independiente)    │");
        System.out.println("│ 11. Listar todas las Credenciales       │");
        System.out.println("│ 12. Buscar Credencial por ID            │");
        System.out.println("│ 13. Actualizar Credencial               │");
        System.out.println("│ 14. Eliminar Credencial                 │");
        System.out.println("│ 15. Cambiar Password de Credencial      │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println("\n┌─── SISTEMA ─────────────────────────────────┐");
        System.out.println("│ 0.  Salir                             │");
        System.out.println("└──────────────────────────────────────────────┘");
        System.out.print("\n➤ Ingrese una opción: ");
    }
    
    /**
     * Muestra un mensaje de éxito.
     */
    public static void mostrarExito(String mensaje) {
        System.out.println("\n✓ ÉXITO: " + mensaje);
    }
    
    /**
     * Muestra un mensaje de error.
     */
    public static void mostrarError(String mensaje) {
        System.out.println("\n✗ ERROR: " + mensaje);
    }
    
    /**
     * Muestra un mensaje de advertencia.
     */
    public static void mostrarAdvertencia(String mensaje) {
        System.out.println("\n⚠ ADVERTENCIA: " + mensaje);
    }
    
    /**
     * Muestra un separador visual.
     */
    public static void mostrarSeparador() {
        System.out.println("\n" + "─".repeat(50));
    }
    
    /**
     * Muestra un encabezado de sección.
     */
    public static void mostrarEncabezado(String titulo) {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("  " + titulo.toUpperCase());
        System.out.println("═".repeat(50));
    }
    
}
