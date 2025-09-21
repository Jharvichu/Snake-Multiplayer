package main.java.com.snake.game.levels;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Level {

    private int id; //
    private int[] dimensions;
    private String[] obstacles;
    private int maxPlayers;
    private int difficulty;
    private int gameSpeed;
    private double fruitSpawnRate;
    private List<Point> obstaclePositions;

    // Constructor
    public Level() {
        this.obstaclePositions = new ArrayList<>();
    }

    // Constructor completo
    public Level(int id, int[] dimensions, String[] obstacles,
                 int maxPlayers, int difficulty, int gameSpeed, double fruitSpawnRate) {
        this.id = id;
        this.dimensions = dimensions;
        this.obstacles = obstacles;
        this.maxPlayers = maxPlayers;
        this.difficulty = difficulty;
        this.gameSpeed = gameSpeed;
        this.fruitSpawnRate = fruitSpawnRate;
        this.obstaclePositions = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int[] getDimensions() { return dimensions; }
    public void setDimensions(int[] dimensions) { this.dimensions = dimensions; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public String[] getObstacles() { return obstacles; }
    public void setObstacles(String[] obstacles) { this.obstacles = obstacles; }

    public int getGameSpeed() { return gameSpeed; }
    public void setGameSpeed(int gameSpeed) { this.gameSpeed = gameSpeed; }

    public double getFruitSpawnRate() { return fruitSpawnRate; }
    public void setFruitSpawnRate(double fruitSpawnRate) { this.fruitSpawnRate = fruitSpawnRate; }

    public List<Point> getObstaclePositions() { return obstaclePositions; }
    public void setObstaclePositions(List<Point> obstaclePositions) { this.obstaclePositions = obstaclePositions; }

    // Métodos utilitarios
    public void addObstacle(int x, int y) {
        obstaclePositions.add(new Point(x, y));
    }

    public boolean hasObstacleAt(int x, int y) {
        return obstaclePositions.contains(new Point(x, y));
    }

    @Override
    public String toString() {
        return "Level " + id + " (Difficulty: " + difficulty + ")";
    }
}
