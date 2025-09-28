package main.java.com.snake.integration;

import main.java.com.snake.game.engine.GameEngine;
import main.java.com.snake.game.entities.GameState;
import main.java.com.snake.game.entities.Player;
import main.java.com.snake.server.GameServer;
import main.java.com.snake.utils.Direction;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Integrador principal que conecta Server ↔ GameEngine
 * Esta es la pieza crítica que faltaba para unir todo el sistema
 */
public class GameIntegrator {
    private static GameIntegrator instance;
    private GameServer gameServer;
    private GameEngine gameEngine;
    private GameStateSerializer serializer;
    private Timer gameLoopTimer;
    private boolean isRunning = false;

    // Mapeo de playerIds para sincronización
    private ConcurrentHashMap<Integer, Player> playerMapping;

    private GameIntegrator() {
        this.gameEngine = GameEngine.getInstance();
        this.serializer = new GameStateSerializer();
        this.playerMapping = new ConcurrentHashMap<>();
        setupGameEngineObserver();
    }

    public static GameIntegrator getInstance() {
        if (instance == null) {
            synchronized (GameIntegrator.class) {
                if (instance == null) {
                    instance = new GameIntegrator();
                }
            }
        }
        return instance;
    }

    /**
     * Inicializar todos los componentes del sistema
     */
    public void initializeAllComponents() {
        System.out.println("[GameIntegrator] Inicializando componentes...");

        // El GameEngine ya está inicializado (Singleton)
        // El GameServer se inicializa externamente

        System.out.println("[GameIntegrator] Componentes inicializados");
    }

    /**
     * Conectar el servidor con el motor del juego
     */
    public void connectServerToEngine(GameServer server) {
        this.gameServer = server;
        System.out.println("[GameIntegrator] Servidor conectado al motor del juego");
    }

    /**
     * Iniciar el bucle principal del juego
     */
    public void startGameLoop() {
        if (isRunning) return;

        isRunning = true;
        gameLoopTimer = new Timer("GameLoop", true);

        // Timer para broadcast del estado cada 100ms
        gameLoopTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    broadcastGameState();
                } catch (Exception e) {
                    System.err.println("[GameIntegrator] Error en game loop: " + e.getMessage());
                }
            }
        }, 0, 100); // Cada 100ms

        System.out.println("[GameIntegrator] Game loop iniciado");
    }

    /**
     * Procesar movimiento de jugador (llamado desde GameServer)
     */
    public void processPlayerMove(int playerId, String direction) {
        try {
            Direction gameDirection = parseDirection(direction);
            if (gameDirection != null) {
                // Enviar movimiento al GameEngine
                gameEngine.processPlayerInput(playerId, gameDirection);
            }
        } catch (Exception e) {
            System.err.println("[GameIntegrator] Error procesando movimiento: " + e.getMessage());
        }
    }

    /**
     * Agregar jugador al sistema (sincronizar Server ↔ Engine)
     */
    public boolean addPlayer(int playerId, String playerName) {
        try {
            // Crear jugador real
            Player newPlayer = new Player(playerId, playerName);

            // Agregar al GameEngine
            boolean success = gameEngine.addPlayer(newPlayer);

            if (success) {
                playerMapping.put(playerId, newPlayer);
                System.out.println("[GameIntegrator] Jugador agregado: " + playerName + " (ID: " + playerId + ")");

                // Broadcast que se unió un jugador
                if (gameServer != null) {
                    gameServer.getBroadcaster().broadcastToAll("PLAYER_JOINED:" + playerId);
                }
            }

            return success;
        } catch (Exception e) {
            System.err.println("[GameIntegrator] Error agregando jugador: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remover jugador del sistema
     */
    public void removePlayer(int playerId) {
        try {
            // Remover del GameEngine
            gameEngine.removePlayer(playerId);

            // Remover del mapeo local
            Player removed = playerMapping.remove(playerId);

            if (removed != null) {
                System.out.println("[GameIntegrator] Jugador removido: " + removed.getPlayerName());

                // Broadcast que se fue un jugador
                if (gameServer != null) {
                    gameServer.getBroadcaster().broadcastToAll("PLAYER_LEFT:" + playerId);
                }
            }
        } catch (Exception e) {
            System.err.println("[GameIntegrator] Error removiendo jugador: " + e.getMessage());
        }
    }

    /**
     * Broadcast del estado del juego a todos los clientes
     */
    private void broadcastGameState() {
        if (gameServer == null) return;

        try {
            // Obtener estado actual del GameEngine
            GameState currentState = gameEngine.getGameState();

            // Serializar a JSON
            String gameStateJson = serializer.toJson(currentState);

            // Enviar a todos los clientes
            gameServer.getBroadcaster().broadcastToAll("STATE:" + gameStateJson);

        } catch (Exception e) {
            System.err.println("[GameIntegrator] Error en broadcast: " + e.getMessage());
        }
    }

    /**
     * Configurar observador del GameEngine para eventos importantes
     */
    private void setupGameEngineObserver() {
        gameEngine.addObserver((event, gameState) -> {
            if (gameServer == null) return;

            switch (event) {
                case "GAME_START":
                    gameServer.getBroadcaster().broadcastToAll("GAME_STARTED");
                    break;
                case "GAME_END":
                    // Determinar ganador
                    Player winner = gameState.getLeadingPlayer();
                    String winnerName = winner != null ? winner.getPlayerName() : "Empate";
                    gameServer.getBroadcaster().broadcastToAll("GAME_OVER:" + winnerName);
                    break;
                case "FRUIT_EATEN":
                    // Opcional: sonido o efecto especial
                    break;
                case "COLLISION":
                    // Opcional: efectos de colisión
                    break;
            }
        });
    }

    /**
     * Iniciar juego si hay suficientes jugadores
     */
    public void tryStartGame() {
        if (playerMapping.size() >= 2) { // Mínimo 2 jugadores
            try {
                gameEngine.startGame();
                System.out.println("[GameIntegrator] Juego iniciado con " + playerMapping.size() + " jugadores");
            } catch (Exception e) {
                System.err.println("[GameIntegrator] Error iniciando juego: " + e.getMessage());
            }
        }
    }

    /**
     * Parsear dirección de string a enum
     */
    private Direction parseDirection(String directionStr) {
        if (directionStr == null) return null;

        switch (directionStr.toUpperCase()) {
            case "UP": return Direction.UP;
            case "DOWN": return Direction.DOWN;
            case "LEFT": return Direction.LEFT;
            case "RIGHT": return Direction.RIGHT;
            default: return null;
        }
    }

    /**
     * Manejo de errores críticos
     */
    public void handleCriticalError(String error, Exception e) {
        System.err.println("[GameIntegrator] ERROR CRÍTICO: " + error);
        if (e != null) {
            e.printStackTrace();
        }

        // Notificar a todos los clientes del error
        if (gameServer != null) {
            gameServer.getBroadcaster().broadcastToAll("ERROR:" + error);
        }
    }

    /**
     * Cerrar todo limpiamente
     */
    public void shutdownGracefully() {
        isRunning = false;

        if (gameLoopTimer != null) {
            gameLoopTimer.cancel();
        }

        if (gameEngine != null) {
            gameEngine.stopGame();
        }

        playerMapping.clear();

        System.out.println("[GameIntegrator] Sistema cerrado limpiamente");
    }

    // Getters
    public boolean isRunning() { return isRunning; }
    public int getPlayerCount() { return playerMapping.size(); }
    public GameEngine getGameEngine() { return gameEngine; }
}