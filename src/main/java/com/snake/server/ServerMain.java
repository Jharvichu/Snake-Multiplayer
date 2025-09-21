package main.java.com.snake.server;

import java.io.IOException;
import java.util.Scanner;

/**
 * Punto de entrada del servidor Snake Multijugador
 * Maneja configuración inicial y lifecycle del servidor
 */
public class ServerMain {
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_MAX_PLAYERS = 4;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        int maxPlayers = DEFAULT_MAX_PLAYERS;

        // Parsear argumentos de línea de comandos
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto inválido: " + args[0] + ". Usando puerto por defecto: " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        if (args.length >= 2) {
            try {
                maxPlayers = Integer.parseInt(args[1]);
                if (maxPlayers < 1 || maxPlayers > 10) {
                    System.err.println("Número de jugadores debe estar entre 1 y 10. Usando valor por defecto: " + DEFAULT_MAX_PLAYERS);
                    maxPlayers = DEFAULT_MAX_PLAYERS;
                }
            } catch (NumberFormatException e) {
                System.err.println("Número de jugadores inválido: " + args[1] + ". Usando valor por defecto: " + DEFAULT_MAX_PLAYERS);
                maxPlayers = DEFAULT_MAX_PLAYERS;
            }
        }

        System.out.println("========================================");
        System.out.println("    SNAKE MULTIJUGADOR - SERVIDOR");
        System.out.println("========================================");
        System.out.println("Puerto: " + port);
        System.out.println("Máx jugadores: " + maxPlayers);
        System.out.println("========================================");
        System.out.println("Comandos disponibles:");
        System.out.println("  'status' - Ver estado del servidor");
        System.out.println("  'players' - Lista de jugadores conectados");
        System.out.println("  'stop' - Detener servidor");
        System.out.println("========================================");

        GameServer server = new GameServer(port, maxPlayers);

        // Thread para comandos de consola
        Thread consoleThread = new Thread(() -> handleConsoleCommands(server));
        consoleThread.setDaemon(true);
        consoleThread.start();

        // Iniciar servidor
        try {
            server.startServer();
        } catch (IOException e) {
            System.err.println("Error iniciando servidor: " + e.getMessage());
            System.err.println("Posibles causas:");
            System.err.println("- Puerto " + port + " ya está en uso");
            System.err.println("- Sin permisos para usar el puerto");
            System.err.println("- Firewall bloqueando la conexión");
            System.exit(1);
        }
    }

    /**
     * Manejar comandos de consola del servidor
     */
    private static void handleConsoleCommands(GameServer server) {
        Scanner scanner = new Scanner(System.in);

        while (server.isRunning()) {
            try {
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "status":
                        System.out.println("Estado del servidor:");
                        System.out.println("  Ejecutándose: " + server.isRunning());
                        System.out.println("  Jugadores conectados: " + server.getConnectedPlayersCount());
                        System.out.println("  Memoria usada: " + getMemoryUsage());
                        break;

                    case "players":
                        System.out.println("Jugadores conectados:");
                        if (server.getConnectedPlayersCount() == 0) {
                            System.out.println("  Ningún jugador conectado");
                        } else {
                            // TODO: Implementar lista detallada de jugadores
                            System.out.println("  Total: " + server.getConnectedPlayersCount() + " jugadores");
                        }
                        break;

                    case "stop":
                        System.out.println("Deteniendo servidor...");
                        server.stopServer();
                        System.exit(0);
                        break;

                    case "help":
                        System.out.println("Comandos disponibles:");
                        System.out.println("  status  - Ver estado del servidor");
                        System.out.println("  players - Lista de jugadores conectados");
                        System.out.println("  stop    - Detener servidor");
                        System.out.println("  help    - Mostrar esta ayuda");
                        break;

                    case "":
                        // Línea vacía, ignorar
                        break;

                    default:
                        System.out.println("Comando desconocido: '" + command + "'. Escribe 'help' para ver comandos disponibles.");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error procesando comando: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Obtener información de uso de memoria
     */
    private static String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return String.format("%.2f MB / %.2f MB",
                usedMemory / 1024.0 / 1024.0,
                totalMemory / 1024.0 / 1024.0);
    }
}