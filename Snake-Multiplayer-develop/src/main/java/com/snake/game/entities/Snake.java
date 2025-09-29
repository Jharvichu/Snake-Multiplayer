package main.java.com.snake.game.entities;

import main.java.com.snake.utils.Constants;
import main.java.com.snake.utils.Direction;

import java.awt.*;
import java.util.List;
import java.util.LinkedList;

public class Snake {
    private Color color;
    private LinkedList<Point> body;

    private Direction nextDirection;
    private Direction currentDirection;
    private int growthValue;
    private boolean alive;

    private long creationTime;
    private int fruitsEaten;
    private int length;

    public Snake(int startX, int startY, Color color) {
        this.body = new LinkedList<>();
        this.body.add(new Point(startX, startY));
        nextDirection = Direction.DOWN;  // Cambiado de UP a DOWN para coincidir con el cuerpo inicial
        currentDirection = Direction.DOWN;
        this.color = color;
        this.alive = true;
        this.length = 0;
        this.growthValue = 0;
        this.fruitsEaten = 0;
        this.creationTime = System.currentTimeMillis();

        createInitialSnake();
    }

    public Snake(int startX, int startY) {
        this(startX, startY, Color.green);
    }

    // METODOS PRINCIPALES

    private void createInitialSnake() {
        // El cuerpo ya tiene la cabeza, agregar segmentos adicionales en dirección opuesta al movimiento
        // Si la serpiente va hacia DOWN, el cuerpo debe extenderse hacia UP
        for (int i = 1; i < Constants.INITIAL_SNAKE_LENGTH; i++) {
                Point lastSegment = body.getLast();
                // Extender hacia arriba ya que la dirección es DOWN
                Point newSegment = new Point(lastSegment.x, lastSegment.y - 1);
                body.addLast(newSegment);
        }
        this.length = body.size();
    }

    public void move() {
        if(!alive || body.isEmpty()) return;

        currentDirection = nextDirection;
        Point head = getHead();
        Point newHead = new Point(head);

        switch (currentDirection) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case RIGHT:
                newHead.x++;
                break;
            case LEFT:
                newHead.x--;
                break;
        }

        body.addFirst(newHead);

        if (growthValue <= 0) { 
            body.removeLast();
        } 
        else {
            growthValue--;
            this.length = body.size();
        }
    }

    public void grown(Fruit fruit) {
        this.growthValue += fruit.getGrowthValue();
        this.fruitsEaten++;
    }

    public boolean checkSelfCollision(){
        if (body.size() <= 4) return false;
            
        Point head = body.getFirst();

        List<Point> bodyWithoutHead = body.subList(1, body.size());
        return bodyWithoutHead.contains(head);
    }

    private boolean isValidDirectionChange(Direction newDirection) {
        if(body.size() == 1) return true;
        if (newDirection == null) return false;
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

    // GETTER

    public Point getHead() { return body.isEmpty() ? null : new Point(body.getFirst()); }
    public Point getTail() { return body.isEmpty() ? null : new Point(body.getLast()); }
    public LinkedList<Point> getBody() { return new LinkedList<>(body); }
    public boolean getAlive() { return alive; }
    public Direction getCurrentDirection() { return currentDirection;}
    public Direction getNextDirection() { return nextDirection;}
    public Color getColor() {return color; }
    public long getCreationTime() { return creationTime; }
    public int getTotalFruitsEaten() { return fruitsEaten; }
    public int getMaxLength() { return body.size() > length ? body.size() : length; }

    // SETTER

    public void setAlive(boolean alive) { this.alive = alive; }
    public void setColor(Color color) { this.color = color; }

    public void setDirection(Direction newDirection) {
        if(isValidDirectionChange(newDirection)){
            this.nextDirection = newDirection;
        }
    }

}