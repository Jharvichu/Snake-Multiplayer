package main.java.com.snake.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Panel donde se renderiza el juego Snake Multijugador
 * @author Ariana
 */
public class GamePanel extends JPanel implements KeyListener {

    // Estado temporal para testing
    private boolean showGrid = true;

    public GamePanel() {
        initializePanel();
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

        // TODO: Dibujar serpientes cuando tengamos la lógica
        // drawSnakes(g2d, snakesList);

        // TODO: Dibujar frutas cuando tengamos la lógica
        // drawFruits(g2d, fruitsList);

        // Por ahora dibujar mensaje de testing
        drawTestingMessage(g2d);

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
     * Mensaje temporal para testing
     */
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
     * Método para dibujar serpientes (implementar más tarde)
     */
    public void drawSnakes(Graphics2D g2d, java.util.List<Object> snakes) {
        // TODO: Implementar cuando tengamos la clase Snake
        // for (Snake snake : snakes) {
        //     drawSingleSnake(g2d, snake);
        // }
    }

    /**
     * Método para dibujar frutas (implementar más tarde)
     */
    public void drawFruits(Graphics2D g2d, java.util.List<Object> fruits) {
        // TODO: Implementar cuando tengamos la clase Fruit
        // for (Fruit fruit : fruits) {
        //     drawSingleFruit(g2d, fruit);
        // }
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