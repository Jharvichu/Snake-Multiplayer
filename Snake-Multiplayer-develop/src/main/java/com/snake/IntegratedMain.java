package main.java.com.snake;

import main.java.com.snake.integration.GameIntegrator;
import main.java.com.snake.server.GameServer;
import main.java.com.snake.ui.GameWindow;

public class IntegratedMain {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("server")) {
            // Modo servidor
            startServer();
        } else {
            // Modo cliente con UI
            startClientWithUI();
        }
    }

    private static void startServer() {
        try {
            GameIntegrator integrator = GameIntegrator.getInstance();
            integrator.initializeAllComponents();

            GameServer server = new GameServer(8080, 4);
            server.startServer();

        } catch (Exception e) {
            System.err.println("Error iniciando servidor: " + e.getMessage());
        }
    }

    private static void startClientWithUI() {
        // Iniciar GameWindow que ya tiene todo integrado
        GameWindow.main(new String[0]);
    }
}