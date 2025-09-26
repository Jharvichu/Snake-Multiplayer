package main.java.com.snake.ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase temporal para testing del renderizado de serpientes
 * @author Ariana
 */
public class TestSnake {
    private int playerId;
    private List<Point> body;
    private String direction;
    private boolean alive;

    public TestSnake(int playerId, int startX, int startY, int length) {
        this.playerId = playerId;
        this.body = new ArrayList<>();
        this.direction = "RIGHT";
        this.alive = true;

        // Crear cuerpo inicial
        for (int i = 0; i < length; i++) {
            body.add(new Point(startX - i, startY));
        }
    }

    public int getPlayerId() {
        return playerId;
    }

    public List<Point> getBody() {
        return body;
    }

    public Point getHead() {
        return body.isEmpty() ? null : body.get(0);
    }

    public String getDirection() {
        return direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Mover serpiente una posición (para animación de testing)
     */
    public void move() {
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

        // Agregar nueva cabeza
        body.add(0, newHead);

        // Remover cola (mantener longitud constante para testing)
        body.remove(body.size() - 1);

        // Verificar límites del tablero
        if (newHead.x < 0 || newHead.x >= UIConstants.BOARD_WIDTH ||
                newHead.y < 0 || newHead.y >= UIConstants.BOARD_HEIGHT) {
            alive = false;
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
     * Crear serpientes de ejemplo para testing
     */
    public static List<TestSnake> createTestSnakes() {
        List<TestSnake> snakes = new ArrayList<>();

        // Serpiente 1 - Jugador 0 (roja)
        TestSnake snake1 = new TestSnake(0, 10, 10, 5);
        snake1.setDirection("RIGHT");
        snakes.add(snake1);

        // Serpiente 2 - Jugador 1 (verde)
        TestSnake snake2 = new TestSnake(1, 30, 15, 7);
        snake2.setDirection("DOWN");
        snakes.add(snake2);

        // Serpiente 3 - Jugador 2 (azul)
        TestSnake snake3 = new TestSnake(2, 20, 25, 4);
        snake3.setDirection("LEFT");
        snakes.add(snake3);

        // Serpiente 4 - Jugador 3 (amarilla) - más larga
        TestSnake snake4 = new TestSnake(3, 40, 5, 10);
        snake4.setDirection("DOWN");
        snakes.add(snake4);

        return snakes;
    }
}