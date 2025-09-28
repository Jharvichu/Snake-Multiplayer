package main.java.com.snake.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import main.java.com.snake.game.engine.GameEngine;
import main.java.com.snake.utils.Direction;
import main.java.com.snake.game.entities.Player;

/**
 * Servidor principal del juego Snake Multijugador
 * Maneja conexiones de clientes y coordina la comunicación
 */
public class GameServer {
    private ServerSocket serverSocket;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger connectedPlayers = new AtomicInteger(0);
    private final int maxPlayers;
    private final int port;

    // Thread pool para manejar clientes
    private ExecutorService clientThreadPool;

    // Monitor de rendimiento
    private PerformanceMonitor performanceMonitor;

    // Gestores de componentes
    private ServerMessageBroadcaster broadcaster;
    private PlayerManager playerManager;

    // Mapa de clientes conectados
    private final ConcurrentHashMap<Integer, ClientHandler> clients = new ConcurrentHashMap<>();

    public GameServer(int port, int maxPlayers) {
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.clientThreadPool = Executors.newCachedThreadPool();
        this.broadcaster = new ServerMessageBroadcaster();
        this.playerManager = new PlayerManager();
        this.performanceMonitor = new PerformanceMonitor();
    }

    /**
     * Iniciar el servidor en el puerto especificado
     */
    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning.set(true);

        System.out.println("Servidor iniciado en puerto " + port);
        System.out.println("Esperando conexiones... (máximo " + maxPlayers + " jugadores)");

        acceptConnections();
    }

    /**
     * Bucle principal para aceptar nuevas conexiones
     */
    public void acceptConnections() {
        while (isRunning.get()) {
            try {
                Socket clientSocket = serverSocket.accept();

                if (connectedPlayers.get() >= maxPlayers) {
                    System.out.println("Conexión rechazada: servidor lleno");
                    clientSocket.close();
                    continue;
                }

                // Crear nuevo jugador
                int playerId = playerManager.addNewPlayer();
                connectedPlayers.incrementAndGet();

                // Crear jugador en GameEngine
                Player newPlayer = new Player(playerId, "Player " + playerId);
                GameEngine.getInstance().addPlayer(newPlayer);

                // Crear handler para el cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket, playerId, this);
                clients.put(playerId, clientHandler);
                broadcaster.addClient(playerId, clientHandler);

                // Ejecutar en thread separado
                clientThreadPool.submit(clientHandler);

                System.out.println("Cliente conectado: Player " + playerId +
                        " (" + connectedPlayers.get() + "/" + maxPlayers + ")");
                System.out.println("Jugador agregado al GameEngine: " + playerId);

                // Actualizar métricas de rendimiento
                performanceMonitor.updatePeakPlayers(connectedPlayers.get());

                // Notificar a otros clientes sobre nuevo jugador
                broadcaster.broadcastToOthers("PLAYER_JOINED:" + playerId, playerId);

            } catch (IOException e) {
                if (isRunning.get()) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Enviar estado del juego a todos los clientes conectados
     */
    public void broadcastGameState(String gameState) {
        broadcaster.broadcastToAll("STATE:" + gameState);
        performanceMonitor.recordMessageSent("STATE:" + gameState);
    }

    /**
     * Remover cliente desconectado
     */
    public void removeDisconnectedClient(int playerId) {
        ClientHandler removed = clients.remove(playerId);
        if (removed != null) {
            broadcaster.removeClient(playerId);
            playerManager.removePlayer(playerId);

            // Remover del GameEngine
            GameEngine.getInstance().removePlayer(playerId);

            connectedPlayers.decrementAndGet();

            System.out.println("Cliente desconectado: Player " + playerId +
                    " (" + connectedPlayers.get() + "/" + maxPlayers + ")");

            // Notificar a otros clientes
            broadcaster.broadcastToAll("PLAYER_LEFT:" + playerId);
        }
    }

    /**
     * Procesar mensaje de movimiento de cliente
     */
    public void processPlayerMove(int playerId, String direction) {
        // Integrar con GameEngine
        Direction gameDirection = parseDirection(direction);
        if (gameDirection != null) {
            GameEngine.getInstance().processPlayerInput(playerId, gameDirection);
        }

        // También hacer broadcast para sincronizar
        broadcaster.broadcastToOthers("MOVE:" + playerId + ":" + direction, playerId);
    }

    /**
     * Parsear dirección de string a enum
     */
    private Direction parseDirection(String directionStr) {
        switch (directionStr.toUpperCase()) {
            case "UP": return Direction.UP;
            case "DOWN": return Direction.DOWN;
            case "LEFT": return Direction.LEFT;
            case "RIGHT": return Direction.RIGHT;
            default: return null;
        }
    }

    /**
     * Cerrar servidor limpiamente
     */
    public void stopServer() {
        isRunning.set(false);

        // Cerrar todas las conexiones de clientes
        for (ClientHandler client : clients.values()) {
            client.cleanupConnection();
        }
        clients.clear();

        // Cerrar thread pool
        clientThreadPool.shutdown();

        // Cerrar server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando servidor: " + e.getMessage());
        }

        System.out.println("Servidor cerrado");
    }

    // Getters
    public int getConnectedPlayersCount() {
        return connectedPlayers.get();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public ServerMessageBroadcaster getBroadcaster() {
        return broadcaster;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
}