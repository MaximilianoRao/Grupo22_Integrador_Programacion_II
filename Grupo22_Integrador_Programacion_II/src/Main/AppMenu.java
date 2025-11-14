
package Main;

import Dao.CredencialAccesoDAO;
import Dao.UsuarioDAO;
import Service.CredencialAccesoServiceImpl;
import Service.UsuarioServiceImpl;
import java.util.Scanner;

/**
 *
 * @author Maximiliano Rao
 * 
 * Orquestador principal del menú de la aplicación.
 * Gestiona el ciclo de vida del menú y coordina todas las dependencias.
 *
 * Responsabilidades:
 * - Crear y gestionar el Scanner único (evita múltiples instancias de System.in)
 * - Inicializar toda la cadena de dependencias (DAOs → Services → Handler)
 * - Ejecutar el loop principal del menú
 * - Manejar la selección de opciones y delegarlas a MenuHandler
 * - Cerrar recursos al salir (Scanner)
 *
 * Patrón: Application Controller + Dependency Injection manual
 * Arquitectura: Punto de entrada que ensambla las 4 capas (Main → Service → DAO → Models)
 *
 * IMPORTANTE: Esta clase NO tiene lógica de negocio ni de UI.
 * Solo coordina y delega.
 * 
 */
public class AppMenu {
     /**
     * Scanner único compartido por toda la aplicación.
     * IMPORTANTE: Solo debe haber UNA instancia de Scanner(System.in).
     * Múltiples instancias causan problemas de buffering de entrada.
     */
    private final Scanner scanner;

    /**
     * Handler que ejecuta las operaciones del menú.
     * Contiene toda la lógica de interacción con el usuario.
     */
    private final MenuHandler menuHandler;

    /**
     * Flag que controla el loop principal del menú.
     * Se setea a false cuando el usuario selecciona "0 - Salir".
     */
    private boolean running;

    /**
     * Constructor que inicializa la aplicación.
     *
     * Flujo de inicialización:
     * 1. Crea Scanner único para toda la aplicación
     * 2. Crear InputValidator
     * 3. Crear DAOs (capa de acceso a datos)
     * 4. Crear Services (capa de lógica de negocio)
     * 5. Crear MenuHandler (capa de presentación/controlador)
     * 6. Setea running=true para iniciar el loop
     *
     * Patrón de inyección de dependencias (DI) manual:
     *
     * Esta inicialización garantiza que todas las dependencias estén correctamente conectadas.
     */
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        InputValidator validator = new InputValidator(scanner);
        CredencialAccesoDAO credencialAccesoDAO = new CredencialAccesoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO(credencialAccesoDAO);
        CredencialAccesoServiceImpl credencialService = new CredencialAccesoServiceImpl(credencialAccesoDAO);
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(usuarioDAO, credencialAccesoDAO);
        this.menuHandler = new MenuHandler(usuarioService, credencialService, validator);
        this.running = true;
    }
    
    
    /**
     * 
     * Punto de entrada de la aplicación Java.
     * Crea instancia de AppMenu y ejecuta el menú principal.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.run();
    }
     
     /**
     * Loop principal del menú.
     *
     * Flujo:
     * 1. Mientras running==true:
     *    a. Muestra menú con MenuDisplay.mostrarMenuPrincipal()
     *    b. Lee opción del usuario (scanner.nextLine())
     *    c. Convierte a int (puede lanzar NumberFormatException)
     *    d. Procesa opción con processOption()
     * 2. Si el usuario ingresa texto no numérico: Muestra mensaje de error y continúa
     * 3. Cuando running==false (opción 0): Sale del loop y cierra Scanner
     *
     * Manejo de errores:
     * - NumberFormatException: Captura entrada no numérica (ej: "abc")
     * - Muestra mensaje amigable y NO termina la aplicación
     * - El usuario puede volver a intentar
     *
     * IMPORTANTE: El Scanner se cierra al salir del loop.
     * Cerrar Scanner(System.in) cierra System.in para toda la aplicación.
     */
    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                processOption(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor, ingrese un numero.");
            }
        }
        scanner.close();
    }

    /**
     * Procesa la opción seleccionada por el usuario y delega a MenuHandler.
     *
     *
     * Mapeo de opciones (corresponde a MenuDisplay)
     * 0  → Salir (setea running=false para terminar el loop)
     *
     * Opción inválida: Muestra mensaje y continúa el loop.
     *
     * IMPORTANTE: Todas las excepciones de MenuHandler se capturan dentro de los métodos.
     * processOption() NO propaga excepciones al caller (run()).
     *
     * @param opcion Número de opción ingresado por el usuario
     */
    private void processOption(int opcion) {
        switch (opcion) {
            case 1 -> menuHandler.crearUsuarioConCredencial();
            case 2 -> menuHandler.listarUsuarios();
            case 3 -> menuHandler.buscarUsuarioPorId();
            case 4 -> menuHandler.buscarUsuarioPorUsername();
            case 5 -> menuHandler.buscarUsuarioPorEmail();
            case 6 -> menuHandler.actualizarUsuario();
            case 7 -> menuHandler.eliminarUsuario();
            case 8 -> menuHandler.activarUsuario();
            case 9 -> menuHandler.desactivarUsuario();
            case 10 -> menuHandler.crearCredencialIndependiente();
            case 11 -> menuHandler.listarCredenciales();
            case 12 -> menuHandler.buscarCredencialPorId();
            case 13 -> menuHandler.actualizarCredencial();
            case 14 -> menuHandler.eliminarCredencial();
            case 15 -> menuHandler.cambiarPassword();
            case 0 -> {
                System.out.println("Saliendo...");
                running = false;
            }
            default -> MenuDisplay.mostrarError("Opción no válida. Intente nuevamente.");
        }
    }

    

    }


