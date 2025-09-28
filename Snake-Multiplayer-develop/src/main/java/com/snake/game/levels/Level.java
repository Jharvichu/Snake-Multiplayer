// ========== ACTUALIZACIÓN DE Level.java ==========
package main.java.com.snake.game.levels;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Level mejorada con más propiedades
 */
public class Level {
    private int id;
    private String name;
    private String description;
    private int width;
    private int height;
    private int gameSpeed;
    private List<Point> obstacles;

    // Nuevas propiedades para progresión
    private int requiredScore;
    private long maxTime; // Tiempo máximo en ms
    private int maxFruits;
    private double fruitSpawnRate;

    public Level(int id, String name, int width, int height, int gameSpeed) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.gameSpeed = gameSpeed;
        this.obstacles = new ArrayList<>();
        this.requiredScore = 50;
        this.maxTime = 120000; // 2 minutos por defecto
        this.maxFruits = 8;
        this.fruitSpawnRate = 1.0;
    }

    // Métodos existentes
    public void addObstacle(int x, int y) {
        if (isInsideBounds(x, y)) {
            obstacles.add(new Point(x, y));
        }
    }

    public boolean hasObstacleAt(int x, int y) {
        return obstacles.contains(new Point(x, y));
    }

    public boolean isInsideBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getGameSpeed() { return gameSpeed; }
    public List<Point> getObstacles() { return new ArrayList<>(obstacles); }
    public int getRequiredScore() { return requiredScore; }
    public long getMaxTime() { return maxTime; }
    public int getMaxFruits() { return maxFruits; }
    public double getFruitSpawnRate() { return fruitSpawnRate; }

    public void setDescription(String description) { this.description = description; }
    public void setRequiredScore(int requiredScore) { this.requiredScore = requiredScore; }
    public void setMaxTime(long maxTime) { this.maxTime = maxTime; }
    public void setMaxFruits(int maxFruits) { this.maxFruits = maxFruits; }
    public void setFruitSpawnRate(double fruitSpawnRate) { this.fruitSpawnRate = fruitSpawnRate; }

    @Override
    public String toString() {
        return String.format("Nivel %d: %s (%dx%d, %d obstáculos, %d pts requeridos)",
                id, name, width, height, obstacles.size(), requiredScore);
    }
}