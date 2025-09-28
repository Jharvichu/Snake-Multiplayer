package main.java.com.snake.game.entities;

import java.awt.*;

import main.java.com.snake.utils.Constants;
import main.java.com.snake.utils.Direction;
import main.java.com.snake.utils.FruitType;

public class Player {

    private final int playerId;
    private String playerName;
    private Snake snake;
    private boolean alive;

    private int score;
    private int snakeLength;
    private int fruitsEaten;
    private long joinTime;
    private long totalSurvivalTime;
    private int gamesPlayed;
    private int gamesWon;

    private boolean connected;
    private boolean ready;

    public Player(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = 0;
        this.alive = true;
        this.fruitsEaten = 0;
        this.snakeLength = Constants.INITIAL_SNAKE_LENGTH;
        this.joinTime = System.currentTimeMillis();
        this.totalSurvivalTime = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    public Snake getSnake() { return snake; }
    public void setSnake(Snake snake) { 
        this.snake = snake;
        if (snake != null) { this.alive = snake.getAlive(); }
    }

    public boolean getAlive() { return alive && (snake != null ? snake.getAlive() : false); }
    public void setAlive(boolean alive) {
        this.alive = alive;
        if (snake != null) { snake.setAlive(alive); }
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }

    public int getPlayerId() { return playerId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    // METODOS DE CONTROL DEL SNAKE

    public void sendDirectionCommand(Direction direction) {
        if (this.snake != null && alive) { 
            snake.setDirection(direction);
        }
    }

    public void updateMaxSnakeLength() {
        if (this.snake != null && alive) { 
            this.snakeLength = snake.getMaxLength();
        }
    }

    public void updateFromSnake() {
        if (snake != null) {
            this.alive = snake.getAlive();
            updateMaxSnakeLength();

            if (snake.getTotalFruitsEaten() > this.fruitsEaten ) {
                this.fruitsEaten = snake.getTotalFruitsEaten();
            }
        }
    }

    // METODOS PARA ESTADISTICAS

    public void addScore(int points) {
        this.score += points;
        updateMaxSnakeLength();
    }

    public void fruitEaten(FruitType fruit) {
        addScore(fruit.getGrowthValue());
        this.fruitsEaten++;
    }

    public void startNewGame() {
        this.gamesPlayed ++;
        this.alive = true;
        this.ready = true;
    }

    public void wonGame() {
        this.gamesWon++;
        this.totalSurvivalTime += System.currentTimeMillis() - joinTime;
    }

    public void resetGameStats() {
        this.score = 0;
        this.alive = true;
        if (snake != null) { snake.setAlive(true); }
    }

    // METODOS GETTER DE ESTADISTICAS

    public long getJoinTime() { return joinTime; }
    public long getTotalSurvivalTime() { return totalSurvivalTime; }
    public int getFruitEaten() { return fruitsEaten; }
    public int getSnakeLength() { return snakeLength; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() { return gamesWon; }

    // METODOS DE CONEXION

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }
    
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
}
