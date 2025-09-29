package main.java.com.snake.game.engine;

import main.java.com.snake.game.entities.Fruit;
import main.java.com.snake.utils.FruitType;
import main.java.com.snake.game.levels.LevelManager;
import main.java.com.snake.game.levels.Level;

import java.awt.*;
import java.util.*;
import java.util.List;

public class FruitGenerator {
    private Random random;

    private final double PROBABILITY_1 = 0.60;
    private final double PROBABILITY_2 = 0.25;
    private final double PROBABILITY_3 = 0.12;
    // reservada para futuras frutas especiales
    // private final double PROBABILITY_4 = 0.03;

    public FruitGenerator() {
        this.random = new Random();
    }

    public Fruit generateRandomFruit(Set<Point> freePositions) {
        if (freePositions.isEmpty()) {
            return null;
        }

        // NUEVO: Filtrar posiciones que NO sean obstáculos
        Set<Point> validPositions = filterObstaclePositions(freePositions);

        if (validPositions.isEmpty()) {
            return null;
        }

        List<Point> freePoints = new ArrayList<>(validPositions);
        Point randomPoint = freePoints.get(random.nextInt(freePoints.size()));
        FruitType fruit = getRandomFruit();
        return new Fruit(randomPoint, fruit);
    }

    // NUEVO: Filtrar posiciones ocupadas por obstáculos
    private Set<Point> filterObstaclePositions(Set<Point> positions) {
        Set<Point> validPositions = new HashSet<>();

        try {
            LevelManager levelManager = LevelManager.getCurrentInstance();
            Level currentLevel = levelManager != null ? levelManager.getCurrentLevel() : null;

            for (Point pos : positions) {
                boolean isObstacle = currentLevel != null && currentLevel.hasObstacleAt(pos.x, pos.y);
                if (!isObstacle) {
                    validPositions.add(pos);
                }
            }
        } catch (Exception e) {
            // Si hay error, devolver todas las posiciones (modo seguro)
            System.err.println("Error filtrando obstáculos: " + e.getMessage());
            return new HashSet<>(positions);
        }

        return validPositions;
    }

    private FruitType getRandomFruit(){
        double probability = random.nextDouble();
        if (probability < PROBABILITY_1) {
            return FruitType.APPLE;
        }
        else if (probability < PROBABILITY_1 + PROBABILITY_2 ) {
            return FruitType.ORANGE;
        }
        else if (probability < PROBABILITY_1 + PROBABILITY_2 + PROBABILITY_3 ) {
            return FruitType.BANANA;
        }
        else {
            return FruitType.WATERMELON;
        }
    }
}