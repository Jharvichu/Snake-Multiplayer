package main.java.com.snake.game.entities;

import main.java.com.snake.utils.Constants;
import main.java.com.snake.utils.GameStatus;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState {
    private GameStatus gameStatus;
    private long gameStartTime;
    private long gameDuration;
    private int roundNumber;
    
    // Información de jugadores
    private Map<Integer, Player> playersInfo;
    private List<Integer> playerRanking;
    
    // Información del tablero
    private Set<Point> occupiedPositions;
    private List<Fruit> activeFruits;
    
    // Estadísticas del juego
    private int totalFruitsEaten;
    private long totalGameTime;
    
    // Observadores del estado
    private List<GameStateObserver> observers;
    
    // Metadatos del juego
    private Properties gameSettings;
    
    public GameState() {

        this.gameStatus = GameStatus.WAITING_FOR_PLAYERS;
        this.gameStartTime = 0;
        this.gameDuration = 0;
        this.roundNumber = 1;
        
        this.playersInfo = new HashMap<>();
        this.playerRanking = new ArrayList<>();
        this.occupiedPositions = new HashSet<>();
        this.activeFruits = new ArrayList<>();
        
        this.totalFruitsEaten = 0;
        this.totalGameTime = 0;
        
        this.observers = new CopyOnWriteArrayList<>();
        this.gameSettings = new Properties();
        
        loadDefaultSettings();
    }

    // Configuraciones predeterminadas
    private void loadDefaultSettings() {
        gameSettings.setProperty("maxPlayers", String.valueOf(Constants.MAX_PLAYERS));
        gameSettings.setProperty("gameSpeed", String.valueOf(Constants.GAME_SPEED));
        gameSettings.setProperty("boardWidth", String.valueOf(Constants.GRID_WIDTH));
        gameSettings.setProperty("boardHeight", String.valueOf(Constants.GRID_HEIGHT));
    }

    public synchronized void updateState(Collection<Player> players, List<Fruit> fruits) {

        updatePlayersInfo(players);
        updatePlayerRanking();
        updateOccupiedPositions(players, fruits);

        this.activeFruits = new ArrayList<>(fruits);
        
        if (gameStatus == GameStatus.RUNNING && gameStartTime > 0) {
            this.gameDuration = System.currentTimeMillis() - gameStartTime;
        }

        notifyObservers("STATE_UPDATED");
    }
    
    private void updatePlayersInfo(Collection<Player> players) {
        for (Player player : players) {
            playersInfo.put(player.getPlayerId(), player);
        }
    }
    
    // Actualiza el ranking de jugadores por puntuación
    private void updatePlayerRanking() {
        playerRanking.clear();
        playerRanking.addAll(playersInfo.keySet());
        
        // Ordenar por puntuación descendente, luego por tiempo de supervivencia
        playerRanking.sort((id1, id2) -> {
            Player p1 = playersInfo.get(id1);
            Player p2 = playersInfo.get(id2);
            
            int scoreComparison = Integer.compare(p2.getScore(), p1.getScore());
            if (scoreComparison != 0) return scoreComparison;
            
            boolean p1Alive = p1.getSnake() != null && p1.getSnake().getAlive();
            boolean p2Alive = p2.getSnake() != null && p2.getSnake().getAlive();
            if (p1Alive != p2Alive) return p1Alive ? -1 : 1;
            
            int p1Length = p1.getSnake() != null ? p1.getSnake().getBody().size() : 0;
            int p2Length = p2.getSnake() != null ? p2.getSnake().getBody().size() : 0;
            return Integer.compare(p2Length, p1Length);
        });
    }
    
    private void updateOccupiedPositions(Collection<Player> players, List<Fruit> fruits) {
        occupiedPositions.clear();
        
        for (Player player : players) {
            if (player.getSnake() != null) {
                occupiedPositions.addAll(player.getSnake().getBody());
            }
        }

        for (Fruit fruit : fruits) {
            occupiedPositions.add(fruit.getPosition());
        }
    }
    
    public synchronized void setGameStatus(GameStatus newStatus) {
        GameStatus oldStatus = this.gameStatus;
        this.gameStatus = newStatus;
        handleStatusTransition(oldStatus, newStatus);
        notifyObservers("STATUS_CHANGED");
    }
    
    private void handleStatusTransition(GameStatus oldStatus, GameStatus newStatus) {
        switch (newStatus) {
            case RUNNING:
                if (oldStatus != GameStatus.PAUSED) {
                    this.gameStartTime = System.currentTimeMillis();
                }
                break; 
            case FINISHED:
                this.totalGameTime = this.gameDuration;
                break; 
            case STOPPED:
                if (oldStatus == GameStatus.RUNNING || oldStatus == GameStatus.PAUSED) {
                    this.totalGameTime = this.gameDuration;
                }
                break;
        }
    }
    
    public synchronized void addPlayer(Player player) {
        playersInfo.put(player.getPlayerId(), player);
        updatePlayerRanking();
        notifyObservers("PLAYER_ADDED");
    }
    
    public synchronized void removePlayer(int playerId) {
        playersInfo.remove(playerId);
        playerRanking.remove(Integer.valueOf(playerId));
        notifyObservers("PLAYER_REMOVED");
    }
    
    public synchronized void fruitEaten(int playerId, Fruit fruit) {
        totalFruitsEaten++;
        notifyObservers("FRUIT_EATEN");
    }
    
    public Player getLeadingPlayer() {
        if (playerRanking.isEmpty()) return null;
        return playersInfo.get(playerRanking.get(0));
    }
    
    public GameStatistics getStatistics() {
        return new GameStatistics(
            gameDuration,
            totalFruitsEaten,
            playersInfo.size(),
            roundNumber,
            gameStartTime
        );
    }

    // MÉTODOS DE OBSERVADOR ===
    
    public void addObserver(GameStateObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers(String event) {
        for (GameStateObserver observer : observers) {
            observer.onStateChanged(event, this);
        }
    }
    
    // === GETTERS ===
    
    public GameStatus getGameStatus() { return gameStatus; }
    public long getGameStartTime() { return gameStartTime; }
    public long getGameDuration() { return gameDuration; }
    public int getRoundNumber() { return roundNumber; }
    public Map<Integer, Player> getPlayersInfo() { return new HashMap<>(playersInfo); }
    public List<Integer> getPlayerRanking() { return new ArrayList<>(playerRanking); }
    public Set<java.awt.Point> getOccupiedPositions() { return new HashSet<>(occupiedPositions); }
    public List<Fruit> getActiveFruits() { return new ArrayList<>(activeFruits); }
    public int getTotalFruitsEaten() { return totalFruitsEaten; }
    public Properties getGameSettings() { return (Properties) gameSettings.clone(); }

    
    /**
     * Clase para estadísticas del juego
     */
    public static class GameStatistics {
        private final long duration;
        private final int totalFruitsEaten;
        private final int playerCount;
        private final int roundNumber;
        private final long startTime;
        
        public GameStatistics(long duration, int totalFruitsEaten, int playerCount, 
                            int roundNumber, long startTime) {
            this.duration = duration;
            this.totalFruitsEaten = totalFruitsEaten;
            this.playerCount = playerCount;
            this.roundNumber = roundNumber;
            this.startTime = startTime;
        }
        
        // Getters
        public long getDuration() { return duration; }
        public int getTotalFruitsEaten() { return totalFruitsEaten; }
        public int getPlayerCount() { return playerCount; }
        public int getRoundNumber() { return roundNumber; }
        public long getStartTime() { return startTime; }
    }
    
    /**
     * Interface para observadores del GameState
     */
    public interface GameStateObserver {
        void onStateChanged(String event, GameState gameState);
    }
}
