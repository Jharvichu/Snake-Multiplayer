package main.java.com.snake.game.engine.collision;

import main.java.com.snake.game.entities.Fruit;
import main.java.com.snake.game.entities.Snake;

import java.util.ArrayList;
import java.util.List;

public class CollisionContext {
    private Snake currentSnake;
    private List<Snake> otherSnakes;
    private List<Fruit> fruits;

    public CollisionContext(Snake currentSnake) {
        this.currentSnake = currentSnake;
        this.otherSnakes = new ArrayList<>();
        this.fruits = new ArrayList<>();
    }

    // Getters y setters
    public Snake getCurrentSnake() { return currentSnake; }
    public void setCurrentSnake(Snake currentSnake) { this.currentSnake = currentSnake; }

    public List<Snake> getOtherSnakes() { return otherSnakes; }
    public void setOtherSnakes(List<Snake> otherSnakes) { this.otherSnakes = otherSnakes; }

    public List<Fruit> getFruits() { return fruits; }
    public void setFruits(List<Fruit> fruits) { this.fruits = fruits; }
}
