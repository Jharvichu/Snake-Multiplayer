package main.java.com.snake.game.engine.collision;

import main.java.com.snake.game.entities.Snake;
import main.java.com.snake.utils.CollisionType;

import java.awt.Point;

public class SnakeCollisionStrategy implements CollisionStrategy {
    
    @Override
    public boolean detectCollision(Point point, CollisionContext context) {
        if (context.getOtherSnakes() == null) { return false; }
        // Verificar colisión con cualquier parte de otras serpientes
        for (Snake otherSnake : context.getOtherSnakes()) {
            if (otherSnake != null && otherSnake.getAlive()) {
                if (otherSnake.getBody().contains(point)) { return true; }
            }
        }
        return false;
    }
    
    @Override
    public CollisionType getCollisionType() { return CollisionType.SNAKE; }
}