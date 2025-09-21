package main.java.com.snake.game.levels;

import java.util.*;

public class ScoreManager {
    private final Map<Integer, Integer> playerScores;
    private final Map<Integer, String> playerNames;
    private final Map<Integer, Integer> fruitsEaten;
    private final Map<Integer, Long> survivalTime;
    private final List<ScoreEntry> leaderboard;

    public ScoreManager() {
        this.playerScores = new HashMap<>();
        this.playerNames = new HashMap<>();
        this.fruitsEaten = new HashMap<>();
        this.survivalTime = new HashMap<>();
        this.leaderboard = new ArrayList<>();
    }

    // Clase para entradas del leaderboard
    public static class ScoreEntry {
        private final int playerId;
        private final String playerName;
        private final int score;
        private final int fruitsEaten;
        private final long survivalTime;

        public ScoreEntry(int playerId, String playerName, int score, int fruitsEaten, long survivalTime) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.score = score;
            this.fruitsEaten = fruitsEaten;
            this.survivalTime = survivalTime;
        }

        // Getters
        public int getPlayerId() { return playerId; }
        public int getScore() { return score; }

        @Override
        public String toString() {
            return playerName + ": " + score + " pts (" + fruitsEaten + " frutas, " + 
                   (survivalTime / 1000) + "s)";
        }
    }

    // Registrar un jugador
    public void registerPlayer(int playerId, String playerName) {
        playerScores.put(playerId, 0);
        playerNames.put(playerId, playerName != null ? playerName : "Jugador " + playerId);
        fruitsEaten.put(playerId, 0);
        survivalTime.put(playerId, System.currentTimeMillis());
    }

    // Actualizar puntaje de jugador
    public void updatePlayerScore(int playerId, int points) {
        if (playerScores.containsKey(playerId)) {
            int currentScore = playerScores.get(playerId);
            playerScores.put(playerId, currentScore + points);
            System.out.println(playerNames.get(playerId) + " obtuvo " + points + " puntos. Total: " + 
                             (currentScore + points));
        }
    }

    // Función para obtener puntaje de un jugador
    public int getPlayerScore(int playerId) {
        return playerScores.getOrDefault(playerId, 0);
    }

    // Incrementar frutas comidas
    public void addFruitEaten(int playerId, int fruitValue) {
        if (fruitsEaten.containsKey(playerId)) {
            fruitsEaten.put(playerId, fruitsEaten.get(playerId) + 1);
            updatePlayerScore(playerId, fruitValue);
        }
    }

    // Resetear todos los puntajes
    public int resetScores() {
        int playerCount = playerScores.size();
        playerScores.clear();
        playerNames.clear();
        fruitsEaten.clear();
        survivalTime.clear();
        return playerCount;
    }

    // Guardar puntaje en el leaderboard
    public void saveHighScores() {
        leaderboard.clear();
        long currentTime = System.currentTimeMillis();
        
        for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
            int playerId = entry.getKey();
            int score = entry.getValue();
            String name = playerNames.get(playerId);
            int fruits = fruitsEaten.get(playerId);
            long survival = currentTime - survivalTime.get(playerId);
            
            leaderboard.add(new ScoreEntry(playerId, name, score, fruits, survival));
        }
        
        // Ordenar por puntaje descendente
        leaderboard.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        System.out.println("Puntajes guardados en leaderboard");
    }

    // Calcular puntos bonus por cantidad de frutas + tiempo supervivencia
    public int calculateBonusPoints(int playerId) {
        if (!playerScores.containsKey(playerId)) {
            return 0;
        }
        
        int fruits = fruitsEaten.get(playerId);
        long currentTime = System.currentTimeMillis();
        long playerSurvivalTime = currentTime - survivalTime.get(playerId);
        int survivalSeconds = (int) (playerSurvivalTime / 1000);
        
        // Bonus: 5 puntos por fruta + 1 punto por cada 10 segundos de supervivencia
        int fruitBonus = fruits * 5;
        int timeBonus = survivalSeconds / 10;
        int totalBonus = fruitBonus + timeBonus;
        
        System.out.println("Bonus para " + playerNames.get(playerId) + 
                          ": " + fruitBonus + " (frutas) + " + timeBonus + " (tiempo) = " + totalBonus);
        
        return totalBonus;
    }

    // Aplicar bonus a un jugador
    public void applyBonusPoints(int playerId) {
        int bonus = calculateBonusPoints(playerId);
        updatePlayerScore(playerId, bonus);
    }

    // Obtener leaderboard ordenado
    public List<ScoreEntry> getLeaderboard() {
        saveHighScores(); // Actualizar antes de retornar
        return new ArrayList<>(leaderboard);
    }

    // Obtener top N jugadores
    public List<ScoreEntry> getTopPlayers(int n) {
        List<ScoreEntry> top = getLeaderboard();
        return top.subList(0, Math.min(n, top.size()));
    }

    // Obtener ranking de un jugador específico
    public int getPlayerRank(int playerId) {
        List<ScoreEntry> ranking = getLeaderboard();
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getPlayerId() == playerId) {
                return i + 1; // Ranking empieza en 1
            }
        }
        return -1; // No encontrado
    }

    // Obtener estadísticas de un jugador
    public String getPlayerStats(int playerId) {
        if (!playerScores.containsKey(playerId)) {
            return "Jugador no encontrado";
        }
        
        String name = playerNames.get(playerId);
        int score = playerScores.get(playerId);
        int fruits = fruitsEaten.get(playerId);
        long currentTime = System.currentTimeMillis();
        long survival = currentTime - survivalTime.get(playerId);
        int rank = getPlayerRank(playerId);
        
        return String.format("%s - Puntaje: %d, Frutas: %d, Tiempo: %ds, Ranking: #%d", 
                           name, score, fruits, (survival / 1000), rank);
    }

    // Verificar si es un nuevo record
    public boolean isNewRecord(int playerId) {
        int currentScore = getPlayerScore(playerId);
        return leaderboard.isEmpty() || currentScore > leaderboard.getFirst().getScore();
    }

    // Remover jugador del sistema de puntuación
    public void removePlayer(int playerId) {
        playerScores.remove(playerId);
        playerNames.remove(playerId);
        fruitsEaten.remove(playerId);
        survivalTime.remove(playerId);
        System.out.println("Jugador " + playerId + " ya no pertenece al Top");
    }

    // Obtener todos los jugadores activos
    public Set<Integer> getActivePlayers() {
        return new HashSet<>(playerScores.keySet());
    }

    // Imprimir leaderboard en consola
    public void printLeaderboard() {
        System.out.println("\nTop Mejores");
        List<ScoreEntry> ranking = getLeaderboard();
        for (int i = 0; i < ranking.size(); i++) {
            System.out.println((i + 1) + ". " + ranking.get(i).toString());
        }
    }
}
