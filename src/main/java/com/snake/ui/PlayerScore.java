package main.java.com.snake.ui;

/**
 * Clase que representa el puntaje y estadísticas de un jugador
 * @author Ariana
 */
public class PlayerScore {
    private int playerId;
    private String name;
    private int score;
    private int fruitsEaten;
    private boolean alive;
    private long joinTime;
    private long deathTime;
    private long lastScoreChange;

    // Para animaciones
    private int previousScore;
    private static final long SCORE_ANIMATION_DURATION = 2000; // 2 segundos

    public PlayerScore(int playerId, String name, int score, int fruitsEaten, boolean alive, long joinTime) {
        this.playerId = playerId;
        this.name = name;
        this.score = score;
        this.fruitsEaten = fruitsEaten;
        this.alive = alive;
        this.joinTime = joinTime;
        this.deathTime = 0;
        this.lastScoreChange = joinTime;
        this.previousScore = score;
    }

    // Getters
    public int getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getFruitsEaten() {
        return fruitsEaten;
    }

    public boolean isAlive() {
        return alive;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public long getDeathTime() {
        return deathTime;
    }

    public long getLastScoreChange() {
        return lastScoreChange;
    }

    // Setters y métodos de actualización
    public void setName(String name) {
        this.name = name;
    }

    public void updateScore(int newScore, int newFruitsEaten, boolean newAliveStatus) {
        if (newScore != this.score) {
            this.previousScore = this.score;
            this.score = newScore;
            this.lastScoreChange = System.currentTimeMillis();
        }

        this.fruitsEaten = newFruitsEaten;

        // Si el jugador murió, registrar tiempo de muerte
        if (this.alive && !newAliveStatus) {
            this.deathTime = System.currentTimeMillis();
        }

        this.alive = newAliveStatus;
    }

    public void kill() {
        this.alive = false;
        this.deathTime = System.currentTimeMillis();
    }

    public void revive() {
        this.alive = true;
        this.deathTime = 0;
    }

    public void reset(long gameStartTime) {
        this.score = 0;
        this.fruitsEaten = 0;
        this.alive = true;
        this.joinTime = gameStartTime;
        this.deathTime = 0;
        this.lastScoreChange = gameStartTime;
        this.previousScore = 0;
    }

    // Métodos utilitarios
    public boolean hasRecentScoreChange() {
        return (System.currentTimeMillis() - lastScoreChange) < SCORE_ANIMATION_DURATION;
    }

    public int getScoreIncrease() {
        return score - previousScore;
    }

    public long getSurvivalTime() {
        if (alive) {
            return System.currentTimeMillis() - joinTime;
        } else {
            return deathTime - joinTime;
        }
    }

    public double getScorePerMinute() {
        long survivalTimeMs = getSurvivalTime();
        if (survivalTimeMs <= 0) return 0.0;

        double minutes = survivalTimeMs / 60000.0;
        return score / minutes;
    }

    public double getFruitsPerMinute() {
        long survivalTimeMs = getSurvivalTime();
        if (survivalTimeMs <= 0) return 0.0;

        double minutes = survivalTimeMs / 60000.0;
        return fruitsEaten / minutes;
    }

    // Métodos para comparación y ordenamiento
    public int compareByScore(PlayerScore other) {
        return Integer.compare(other.score, this.score); // Orden descendente
    }

    public int compareBySurvivalTime(PlayerScore other) {
        return Long.compare(other.getSurvivalTime(), this.getSurvivalTime()); // Orden descendente
    }

    public int compareByFruits(PlayerScore other) {
        return Integer.compare(other.fruitsEaten, this.fruitsEaten); // Orden descendente
    }

    @Override
    public String toString() {
        return String.format("Player[id=%d, name='%s', score=%d, fruits=%d, alive=%s, survival=%dms]",
                playerId, name, score, fruitsEaten, alive, getSurvivalTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerScore that = (PlayerScore) obj;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(playerId);
    }

    // Métodos de factory para crear jugadores comunes
    public static PlayerScore createNewPlayer(int playerId, String name) {
        return new PlayerScore(playerId, name, 0, 0, true, System.currentTimeMillis());
    }

    public static PlayerScore createTestPlayer(int playerId, String name, int score, int fruits) {
        long currentTime = System.currentTimeMillis();
        return new PlayerScore(playerId, name, score, fruits, true, currentTime - 30000); // 30 segundos de juego
    }
}