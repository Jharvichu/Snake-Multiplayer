package main.java.com.snake.utils;

public enum FruitType {
    APPLE(1),
    ORANGE(3),      // Cambiar de BANANA(2)
    BANANA(5),      // Cambiar de ORANGE(3)
    WATERMELON(7);  // Cambiar de WATERMELON(4)

    private final int growthValue;

    FruitType(int growthValue) {
        this.growthValue = growthValue;
    }

    public int getGrowthValue() {
        return growthValue;
    }
}