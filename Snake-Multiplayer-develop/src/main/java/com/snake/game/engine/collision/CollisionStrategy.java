package main.java.com.snake.game.engine.collision;

import main.java.com.snake.utils.CollisionType;
import java.awt.Point;

public interface CollisionStrategy {

    boolean detectCollision(Point point, CollisionContext context);
    CollisionType getCollisionType();
}

