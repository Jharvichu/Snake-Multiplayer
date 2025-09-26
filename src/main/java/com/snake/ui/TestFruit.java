package main.java.com.snake.ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase temporal para testing del renderizado de frutas
 * @author Ariana
 */
public class TestFruit {
    private Point position;
    private int value;
    private long spawnTime;
    private boolean active;

    public TestFruit(int x, int y, int value) {
        this.position = new Point(x, y);
        this.value = value;
        this.spawnTime = System.currentTimeMillis();
        this.active = true;
    }

    public Point getPosition() {
        return position;
    }

    public int getValue() {
        return value;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Verificar si la fruta ha expirado (opcional para testing)
     */
    public boolean isExpired(long currentTime, long maxAge) {
        return (currentTime - spawnTime) > maxAge;
    }

    /**
     * Crear frutas de ejemplo para testing
     */
    public static List<TestFruit> createTestFruits() {
        List<TestFruit> fruits = new ArrayList<>();

        // Frutas con diferentes valores distribuidas por el tablero
        fruits.add(new TestFruit(15, 8, 1));   // Verde
        fruits.add(new TestFruit(25, 12, 3));  // Amarilla
        fruits.add(new TestFruit(35, 20, 5));  // Naranja
        fruits.add(new TestFruit(12, 28, 7));  // Roja
        fruits.add(new TestFruit(42, 15, 1));  // Verde
        fruits.add(new TestFruit(8, 22, 3));   // Amarilla
        fruits.add(new TestFruit(38, 8, 5));   // Naranja
        fruits.add(new TestFruit(28, 30, 7));  // Roja

        return fruits;
    }

    /**
     * Generar fruta aleatoria en posición libre
     */
    public static TestFruit generateRandomFruit(List<Point> occupiedPositions) {
        Random random = new Random();
        Point position;
        int attempts = 0;

        // Intentar encontrar posición libre
        do {
            int x = random.nextInt(UIConstants.BOARD_WIDTH);
            int y = random.nextInt(UIConstants.BOARD_HEIGHT);
            position = new Point(x, y);
            attempts++;
        } while (occupiedPositions.contains(position) && attempts < 100);

        // Valores posibles: 1, 3, 5, 7 con diferentes probabilidades
        int[] values = {1, 1, 1, 3, 3, 5, 7}; // 1 más común, 7 más raro
        int value = values[random.nextInt(values.length)];

        return new TestFruit(position.x, position.y, value);
    }

    /**
     * Crear patrón de frutas para demostración
     */
    public static List<TestFruit> createDemoPattern() {
        List<TestFruit> fruits = new ArrayList<>();

        // Patrón en forma de cruz con diferentes valores
        int centerX = UIConstants.BOARD_WIDTH / 2;
        int centerY = UIConstants.BOARD_HEIGHT / 2;

        // Línea horizontal
        for (int i = -3; i <= 3; i++) {
            if (i != 0) {
                int value = Math.abs(i) % 4 == 1 ? 1 : Math.abs(i) % 4 == 2 ? 3 : Math.abs(i) % 4 == 3 ? 5 : 7;
                fruits.add(new TestFruit(centerX + i * 2, centerY, value));
            }
        }

        // Línea vertical
        for (int i = -3; i <= 3; i++) {
            if (i != 0) {
                int value = Math.abs(i) % 4 == 1 ? 3 : Math.abs(i) % 4 == 2 ? 5 : Math.abs(i) % 4 == 3 ? 7 : 1;
                fruits.add(new TestFruit(centerX, centerY + i * 2, value));
            }
        }

        return fruits;
    }

    @Override
    public String toString() {
        return String.format("Fruit[pos=(%d,%d), value=%d, active=%s]",
                position.x, position.y, value, active);
    }
}