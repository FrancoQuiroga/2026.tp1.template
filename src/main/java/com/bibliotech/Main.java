package main.java.com.bibliotech;

import main.java.com.bibliotech.exception.PrestamoException;
import main.java.com.bibliotech.exception.RecursoException;
import main.java.com.bibliotech.exception.SocioException;
import main.java.com.bibliotech.model.Categoria;
import main.java.com.bibliotech.repository.*;
import main.java.com.bibliotech.service.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // 1. CONFIGURACIÓN E INYECCIÓN DE DEPENDENCIAS
        RecursoRepository recursoRepo = new InMemoryRecursoRepository();
        SocioRepository socioRepo = new InMemorySocioRepository();
        PrestamoRepository prestamoRepo = new InMemoryPrestamoRepository();

        RecursoService recursoService = new RecursoServiceImpl(recursoRepo);
        SocioService socioService = new SocioServiceImpl(socioRepo);
        PrestamoService prestamoService = new PrestamoServiceImpl(recursoRepo, socioRepo, prestamoRepo);

        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("==========================================");
        System.out.println("   BIENVENIDO AL SISTEMA BIBLIOTECH");
        System.out.println("==========================================");

        precargarDatos(socioService, recursoService);

        while (!salir) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Registrar nuevo Socio (Estudiante)");
            System.out.println("2. Realizar Préstamo");
            System.out.println("3. Registrar Devolución");
            System.out.println("4. Mostrar Préstamos Vencidos");
            System.out.println("5. Ejecutar Test Automático de Arquitectura");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String entrada = scanner.nextLine();

            try {
                int opcion = Integer.parseInt(entrada);

                switch (opcion) {
                    case 1 -> registrarSocioCLI(scanner, socioService);
                    case 2 -> realizarPrestamoCLI(scanner, prestamoService);
                    case 3 -> registrarDevolucionCLI(scanner, prestamoService);
                    case 4 -> mostrarVencidosCLI(prestamoService);
                    case 5 -> ejecutarTestAutomatico(socioService, recursoService, prestamoService);
                    case 6 -> {
                        System.out.println("¡Gracias por usar BiblioTech! Hasta pronto.");
                        salir = true;
                    }
                    default -> System.out.println("⚠️ Opción no reconocida.");
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ Error: Debe ingresar un número válido.");
            }
        }
        scanner.close();
    }

    // --- MÉTODOS AUXILIARES (CON MANEJO DE EXCEPCIONES INTERNO) ---

    private static void registrarSocioCLI(Scanner scanner, SocioService socioService) {
        try {
            System.out.print("Ingrese DNI: ");
            int dni = Integer.parseInt(scanner.nextLine());
            System.out.print("Ingrese Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Ingrese Email: ");
            String email = scanner.nextLine();

            // El IDE ahora ve la excepción manejada explícitamente aquí
            socioService.registrarEstudiante(dni, nombre, email);
            System.out.println("✅ Socio registrado exitosamente.");

        } catch (NumberFormatException e) {
            System.err.println("❌ Error: El DNI debe ser numérico.");
        } catch (SocioException e) {
            System.err.println("❌ Error al registrar socio: " + e.getMessage());
        }
    }

    private static void realizarPrestamoCLI(Scanner scanner, PrestamoService prestamoService) {
        try {
            System.out.print("Ingrese DNI del Socio: ");
            int dni = Integer.parseInt(scanner.nextLine());
            System.out.print("Ingrese ISBN del Libro: ");
            String isbn = scanner.nextLine();

            // Capturamos las 3 excepciones exactas que declara tu interfaz
            prestamoService.realizarPrestamo(isbn, dni);
            System.out.println("✅ Préstamo procesado con éxito.");

        } catch (NumberFormatException e) {
            System.err.println("❌ Error: El DNI debe ser numérico.");
        } catch (SocioException | RecursoException | PrestamoException e) {
            System.err.println("❌ Error al procesar el préstamo: " + e.getMessage());
        }
    }

    private static void registrarDevolucionCLI(Scanner scanner, PrestamoService prestamoService) {
        try {
            System.out.print("Ingrese el ID del Préstamo: ");
            int idPrestamo = Integer.parseInt(scanner.nextLine());

            prestamoService.registrarDevolucion(idPrestamo);
            System.out.println("✅ Devolución completada.");

        } catch (NumberFormatException e) {
            System.err.println("❌ Error: El ID de préstamo debe ser numérico.");
        } catch (PrestamoException e) {
            System.err.println("❌ Error en la devolución: " + e.getMessage());
        }
    }

    private static void mostrarVencidosCLI(PrestamoService prestamoService) {
        try {
            var vencidos = prestamoService.obtenerPrestamosVencidos();
            if (vencidos.isEmpty()) {
                System.out.println("✅ No hay préstamos vencidos.");
            } else {
                System.out.println("⚠️ LISTADO DE VENCIMIENTOS:");
                vencidos.forEach(p -> System.out.println("- ID: " + p.idPrestamo() + " | Libro: " + p.recurso().titulo()));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener vencidos: " + e.getMessage());
        }
    }

    // --- TEST AUTOMÁTICO DE ARQUITECTURA ---
    private static void ejecutarTestAutomatico(SocioService socioService, RecursoService recursoService, PrestamoService prestamoService) {
        System.out.println("\n>>> INICIANDO TEST DE INTEGRACIÓN AUTOMÁTICO <<<");
        try {
            System.out.println("1. Registrando Socio de prueba...");
            socioService.registrarEstudiante(99999999, "Usuario Test", "test@universidad.edu");
            System.out.println("  -> OK");

            System.out.println("2. Registrando Recurso de prueba...");
            recursoService.registrarLibroFisico("TEST-123", "Libro de Test", "Autor Automático", 2026, Categoria.CIENCIA, "Estante T");
            System.out.println("  -> OK");

            System.out.println("3. Ejecutando Lógica de Préstamo...");
            prestamoService.realizarPrestamo("TEST-123", 99999999);
            System.out.println("  -> OK (Préstamo generado correctamente)");

            System.out.println("4. Ejecutando Lógica de Devolución...");
            // Asumimos el ID 1 ya que es el primer préstamo generado por el generador AtomicInteger en memoria
            prestamoService.registrarDevolucion(1);
            System.out.println("  -> OK (Devolución procesada correctamente)");

            System.out.println(">>> TEST FINALIZADO CON ÉXITO: TODAS LAS CAPAS FUNCIONAN <<<");

        } catch (SocioException | RecursoException | PrestamoException e) {
            System.err.println(">>> EL TEST FALLÓ POR REGLA DE NEGOCIO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(">>> EL TEST FALLÓ POR ERROR CRÍTICO: " + e.getMessage());
        }
    }

    private static void precargarDatos(SocioService sS, RecursoService rS) {
        try {
            sS.registrarEstudiante(12345678, "Franco Quiroga", "franco@u.edu");
            rS.registrarLibroFisico("978-Java", "Java Moderno", "Autor X", 2024, Categoria.PROGRAMACION, "A1");
        } catch (Exception ignored) {}
    }
}