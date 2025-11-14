
package Main;

/**
 *
 * @author Maximiliano Rao
 * 
 *
 * Punto de entrada alternativo de la aplicación.
 * Clase simple que delega inmediatamente a AppMenu.
 *
 * Responsabilidad:
 * - Proporcionar un punto de entrada main() estándar
 * - Delegar la ejecución a AppMenu
 *
 * Diferencia con AppMenu.main():
 * - AppMenu.main(): Punto de entrada primario (más usado)
 * - Main.main(): Punto de entrada alternativo (mismo comportamiento)
 *
 * Ambos métodos hacen exactamente lo mismo:
 * 1. Crean instancia de AppMenu
 * 2. Llaman a app.run() para iniciar el menú
 *
 * ¿Por qué existen dos puntos de entrada?
 * - Flexibilidad: Algunos IDEs buscan clase Main por convención
 * - AppMenu es más semántico como nombre de clase principal
 * - Mantener Main por compatibilidad/convención
 *
 * Uso recomendado:
 * - Para ejecutar desde IDE: Usar AppMenu.main() o Main.main() (indistinto)
 * - Para ejecutar desde JAR: Especificar AppMenu o Main en manifest
 *
 */

public class Main {
    /**
     * Punto de entrada alternativo de la aplicación Java.
     * Crea AppMenu y ejecuta el menú principal.
     *
     * Flujo:
     * 1. Crea instancia de AppMenu (inicializa toda la aplicación)
     * 2. Llama a app.run() que ejecuta el loop del menú
     * 3. Cuando el usuario sale (opción 0), run() termina y la aplicación finaliza
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.run();
    }

}
