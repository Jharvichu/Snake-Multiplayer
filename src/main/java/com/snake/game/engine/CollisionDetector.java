package main.java.com.snake.game.engine;

import main.java.com.snake.game.engine.collision.*;
import main.java.com.snake.game.entities.Snake;
import main.java.com.snake.game.entities.Fruit;
import main.java.com.snake.utils.CollisionType;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CollisionDetector {
    private Map<CollisionType, CollisionStrategy> strategies;
    
    public CollisionDetector() {
        this.strategies = new HashMap<>();
        this.strategies.put(CollisionType.WALL, new WallCollisionStrategy());
        this.strategies.put(CollisionType.SELF, new SelfCollisionStrategy());
        this.strategies.put(CollisionType.SNAKE, new SnakeCollisionStrategy());
        this.strategies.put(CollisionType.FRUIT, new FruitCollisionStrategy());
    }

    // Chequea si colisiono con una pared
    public boolean checkWallCollision(Point point) {
        CollisionContext context = new CollisionContext(null);
        CollisionStrategy strategy = this.strategies.get(CollisionType.WALL);

        return strategy.detectCollision(point, context);
    }

    // Chequea si colisiono consigo misma
    public boolean checkSelfCollision(Snake snake) {
        if (snake == null || !snake.getAlive()) { return false; }

        Point head = snake.getHead();
        CollisionContext context = new CollisionContext(snake);
        CollisionStrategy strategy = strategies.get(CollisionType.SELF);

        return strategy.detectCollision(head, context);
    }

    // Chequea si colisiono con otras serpiente (varias serpiente)
    public boolean checkSnakesCollision(Snake currentSnake, List<Snake> otherSnakes) {
        if (currentSnake == null || !currentSnake.getAlive() || otherSnakes == null) {
            return false;
        }

        Point head = currentSnake.getHead();
        CollisionContext context = new CollisionContext(currentSnake);
        context.setOtherSnakes(otherSnakes);
        CollisionStrategy strategy = strategies.get(CollisionType.SNAKE);

        return strategy.detectCollision(head, context);
    }

    // Chequea si colisiono con otra serpiente (1 serpiente)
    public boolean checkSnakeCollision(Snake snake1, Snake snake2) {
        return checkSnakesCollision(snake1, List.of(snake2));
    }

    // Chequea si colisiono con una fruta
    public boolean hasFruitCollision(Point point, List<Fruit> fruits) {
        return checkFruitCollision(point, fruits) != null;
    }

    // Devuelve la fruta con que choco
    public Fruit checkFruitCollision(Point point, List<Fruit> fruits) {
        if (fruits == null || fruits.isEmpty()) { return null; }

        CollisionContext context = new CollisionContext(null);
        context.setFruits(fruits);

        FruitCollisionStrategy strategy = (FruitCollisionStrategy) strategies.get(CollisionType.FRUIT);
        if (strategy.detectCollision(point, context)) { return strategy.getCollidingFruit(point, context); }

        return null;
    }
}
