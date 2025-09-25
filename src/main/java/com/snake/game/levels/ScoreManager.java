package main.java.com.snake.game.levels;

import java.util.*;

/**
 * Gestor de puntuación
 * - playerScores: Puntuación por jugador {"jugador", "puntos"}
 * - playerNames: Nombre e ID de jugador {"nombre", "ID"}
 */
public class ScoreManager {
    private Map<Integer, Integer> playerScores;
    private Map<Integer, String> playerNames;

    public ScoreManager() {
        this.playerScores = new HashMap<>();
        this.playerNames = new HashMap<>();
    }

    // Registrar un jugador
    public void addPlayer(int playerId, String playerName) {
        playerScores.put(playerId, 0);
        playerNames.put(playerId, playerName != null ? playerName : "Jugador " + playerId);
    }

    // Actualizar puntaje de jugador
    public void updateScore(int playerId, int points) {
        if (playerScores.containsKey(playerId)) {
            int currentScore = playerScores.get(playerId);
            playerScores.put(playerId, currentScore + points);
        }
    }

    // Obtener puntaje de un jugador
    public int getScore(int playerId) {
        return playerScores.getOrDefault(playerId, 0);
    }

    // Obtener nombre de un jugador
    public String getPlayerName(int playerId) {
        return playerNames.getOrDefault(playerId, "Desconocido");
    }

    // Obtener ranking ordenado
    public List<Integer> getRanking() {
        List<Integer> ranking = new ArrayList<>(playerScores.keySet());
        ranking.sort((a, b) -> Integer.compare(playerScores.get(b), playerScores.get(a)));
        return ranking;
    }

    // Obtener posición de un jugador en el ranking
    public int getPlayerPosition(int playerId) {
        List<Integer> ranking = getRanking();
        return ranking.indexOf(playerId) + 1; // +1 porque el ranking empieza en 1
    }

    // Resetear todos los puntajes
    public void resetAllScores() {
        for (Integer playerId : playerScores.keySet()) {
            playerScores.put(playerId, 0);
        }
    }

    // Remover jugador
    public void removePlayer(int playerId) {
        playerScores.remove(playerId);
        playerNames.remove(playerId);
    }

    // Obtener jugador con mayor puntaje
    public int getTopPlayer() {
        return playerScores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    // Obtener todos los jugadores activos
    public Set<Integer> getAllPlayers() {
        return playerScores.keySet();
    }

    // Imprimir ranking en consola
    public void printRanking() {
        System.out.println("\nRANKING");
        List<Integer> ranking = getRanking();
        for (int i = 0; i < ranking.size(); i++) {
            int playerId = ranking.get(i);
            System.out.println((i + 1) + ". " + playerNames.get(playerId) + 
                             ": " + playerScores.get(playerId) + " puntos");
        }
    }

    // Verificar si hay jugadores registrados
    public boolean hasPlayers() {
        return !playerScores.isEmpty();
    }

    // Obtener cantidad de jugadores
    public int getPlayerCount() {
        return playerScores.size();
    }
}
