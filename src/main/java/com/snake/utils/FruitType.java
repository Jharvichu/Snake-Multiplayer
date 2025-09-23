package main.java.com.snake.utils;

public enum FruitType {

    APPLE(1),
    BANANA(2),
    ORANGE(3),
    WATERMELON(4);

    private final int growthValue;

    FruitType(int growhtValue) {
        this.growthValue = growhtValue;
    }

    public int getGrowthValue() {
        return growthValue;
    }

}
