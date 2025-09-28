package main.java.com.snake.game.entities;

import main.java.com.snake.utils.FruitType;

import java.awt.*;

public class Fruit {
    private Point position;
    private FruitType fruitType;

    public Fruit(Point position, FruitType fruitType) {
        this.position = position;
        this.fruitType = fruitType;
    }

    public Point getPosition() { return this.position; }

    public FruitType getFruitType() { return this.fruitType; }

    public int getGrowthValue() { return fruitType.getGrowthValue(); }

}
