package main.java.com.snake;

import main.java.com.snake.integration.GameIntegrator;
import main.java.com.snake.server.GameServer;
import main.java.com.snake.ui.GameWindow;
import main.java.com.snake.client.GameClient;
import main.java.com.snake.client.ClientUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look and Feel
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Crear ventana del juego
            GameWindow window = new GameWindow();
            window.setVisible(true);
            
            // Conectar al servidor como jugador real
            connectToServerAsPlayer(window);
        });
    }
    
    private static void connectToServerAsPlayer(GameWindow window) {
        try {
            // Generar ID de jugador único
            int playerId = (int)(Math.random() * 10000) + 1;
            
            // Crear cliente y conectar al servidor
            GameClient client = new GameClient(playerId, "localhost", 8080);
            
            // Configurar UI del cliente
            ClientUI clientUI = new ClientUI() {
                @Override
                public void onConnected(int playerId) {
                    SwingUtilities.invokeLater(() -> {
                        window.updateConnectionStatus("Conectado como Jugador " + playerId + " - Nivel 1");
                        window.updatePlayerCount(1); // Actualizar contador de jugadores
                    });
                    System.out.println("Conectado al servidor como jugador " + playerId);
                }
                
                @Override
                public void onDisconnected(String reason) {
                    SwingUtilities.invokeLater(() -> {
                        window.updateConnectionStatus("Desconectado: " + reason);
                        window.updatePlayerCount(0);
                    });
                    System.out.println("Desconectado: " + reason);
                }
                
                @Override
                public void onGameState(String gameStateJson) {
                    SwingUtilities.invokeLater(() -> {
                        window.getGamePanel().updateGameState(gameStateJson);
                        // TODO: Parsear JSON para obtener número real de jugadores
                        // Por ahora mantener el contador actual
                    });
                }
                
                @Override
                public void onPlayerJoined(int playerId) {
                    System.out.println("Jugador " + playerId + " se unió al juego");
                    SwingUtilities.invokeLater(() -> {
                        // Se actualizará con el próximo estado del juego
                    });
                }
                
                @Override
                public void onGameOver(String winner) {
                    SwingUtilities.invokeLater(() -> {
                        window.showGameOverDialog(winner);
                    });
                }
            };
            
            client.setUi(clientUI);
            window.setGameClient(client);
            
            // Conectar al servidor
            window.updateConnectionStatus("Conectando al servidor...");
            client.connectToServer();
            client.startListening();
            
        } catch (Exception e) {
            window.updateConnectionStatus("Error de conexión: " + e.getMessage());
            System.err.println("Error conectando al servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}