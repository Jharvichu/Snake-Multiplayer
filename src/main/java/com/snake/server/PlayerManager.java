package main.java.com.snake.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

/**
 * Gestiona los jugadores conectados al servidor
 * Thread-safe para manejo concurrente
 */
public class PlayerManager {
    private final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
    private final AtomicInteger playerIdCounter = new AtomicInteger(1);

    /**
     * Crear nuevo jugador y asignar ID único
     */
    public int addNewPlayer() {
        int playerId = generateNewPlayerId();
        Player newPlayer = new Player(playerId);
        players.put(playerId, newPlayer);

        System.out.println("Nuevo jugador creado: Player " + playerId);
        return playerId;
    }

    /**
     * Remover jugador del juego
     */
    public void removePlayer(int playerId) {
        Player removed = players.remove(playerId);
        if (removed != null) {
            System.out.println("Jugador removido: Player " + playerId);
        }
    }

    /**
     * Buscar jugador por ID
     */
    public Player getPlayerById(int playerId) {
        return players.get(playerId);
    }

    /**
     * Obtener lista de todos los jugadores
     */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    /**
     * Generar ID único para nuevo jugador
     */
    public int generateNewPlayerId() {
        return playerIdCounter.getAndIncrement();
    }

    /**
     * Verificar si jugador está conectado
     */
    public boolean isPlayerConnected(int playerId) {
        return players.containsKey(playerId);
    }

    /**
     * Obtener número total de jugadores
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Actualizar puntuación de jugador
     */
    public void updatePlayerScore(int playerId, int score) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setScore(score);
        }
    }

    /**
     * Clase interna para representar un jugador
     */
    public static class Player {
        private final int playerId;
        private int score;
        private long connectionTime;
        private String status;

        public Player(int playerId) {
            this.playerId = playerId;
            this.score = 0;
            this.connectionTime = System.currentTimeMillis();
            this.status = "CONNECTED";
        }

        // Getters y Setters
        public int getPlayerId() { return playerId; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public long getConnectionTime() { return connectionTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() {
            return "Player{" +
                    "id=" + playerId +
                    ", score=" + score +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}