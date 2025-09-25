package main.java.com.snake.game.entities;

import main.java.com.snake.utils.Direction;

import java.awt.*;
import java.util.LinkedList;


public class Snake {
    private Color color;
    private LinkedList<Point> body;
    private Direction nextDirection;
    private Direction currentDirection;
    private int growthValue;
    private boolean alive;

    public Snake(int startX, int startY, Color color) {
        this.body = new LinkedList<>();
        this.body.add(new Point(startX, startY));
        nextDirection = Direction.UP;
        currentDirection = Direction.UP;
        this.color = color;
        this.alive = true;
    }

    public Snake(int startX, int startY) {
        this(startX, startY, Color.green);
    }

    public void setDirection(Direction newDirection) {
        if(isValidDirectionChange(newDirection)){
            this.nextDirection = newDirection;
        }
    }

    public Point getHead() { return body.getFirst(); }

    public LinkedList<Point> getBody() { return body; }

    public boolean getAlive() { return alive; }

    // Mueve a la serpiente
    public void move() {
        currentDirection = nextDirection;

        // Crea una cabeza para que de la sensacion de movimiento
        Point head = getHead();
        Point newHead = new Point(head);

        switch (currentDirection) {
            case UP:
                newHead.y++;
                break;
            case DOWN:
                newHead.y--;
                break;
            case RIGHT:
                newHead.x++;
                break;
            case LEFT:
                newHead.x--;
                break;
        }

        body.addFirst(newHead);

        // En caso comio una fruta, no se remueve su cola
        if(growthValue != 0){
            growthValue--;
            return;
        }

        // Remueve la cola, para una sensacion de movimiento
        body.removeLast();
    }

    public void grown(int segments) {
        this.growthValue += segments;
    }

    public boolean checkSelfCollision(){
        return false;
    }

    // Valida si la direccion que se movera esta permitida
    private boolean isValidDirectionChange(Direction newDirection) {
        if(body.size() == 1) return true;
        switch(currentDirection) {
            case UP:
                return newDirection != Direction.DOWN;
            case DOWN:
                return newDirection != Direction.UP;
            case LEFT:
                return newDirection != Direction.RIGHT;
            case RIGHT:
                return newDirection != Direction.LEFT;
            default:
                return true;
        }
    }

}
