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

    // Chequea si colisiono con una pared O OBSTÁCULO
    public boolean checkWallCollision(Point point) {
        CollisionContext context = new CollisionContext(null);
        CollisionStrategy strategy = this.strategies.get(CollisionType.WALL);

        boolean collision = strategy.detectCollision(point, context);

        // DEBUG: Mostrar cuando hay colisión con obstáculo
        if (collision) {
            System.out.println("¡COLISIÓN DETECTADA! Posición: (" + point.x + "," + point.y + ")");
        }

        return collision;
    }

    // Chequea si colisiono consigo misma
    public boolean checkSelfCollision(Snake snake) {
        if (snake == null || !snake.getAlive()) { return false; }

        Point head = snake.getHead();
        CollisionContext context = new CollisionContext(snake);
        CollisionStrategy strategy = strategies.get(CollisionType.SELF);

        boolean collision = strategy.detectCollision(head, context);

        // DEBUG: Mostrar auto-colisión
        if (collision) {
            System.out.println("Auto-colisión detectada para serpiente en: (" + head.x + "," + head.y + ")");
        }

        return collision;
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

        boolean collision = strategy.detectCollision(head, context);

        // DEBUG: Mostrar colisión entre serpientes
        if (collision) {
            System.out.println("Colisión entre serpientes en: (" + head.x + "," + head.y + ")");
        }

        return collision;
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
        if (strategy.detectCollision(point, context)) {
            Fruit fruit = strategy.getCollidingFruit(point, context);

            // DEBUG: Mostrar cuando se come una fruta
            if (fruit != null) {
                System.out.println("Fruta comida en: (" + point.x + "," + point.y + ") valor: " + fruit.getGrowthValue());
            }

            return fruit;
        }

        return null;
    }

    // NUEVO: Método de debug para verificar que se está ejecutando
    public void debugCollisionCheck(Point point, String snakeId) {
        System.out.println("Verificando colisiones para " + snakeId + " en (" + point.x + "," + point.y + ")");
    }
}