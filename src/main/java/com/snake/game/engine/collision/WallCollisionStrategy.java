package main.java.com.snake.game.engine.collision;

import main.java.com.snake.utils.CollisionType;
import main.java.com.snake.utils.Constants;

import java.awt.Point;

public class WallCollisionStrategy implements CollisionStrategy {
    
    @Override
    public boolean detectCollision(Point point, CollisionContext context) {
        return point.x < 0 || 
               point.x >= Constants.GRID_WIDTH ||
               point.y < 0 || 
               point.y >= Constants.GRID_HEIGHT;
    }
    
    @Override
    public CollisionType getCollisionType() { return CollisionType.WALL; }
}