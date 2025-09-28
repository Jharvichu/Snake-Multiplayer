package main.java.com.snake.ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase temporal para testing del renderizado de serpientes
 * @author Ariana
 */
public class TestSnake {
    private int playerId;
    private List<Point> body;
    private String direction;
    private boolean alive;
    private Random random;
    private int moveCounter;
    private int fruitsEaten; // Para tracking del scoreboard
    private int pendingGrowth; // Crecimiento pendiente

    public TestSnake(int playerId, int startX, int startY, int length) {
        this.playerId = playerId;
        this.body = new ArrayList<>();
        this.direction = "RIGHT";
        this.alive = true;
        this.random = new Random();
        this.moveCounter = 0;
        this.fruitsEaten = 0;
        this.pendingGrowth = 0;

        // Crear cuerpo inicial
        for (int i = 0; i < length; i++) {
            body.add(new Point(startX - i, startY));
        }
    }

    /**
     * Mover serpiente con lógica completa
     */
    public void move(List<TestSnake> otherSnakes, List<TestFruit> fruits) {
        if (!alive || body.isEmpty()) return;

        Point head = getHead();
        Point newHead = new Point(head);

        // Calcular nueva posición de cabeza
        switch (direction) {
            case "UP":
                newHead.y--;
                break;
            case "DOWN":
                newHead.y++;
                break;
            case "LEFT":
                newHead.x--;
                break;
            case "RIGHT":
                newHead.x++;
                break;
        }

        // Límites: X normal, Y reducido a 32
        int maxGridX = UIConstants.BOARD_WIDTH - 1;  // 49
        int maxGridY = 32; // Límite fijo en 32

        // 1. Verificar límites - morir al tocar cualquier borde
        if (newHead.x < 0 || newHead.x > maxGridX ||
                newHead.y < 0 || newHead.y > maxGridY) {
            alive = false;
            return;
        }

        // 2. Verificar colisión con otras serpientes
        if (checkCollisionWithOthers(newHead, otherSnakes)) {
            alive = false;
            return;
        }

        // 3. Verificar auto-colisión
        if (body.contains(newHead)) {
            alive = false;
            return;
        }

        // 4. Verificar si come fruta
        TestFruit eatenFruit = checkFruitCollision(newHead, fruits);
        if (eatenFruit != null) {
            eatFruit(eatenFruit, fruits);
        }

        // 5. Mover serpiente
        body.add(0, newHead);

        // 6. Remover cola solo si no hay crecimiento pendiente
        if (pendingGrowth > 0) {
            pendingGrowth--;
        } else {
            body.remove(body.size() - 1);
        }

        // 7. Cambiar dirección ocasionalmente
        moveCounter++;
        if (moveCounter > 15 + random.nextInt(20)) {
            if (random.nextDouble() < 0.3) {
                changeRandomDirection();
            }
            moveCounter = 0;
        }
    }

    /**
     * Verificar colisión con otras serpientes
     */
    private boolean checkCollisionWithOthers(Point newHead, List<TestSnake> otherSnakes) {
        for (TestSnake other : otherSnakes) {
            if (other.playerId != this.playerId && other.alive) {
                // Verificar colisión con cualquier parte del cuerpo de la otra serpiente
                for (Point segment : other.body) {
                    if (segment.equals(newHead)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Verificar colisión con frutas
     */
    private TestFruit checkFruitCollision(Point newHead, List<TestFruit> fruits) {
        for (TestFruit fruit : fruits) {
            if (fruit.isActive() && fruit.getPosition().equals(newHead)) {
                return fruit;
            }
        }
        return null;
    }

    /**
     * Comer fruta y crecer
     */
    private void eatFruit(TestFruit fruit, List<TestFruit> fruits) {
        fruitsEaten++;
        pendingGrowth += fruit.getValue(); // Crecer según valor de la fruta
        fruit.setActive(false); // Desactivar fruta
        fruits.remove(fruit); // Remover de la lista
    }

    // Método move() original para compatibilidad
    public void move() {
        // Este método se mantiene para compatibilidad, pero sin lógica completa
        move(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Cambiar a una dirección aleatoria válida
     */
    private void changeRandomDirection() {
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        String newDirection;

        do {
            newDirection = directions[random.nextInt(directions.length)];
        } while (newDirection.equals(getOppositeDirection(direction)));

        this.direction = newDirection;
    }

    /**
     * Obtener dirección opuesta
     */
    private String getOppositeDirection(String dir) {
        switch (dir) {
            case "UP": return "DOWN";
            case "DOWN": return "UP";
            case "LEFT": return "RIGHT";
            case "RIGHT": return "LEFT";
            default: return "";
        }
    }

    /**
     * Hacer crecer la serpiente
     */
    public void grow() {
        if (!body.isEmpty()) {
            Point tail = body.get(body.size() - 1);
            body.add(new Point(tail));
        }
    }

    /**
     * Reiniciar serpiente en posición segura
     */
    public void reset(int startX, int startY, int length) {
        body.clear();

        // Límites seguros
        int maxX = UIConstants.BOARD_WIDTH - 1;
        int maxY = 32;

        int safeX = Math.max(2, Math.min(startX, maxX - 2));
        int safeY = Math.max(2, Math.min(startY, maxY - 2));

        for (int i = 0; i < length; i++) {
            body.add(new Point(safeX - i, safeY));
        }
        alive = true;
        moveCounter = 0;
        fruitsEaten = 0;
        pendingGrowth = 0;
        changeRandomDirection();
    }

    // Getters adicionales para el scoreboard
    public int getFruitsEaten() { return fruitsEaten; }
    public int getScore() { return fruitsEaten * 10; } // 10 puntos por fruta
    public int getLength() { return body.size(); }

    // Getters existentes
    public int getPlayerId() { return playerId; }
    public List<Point> getBody() { return body; }
    public Point getHead() { return body.isEmpty() ? null : body.get(0); }
    public String getDirection() { return direction; }
    public boolean isAlive() { return alive; }
    public void setDirection(String direction) { this.direction = direction; }
    public void setAlive(boolean alive) { this.alive = alive; }

    /**
     * Crear serpientes de ejemplo para testing
     */
    public static List<TestSnake> createTestSnakes() {
        List<TestSnake> snakes = new ArrayList<>();

        // Usar posiciones seguras con el nuevo límite Y=32
        int margin = 3;

        // Serpiente 1 - Jugador 0 (roja) - esquina superior izquierda
        TestSnake snake1 = new TestSnake(0, margin + 3, margin, 5);
        snake1.setDirection("RIGHT");
        snakes.add(snake1);

        // Serpiente 2 - Jugador 1 (verde) - esquina superior derecha
        TestSnake snake2 = new TestSnake(1, UIConstants.BOARD_WIDTH - margin - 3, margin, 7);
        snake2.setDirection("DOWN");
        snakes.add(snake2);

        // Serpiente 3 - Jugador 2 (azul) - esquina inferior izquierda
        TestSnake snake3 = new TestSnake(2, margin + 3, 30, 4);
        snake3.setDirection("UP");
        snakes.add(snake3);

        // Serpiente 4 - Jugador 3 (amarilla) - esquina inferior derecha
        TestSnake snake4 = new TestSnake(3, UIConstants.BOARD_WIDTH - margin - 3, 30, 6);
        snake4.setDirection("LEFT");
        snakes.add(snake4);

        return snakes;
    }
}