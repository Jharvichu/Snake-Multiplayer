package main.java.com.snake.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

/** =====================
 * PUNTO DE ENTRADA
 * =====================
 * Uso:
 * java com.snake.client.ClientMain [ip] [port] [playerId]
 * Ejemplos:
 * java com.snake.client.ClientMain
 * java com.snake.client.ClientMain 127.0.0.1 9000 42
 */
public class ClientMain {
    public static void main(String[] args) {
        final String ip = args.length > 0 ? args[0] : "127.0.0.1";
        final int port = args.length > 1 ? Integer.parseInt(args[1]) : 9000;
        final int playerId = args.length > 2 ? Integer.parseInt(args[2]) : new Random().nextInt(10_000) + 1;


        System.out.printf("[Client] Iniciando... ip=%s port=%d playerId=%d%n", ip, port, playerId);


        // UI mínima de consola para empezar a probar (sustituir por GameWindow más adelante)
        ClientUI consoleUI = new ClientUI() {
            @Override public void onConnected(int pid) { System.out.println("[Client] Conectado como player=" + pid); }
            @Override public void onDisconnected(String reason) { System.out.println("[Client] Desconectado: " + reason); }
            @Override public void onGameState(String rawStateJson) { System.out.println("[STATE] " + rawStateJson); }
            @Override public void onPlayerJoined(int pid) { System.out.println("[JOINED] " + pid); }
            @Override public void onGameOver(String winnerId) { System.out.println("[GAMEOVER] winner=" + winnerId); }
        };


        try (GameClient client = new GameClient(playerId, ip, port)) {
            client.setUi(consoleUI);
            client.connectToServer();
            client.startListening();


            // Bucle simple de demo: lee movimientos desde stdin y los envía
            try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.println("Ingresa movimientos (W/A/S/D o UP/DOWN/LEFT/RIGHT). Escribe EXIT para salir.");
                for (String line; (line = stdin.readLine()) != null; ) {
                    String s = line.trim();
                    if (s.equalsIgnoreCase("EXIT")) break;
                    if (s.isEmpty()) continue;
                    client.sendMovement(s);
                }
            }
        } catch (Exception e) {
            System.err.println("[Client] Error fatal: " + e.getMessage());
        }


        System.out.println("[Client] Bye.");
    }
}
