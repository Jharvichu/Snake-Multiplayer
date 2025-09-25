package main.java.com.snake.game.entities;

import java.awt.*;

public class Player {
    private final int playerId;
    private String playerName;
    private Snake snake;
    private int score;
    private boolean alive;

    public Player(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = 0;
        this.alive = true;
    }

    public int getPlayerId() { return playerId; }

    public String getPlayerName() {return playerName;}

    public void setPlayerName(String name) { this.playerName = name; }

    public void setSnake(Snake snake) { this.snake = snake; }

    public int getScore() {return score;}

    public void setScore(int score) {this.score = score;}

}
