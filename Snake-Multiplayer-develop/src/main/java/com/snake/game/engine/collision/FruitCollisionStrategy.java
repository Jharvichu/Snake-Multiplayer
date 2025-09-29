package main.java.com.snake.game.engine.collision;

import main.java.com.snake.game.entities.Fruit;
import main.java.com.snake.utils.CollisionType;

import java.awt.Point;

public class FruitCollisionStrategy implements CollisionStrategy {
    
    @Override
    public boolean detectCollision(Point point, CollisionContext context) {
        if (context.getFruits() == null) { return false; }
        // Verificar colisión con cualquier fruta
        for (Fruit fruit : context.getFruits()) {
            if (fruit != null && fruit.getPosition().equals(point)) { return true; }
        }
        return false;
    }
    
    @Override
    public CollisionType getCollisionType() { return CollisionType.FRUIT; }

    public Fruit getCollidingFruit(Point point, CollisionContext context) {
        if (context.getFruits() == null) { return null; }
        // Verificar que fruta colisiono
        for (Fruit fruit : context.getFruits()) {
            if (fruit != null && fruit.getPosition().equals(point)) { return fruit; }
        }
        return null;
    }
}