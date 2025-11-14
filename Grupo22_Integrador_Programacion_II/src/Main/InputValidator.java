
package Main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 *
 * @author Maximiliano Rao
 * 
 * Clase utilitaria para validar y leer entrada del usuario.
 * Maneja conversiones de tipos y validaciones básicas.
 * 
 * Responsabilidades:
 * - Leer y validar entrada de tipos primitivos
 * - Convertir strings a mayúsculas donde aplique
 * - Validar formatos básicos (email, fechas)
 * - Mostrar mensajes de error al usuario
 * 
 */
public class InputValidator {

    private final Scanner scanner;
    
    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Lee un número entero con validación.
     */
    public int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                MenuDisplay.mostrarError("Debe ingresar un número entero válido.");
            }
        }
    }
    
    /**
     * Lee un número long con validación.
     */
    public Long leerLong(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                MenuDisplay.mostrarError("Debe ingresar un número válido.");
            }
        }
    }
    
    /**
     * Lee un string no vacío.
     */
    public String leerString(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            MenuDisplay.mostrarError("El campo no puede estar vacío.");
        }
    }
    
    /**
     * Lee un string no vacío y lo convierte a mayúsculas.
     */
    public String leerStringMayusculas(String mensaje) {
        return leerString(mensaje).toUpperCase();
    }
    
    /**
     * Lee un string opcional (puede estar vacío).
     */
    public String leerStringOpcional(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
    
    /**
     * Lee un boolean (S/N).
     */
    public boolean leerBoolean(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (S/N): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("S") || input.equals("SI")) {
                return true;
            } else if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            MenuDisplay.mostrarError("Debe ingresar S (Sí) o N (No).");
        }
    }
    
    /**
     * Lee un email con validación básica de formato.
     */
    public String leerEmail(String mensaje) {
        while (true) {
            String email = leerString(mensaje);
            if (validarFormatoEmail(email)) {
                return email.toLowerCase(); // Emails siempre en minúsculas
            }
            MenuDisplay.mostrarError("El formato del email es inválido.");
        }
    }
    
    /**
     * Lee una fecha/hora en formato específico.
     */
    public LocalDateTime leerFechaHora(String mensaje, String formato) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
        while (true) {
            try {
                System.out.print(mensaje + " (formato: " + formato + "): ");
                String input = scanner.nextLine().trim();
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                MenuDisplay.mostrarError("Formato de fecha inválido. Use: " + formato);
            }
        }
    }
    
    /**
     * Valida formato básico de email.
     */
    private boolean validarFormatoEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
    /**
     * Solicita confirmación al usuario.
     */
    public boolean confirmar(String mensaje) {
        return leerBoolean(mensaje);
    }
    
    /**
     * Pausa y espera a que el usuario presione Enter.
     */
    public void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}
