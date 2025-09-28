package main.java.com.snake.game.engine.collision;

import main.java.com.snake.game.entities.Snake;
import main.java.com.snake.utils.CollisionType;

import java.awt.Point;

public class SelfCollisionStrategy implements CollisionStrategy {
    
    @Override
    public boolean detectCollision(Point point, CollisionContext context) {
        Snake snake = context.getCurrentSnake();
        if (snake == null || snake.getBody().size() <= 1) { return false; }
        // Verificar si la cabeza colisiona con el cuerpo (excluyendo la cabeza misma)
        return snake.getBody().subList(1, snake.getBody().size()).contains(point);
    }
    
    @Override
    public CollisionType getCollisionType() { return CollisionType.SELF; }
}