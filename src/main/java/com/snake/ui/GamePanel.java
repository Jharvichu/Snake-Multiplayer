package main.java.com.snake.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Panel donde se renderiza el juego Snake Multijugador
 * @author Ariana
 */
public class GamePanel extends JPanel implements KeyListener {

    // Estado temporal para testing
    private boolean showGrid = true;
    private List<TestSnake> testSnakes;
    private List<TestFruit> testFruits;
    private boolean showTestData = true;

    public GamePanel() {
        initializePanel();
        initializeTestData();
    }

    /**
     * Configurar propiedades básicas del panel
     */
    private void initializePanel() {
        setBackground(UIConstants.BACKGROUND_COLOR);
        setFocusable(true);
        addKeyListener(this);

        // Establecer tamaño preferido
        setPreferredSize(new Dimension(
                UIConstants.BOARD_WIDTH * UIConstants.CELL_SIZE,
                UIConstants.BOARD_HEIGHT * UIConstants.CELL_SIZE
        ));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Activar antialiasing para mejor calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar elementos del juego
        if (showGrid) {
            drawGrid(g2d);
        }

        // Dibujar datos de testing si están activos
        if (showTestData) {
            // Por esto:
            if (testSnakes != null) {
                drawTestSnakes(g2d, testSnakes);
            }
            if (testFruits != null) {
                drawTestFruits(g2d, testFruits);
            }
            drawTestingMessage(g2d);
        } else {
            drawTestingMessage(g2d);
        }

        g2d.dispose();
    }

    /**
     * Dibujar la cuadrícula de fondo
     */
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(UIConstants.GRID_COLOR);
        g2d.setStroke(new BasicStroke(1));

        // Líneas verticales
        for (int x = 0; x <= UIConstants.BOARD_WIDTH; x++) {
            int pixelX = x * UIConstants.CELL_SIZE;
            g2d.drawLine(pixelX, 0, pixelX,
                    UIConstants.BOARD_HEIGHT * UIConstants.CELL_SIZE);
        }

        // Líneas horizontales
        for (int y = 0; y <= UIConstants.BOARD_HEIGHT; y++) {
            int pixelY = y * UIConstants.CELL_SIZE;
            g2d.drawLine(0, pixelY,
                    UIConstants.BOARD_WIDTH * UIConstants.CELL_SIZE, pixelY);
        }
    }

    /**
     * Inicializar datos de testing
     */
    private void initializeTestData() {
        testSnakes = TestSnake.createTestSnakes();
        testFruits = TestFruit.createTestFruits();

        // Timer para animar serpientes (opcional)
        Timer animationTimer = new Timer(200, e -> {
            if (showTestData && testSnakes != null) {
                for (TestSnake snake : testSnakes) {
                    snake.move();
                }
                repaint();
            }
        });
        // animationTimer.start(); // Descomenta para animación automática
    }
    private void drawTestingMessage(Graphics2D g2d) {
        g2d.setColor(UIConstants.TEXT_COLOR);
        g2d.setFont(UIConstants.TITLE_FONT);

        String message = "Panel de Juego - Presiona teclas para testing";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;

        g2d.drawString(message, x, y);

        // Instrucciones
        g2d.setFont(UIConstants.SCORE_FONT);
        String instructions = "WASD o flechas para mover | G para mostrar/ocultar grid";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(instructions)) / 2;
        y = y + 30;

        g2d.drawString(instructions, x, y);
    }

    /**
     * Dibujar serpientes de testing
     */
    private void drawTestSnakes(Graphics2D g2d, List<TestSnake> snakes) {
        for (TestSnake snake : snakes) {
            if (snake.isAlive()) {
                drawSingleSnake(g2d, snake);
            }
        }
    }

    /**
     * Dibujar frutas de testing
     */
    private void drawTestFruits(Graphics2D g2d, List<TestFruit> fruits) {
        for (TestFruit fruit : fruits) {
            if (fruit.isActive()) {
                drawSingleFruit(g2d, fruit);
            }
        }
    }

    /**
     * Dibujar una serpiente individual
     */
    private void drawSingleSnake(Graphics2D g2d, TestSnake snake) {
        List<Point> body = snake.getBody();
        if (body.isEmpty()) return;

        Color playerColor = UIConstants.getPlayerColor(snake.getPlayerId());

        for (int i = 0; i < body.size(); i++) {
            Point segment = body.get(i);
            int x = UIConstants.gameToPixelX(segment.x);
            int y = UIConstants.gameToPixelY(segment.y);

            if (i == 0) {
                // Dibujar cabeza
                g2d.setColor(playerColor.darker());
                g2d.fillRoundRect(x, y, UIConstants.CELL_SIZE, UIConstants.CELL_SIZE, 8, 8);
                g2d.setColor(playerColor);
                g2d.fillRoundRect(x + 2, y + 2, UIConstants.CELL_SIZE - 4, UIConstants.CELL_SIZE - 4, 6, 6);

                // Ojos
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x + 4, y + 4, 3, 3);
                g2d.fillOval(x + UIConstants.CELL_SIZE - 7, y + 4, 3, 3);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x + 5, y + 5, 2, 2);
                g2d.fillOval(x + UIConstants.CELL_SIZE - 6, y + 5, 2, 2);
            } else {
                // Dibujar cuerpo
                Color bodyColor = new Color(
                        Math.max(playerColor.getRed() - i * 10, 50),
                        Math.max(playerColor.getGreen() - i * 10, 50),
                        Math.max(playerColor.getBlue() - i * 10, 50)
                );
                g2d.setColor(bodyColor);
                g2d.fillRoundRect(x + 1, y + 1, UIConstants.CELL_SIZE - 2, UIConstants.CELL_SIZE - 2, 4, 4);
            }
        }
    }

    /**
     * Dibujar una fruta individual
     */
    private void drawSingleFruit(Graphics2D g2d, TestFruit fruit) {
        Point pos = fruit.getPosition();
        int x = UIConstants.gameToPixelX(pos.x);
        int y = UIConstants.gameToPixelY(pos.y);
        int value = fruit.getValue();

        Color fruitColor = UIConstants.getFruitColor(value);

        // Dibujar fruta
        g2d.setColor(fruitColor);
        g2d.fillOval(x + 1, y + 1, UIConstants.CELL_SIZE - 2, UIConstants.CELL_SIZE - 2);

        // Número del valor
        g2d.setColor(Color.WHITE);
        g2d.setFont(UIConstants.FRUIT_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        String valueStr = String.valueOf(value);
        int textX = x + (UIConstants.CELL_SIZE - fm.stringWidth(valueStr)) / 2;
        int textY = y + (UIConstants.CELL_SIZE + fm.getAscent()) / 2 - 2;
        g2d.drawString(valueStr, textX, textY);
    }

    /**
     * Obtener color de jugador por ID
     */
    public Color getPlayerColor(int playerId) {
        return UIConstants.getPlayerColor(playerId);
    }

    /**
     * Obtener color de fruta por valor
     */
    public Color getFruitColor(int value) {
        return UIConstants.getFruitColor(value);
    }

    /**
     * Actualizar estado del juego (implementar más tarde)
     */
    public void updateGameState(Object gameState) {
        // TODO: Implementar cuando tengamos GameState
        repaint(); // Redibujar panel
    }

    // Implementación de KeyListener para testing
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                System.out.println("Movimiento: ARRIBA");
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                System.out.println("Movimiento: ABAJO");
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                System.out.println("Movimiento: IZQUIERDA");
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                System.out.println("Movimiento: DERECHA");
                break;
            case KeyEvent.VK_G:
                showGrid = !showGrid;
                repaint();
                System.out.println("Grid: " + (showGrid ? "ON" : "OFF"));
                break;
            case KeyEvent.VK_T:
                showTestData = !showTestData;
                repaint();
                System.out.println("Demo data: " + (showTestData ? "ON" : "OFF"));
                break;
            case KeyEvent.VK_SPACE:
                if (testFruits != null && showTestData) {
                    // Obtener posiciones ocupadas por serpientes
                    java.util.List<java.awt.Point> occupied = new java.util.ArrayList<>();
                    if (testSnakes != null) {
                        for (TestSnake snake : testSnakes) {
                            occupied.addAll(snake.getBody());
                        }
                    }
                    // Agregar nueva fruta aleatoria
                    TestFruit newFruit = TestFruit.generateRandomFruit(occupied);
                    testFruits.add(newFruit);
                    repaint();
                    System.out.println("Nueva fruta agregada: " + newFruit);
                }
                break;
            case KeyEvent.VK_R:
                // Reiniciar datos de testing
                initializeTestData();
                repaint();
                System.out.println("Datos de testing reiniciados");
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No necesario por ahora
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No necesario por ahora
    }

}