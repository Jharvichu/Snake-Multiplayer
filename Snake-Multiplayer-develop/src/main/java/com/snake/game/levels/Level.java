package main.java.com.snake.game.levels;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un nivel del juego Snake
 * Contiene solo la información esencial: dimensiones, obstáculos y configuración básica
 */
public class Level {
    private int id;
    private int width;
    private int height;
    private int gameSpeed; // Velocidad del juego en ms
    private List<Point> obstacles; // Lista de obstáculos

    // Constructor
    public Level(int id, int width, int height, int gameSpeed) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.gameSpeed = gameSpeed;
        this.obstacles = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getGameSpeed() { return gameSpeed; }
    public List<Point> getObstacles() { return obstacles; }

    // Métodos para manejar obstáculos
    public void addObstacle(int x, int y) {
        obstacles.add(new Point(x, y));
    }

    public boolean hasObstacleAt(int x, int y) {
        return obstacles.contains(new Point(x, y));
    }

    // Verificar si una posición está dentro del nivel
    public boolean isInsideBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public String toString() {
        return "Nivel " + id + " (" + width + "x" + height + ", " + obstacles.size() + " obstáculos)";
    }
}
