package main.java.com.snake.game.engine.collision;

import main.java.com.snake.utils.CollisionType;
import main.java.com.snake.utils.Constants;
import main.java.com.snake.game.levels.LevelManager;
import main.java.com.snake.game.levels.Level;

import java.awt.Point;

public class WallCollisionStrategy implements CollisionStrategy {

    @Override
    public boolean detectCollision(Point point, CollisionContext context) {
        // Colisión con bordes del tablero
        boolean wallCollision = point.x < 0 ||
                point.x >= Constants.GRID_WIDTH ||
                point.y < 0 ||
                point.y >= Constants.GRID_HEIGHT;

        if (wallCollision) {
            System.out.println("¡Colisión con pared detectada en (" + point.x + "," + point.y + ")!");
            return true;
        }

        // Colisión con obstáculos del nivel actual
        try {
            LevelManager levelManager = LevelManager.getCurrentInstance();
            if (levelManager != null) {
                Level currentLevel = levelManager.getCurrentLevel();
                if (currentLevel != null && currentLevel.hasObstacleAt(point.x, point.y)) {
                    System.out.println("¡Obstáculo detectado en (" + point.x + "," + point.y + ") en nivel " + currentLevel.getId() + "!");
                    return true;
                }
            } else {
                System.out.println("LevelManager es null - no se pueden verificar obstáculos");
            }
        } catch (Exception e) {
            System.err.println("Error verificando obstáculos: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public CollisionType getCollisionType() {
        return CollisionType.WALL;
    }
}