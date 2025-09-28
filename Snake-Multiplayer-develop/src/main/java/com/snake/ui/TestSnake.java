package main.java.com.snake.ui;

import main.java.com.snake.game.levels.Level;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import main.java.com.snake.game.levels.LevelManager;
import main.java.com.snake.game.levels.Level;

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
     * Mover serpiente con lógica completa (CORREGIDO para usar sistema real de colisiones)
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

        // 1. NUEVO: Verificar colisión con obstáculos usando sistema real
        LevelManager levelManager = LevelManager.getCurrentInstance();
        if (levelManager != null) {
            Level currentLevel = levelManager.getCurrentLevel();
            if (currentLevel != null && currentLevel.hasObstacleAt(newHead.x, newHead.y)) {
                alive = false;
                System.out.println("TestSnake " + playerId + " murió por obstáculo en (" + newHead.x + "," + newHead.y + ")");
                return;
            }
        }

        // 2. Verificar límites del tablero
        int maxGridX = UIConstants.BOARD_WIDTH - 1;
        int maxGridY = 32; // Límite fijo en 32

        if (newHead.x < 0 || newHead.x > maxGridX ||
                newHead.y < 0 || newHead.y > maxGridY) {
            alive = false;
            System.out.println("TestSnake " + playerId + " murió por límites en (" + newHead.x + "," + newHead.y + ")");
            return;
        }

        // 3. Verificar colisión con otras serpientes
        if (checkCollisionWithOthers(newHead, otherSnakes)) {
            alive = false;
            System.out.println("TestSnake " + playerId + " murió por colisión con otra serpiente");
            return;
        }

        // 4. Verificar auto-colisión
        if (body.contains(newHead)) {
            alive = false;
            System.out.println("TestSnake " + playerId + " murió por auto-colisión");
            return;
        }

        // 5. Verificar si come fruta
        TestFruit eatenFruit = checkFruitCollision(newHead, fruits);
        if (eatenFruit != null) {
            eatFruit(eatenFruit, fruits);
        }

        // 6. Mover serpiente
        body.add(0, newHead);

        // 7. Remover cola solo si no hay crecimiento pendiente
        if (pendingGrowth > 0) {
            pendingGrowth--;
        } else {
            body.remove(body.size() - 1);
        }

        // 8. Cambiar dirección ocasionalmente
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

    // ========== NUEVOS MÉTODOS PARA NIVELES ==========

    /**
     * Crear serpientes de ejemplo para un nivel específico, evitando obstáculos
     */
    public static List<TestSnake> createTestSnakesForLevel(Level level) {
        List<TestSnake> snakes = new ArrayList<>();

        if (level == null) {
            return createTestSnakes(); // Fallback al método original
        }

        // Crear serpientes evitando obstáculos del nivel
        int margin = 3;
        List<Point> obstacles = level.getObstacles();

        // Buscar posiciones seguras para cada serpiente
        Point[] safePositions = findSafePositions(level, obstacles, 4);

        for (int i = 0; i < Math.min(4, safePositions.length); i++) {
            Point pos = safePositions[i];
            TestSnake snake = new TestSnake(i, pos.x, pos.y, 5);

            // Establecer dirección inicial segura
            String safeDirection = findSafeDirection(pos, obstacles, level);
            snake.setDirection(safeDirection);

            snakes.add(snake);
        }

        return snakes;
    }

    /**
     * Encontrar posiciones seguras evitando obstáculos
     */
    private static Point[] findSafePositions(Level level, List<Point> obstacles, int count) {
        List<Point> safePositions = new ArrayList<>();
        int margin = 5;

        // Intentar encontrar posiciones en las esquinas primero
        Point[] cornerPositions = {
                new Point(margin, margin),                                    // Superior izquierda
                new Point(level.getWidth() - margin, margin),                // Superior derecha
                new Point(margin, level.getHeight() - margin),               // Inferior izquierda
                new Point(level.getWidth() - margin, level.getHeight() - margin) // Inferior derecha
        };

        for (Point corner : cornerPositions) {
            if (isPositionSafe(corner, obstacles, margin)) {
                safePositions.add(corner);
                if (safePositions.size() >= count) break;
            }
        }

        // Si no hay suficientes posiciones en esquinas, buscar otras
        Random random = new Random();
        int maxAttempts = 50;
        int attempts = 0;

        while (safePositions.size() < count && attempts < maxAttempts) {
            Point randomPos = new Point(
                    margin + random.nextInt(level.getWidth() - 2 * margin),
                    margin + random.nextInt(level.getHeight() - 2 * margin)
            );

            if (isPositionSafe(randomPos, obstacles, margin) && !safePositions.contains(randomPos)) {
                safePositions.add(randomPos);
            }
            attempts++;
        }

        // Si aún no hay suficientes, usar las esquinas aunque no sean perfectamente seguras
        while (safePositions.size() < count) {
            for (Point corner : cornerPositions) {
                if (!safePositions.contains(corner)) {
                    safePositions.add(corner);
                    if (safePositions.size() >= count) break;
                }
            }
            break;
        }

        return safePositions.toArray(new Point[0]);
    }

    /**
     * Verificar si una posición es segura (lejos de obstáculos)
     */
    private static boolean isPositionSafe(Point pos, List<Point> obstacles, int minDistance) {
        if (obstacles == null || obstacles.isEmpty()) {
            return true;
        }

        for (Point obstacle : obstacles) {
            double distance = pos.distance(obstacle);
            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encontrar una dirección segura para moverse inicialmente
     */
    private static String findSafeDirection(Point pos, List<Point> obstacles, Level level) {
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};

        for (String direction : directions) {
            Point nextPos = getNextPosition(pos, direction);
            if (nextPos.x >= 0 && nextPos.x < level.getWidth() &&
                    nextPos.y >= 0 && nextPos.y < level.getHeight() &&
                    isPositionSafe(nextPos, obstacles, 3)) {
                return direction;
            }
        }

        return "RIGHT"; // Fallback
    }

    /**
     * Obtener la siguiente posición según la dirección
     */
    private static Point getNextPosition(Point current, String direction) {
        Point next = new Point(current);
        switch (direction) {
            case "UP": next.y--; break;
            case "DOWN": next.y++; break;
            case "LEFT": next.x--; break;
            case "RIGHT": next.x++; break;
        }
        return next;
    }

    /**
     * Crear serpientes de ejemplo para testing (método original)
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