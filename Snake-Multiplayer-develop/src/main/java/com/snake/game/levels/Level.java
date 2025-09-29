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

    // CAMPOS PARA OBSTÁCULOS MÓVILES
    private List<Point> movingObstacles;
    private long lastMoveTime;
    private int moveDirection = 1; // 1 o -1 para dirección

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

        // Inicializar obstáculos móviles
        this.movingObstacles = new ArrayList<>();
        this.lastMoveTime = System.currentTimeMillis();
    }

    // Métodos existentes
    public void addObstacle(int x, int y) {
        if (isInsideBounds(x, y)) {
            obstacles.add(new Point(x, y));
        }
    }

    // Agregar obstáculo móvil
    public void addMovingObstacle(int x, int y) {
        if (isInsideBounds(x, y)) {
            movingObstacles.add(new Point(x, y));
            System.out.println("Obstáculo móvil agregado en (" + x + "," + y + ") - Total: " + movingObstacles.size());
        }
    }

    // MEJORADO: Verificar obstáculos con debug
    public boolean hasObstacleAt(int x, int y) {
        Point point = new Point(x, y);
        boolean hasStatic = obstacles.contains(point);
        boolean hasMoving = movingObstacles.contains(point);

        if (hasStatic || hasMoving) {
            System.out.println("Obstáculo encontrado en (" + x + "," + y + ") - Estático: " + hasStatic + ", Móvil: " + hasMoving);
        }

        return hasStatic || hasMoving;
    }

    public boolean isInsideBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // MEJORADO: Actualizar obstáculos móviles con más debug
    public void updateMovingObstacles() {
        if (id != 4) return; // Solo nivel 4 tiene obstáculos móviles

        if (movingObstacles.isEmpty()) {
            System.out.println("No hay obstáculos móviles para actualizar en nivel " + id);
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < 2000) return; // Mover cada 2 segundos

        System.out.println("Actualizando " + movingObstacles.size() + " obstáculos móviles. Dirección: " + moveDirection);

        // Mover obstáculos en zigzag
        for (int i = 0; i < movingObstacles.size(); i++) {
            Point obstacle = movingObstacles.get(i);
            int oldX = obstacle.x;
            obstacle.x += moveDirection;

            // Cambiar dirección si llega a los bordes
            if (obstacle.x <= 5 || obstacle.x >= width - 5) {
                moveDirection *= -1;
                System.out.println("Cambiando dirección de obstáculos móviles: " + moveDirection);
                break; // Cambiar dirección para todos al mismo tiempo
            }

            System.out.println("Obstáculo " + i + " movido de x=" + oldX + " a x=" + obstacle.x);
        }

        lastMoveTime = currentTime;
    }

    // Obtener todos los obstáculos (estáticos + móviles)
    public List<Point> getAllObstacles() {
        List<Point> allObstacles = new ArrayList<>(obstacles);
        allObstacles.addAll(movingObstacles);
        return allObstacles;
    }

    // NUEVO: Método para forzar debug de obstáculos
    public void debugObstacles() {
        System.out.println("=== DEBUG OBSTÁCULOS NIVEL " + id + " ===");
        System.out.println("Obstáculos estáticos: " + obstacles.size());
        for (int i = 0; i < Math.min(5, obstacles.size()); i++) {
            Point p = obstacles.get(i);
            System.out.println("  - Estático " + i + ": (" + p.x + "," + p.y + ")");
        }

        System.out.println("Obstáculos móviles: " + movingObstacles.size());
        for (int i = 0; i < movingObstacles.size(); i++) {
            Point p = movingObstacles.get(i);
            System.out.println("  - Móvil " + i + ": (" + p.x + "," + p.y + ")");
        }
        System.out.println("=============================");
    }

    // Reiniciar obstáculos móviles a posiciones iniciales
    public void resetMovingObstacles() {
        if (id == 4) {
            movingObstacles.clear();
            // Estas posiciones se agregan desde LevelManager
            moveDirection = 1;
            lastMoveTime = System.currentTimeMillis();
            System.out.println("Obstáculos móviles reiniciados para nivel 4");
        }
    }

    // Verificar si el nivel tiene obstáculos móviles
    public boolean hasMovingObstacles() {
        return !movingObstacles.isEmpty();
    }

    // Getters y setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getGameSpeed() { return gameSpeed; }
    public List<Point> getObstacles() { return new ArrayList<>(obstacles); }
    public List<Point> getMovingObstacles() { return new ArrayList<>(movingObstacles); }
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
        int totalObstacles = obstacles.size() + movingObstacles.size();
        return String.format("Nivel %d: %s (%dx%d, %d obstáculos (%d móviles), %d pts requeridos)",
                id, name, width, height, totalObstacles, movingObstacles.size(), requiredScore);
    }
}