package main.java.com.snake.game.engine;

import main.java.com.snake.game.entities.Fruit;
import main.java.com.snake.utils.FruitType;

import java.awt.*;
import java.util.*;
import java.util.List;

public class FruitGenerator {
    private Random random;

    private final double PROBABILITY_1 = 0.60;
    private final double PROBABILITY_2 = 0.25;
    private final double PROBABILITY_3 = 0.12;
    private final double PROBABILITY_4 = 0.03;

    public FruitGenerator() {
        this.random = new Random();
    }

    public Fruit generateRandomFruit(Set<Point> freePositions) {
        if (freePositions.isEmpty()) { return null; }
        List<Point> freePoints = new ArrayList<>(freePositions);
        Point randomPoint = freePoints.get(random.nextInt(freePoints.size()));
        FruitType fruit = getRandomFruit();
        return new Fruit(randomPoint, fruit);
    }

    private FruitType getRandomFruit(){
        double probability = random.nextDouble();
        if (probability < PROBABILITY_1) { return FruitType.APPLE; }
        else if (probability < PROBABILITY_1 + PROBABILITY_2 ) { return FruitType.ORANGE; }
        else if (probability < PROBABILITY_1 + PROBABILITY_2 + PROBABILITY_3 ) { return FruitType.BANANA; }
        else { return FruitType.WATERMELON; }
    }

}
