package main.java.com.snake.ui;

import main.java.com.snake.game.levels.Level;
import main.java.com.snake.game.levels.LevelManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

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
    private boolean showMessage = true;
    private Timer animationTimer;
    private Timer reviveTimer;
    private Timer fruitSpawnTimer;
    private ScoreboardPanel scoreboardPanel; // Referencia al scoreboard

    // NUEVOS CAMPOS PARA NIVELES
    private Level currentLevel;
    private LevelManager levelManager;

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

        setPreferredSize(new Dimension(UIConstants.GAME_AREA_WIDTH, UIConstants.GAME_PANEL_HEIGHT));
        setMinimumSize(new Dimension(UIConstants.GAME_AREA_WIDTH, UIConstants.GAME_PANEL_HEIGHT));
    }

    /**
     * Conectar con el scoreboard para actualizaciones en tiempo real
     */
    public void setScoreboardPanel(ScoreboardPanel scoreboardPanel) {
        this.scoreboardPanel = scoreboardPanel;
    }

    /**
     * NUEVO: Establecer el LevelManager
     */
    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        if (levelManager != null) {
            this.currentLevel = levelManager.getCurrentLevel();
        }
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

        // NUEVO: Actualizar obstáculos móviles antes de dibujar
        if (currentLevel != null && currentLevel.getId() == 4) {
            currentLevel.updateMovingObstacles();
        }

        // Dibujar obstáculos del nivel actual
        if (currentLevel != null) {
            drawLevelObstacles(g2d, currentLevel);
        }

        // Dibujar datos de testing si están activos
        if (showTestData) {
            if (testSnakes != null) {
                drawTestSnakes(g2d, testSnakes);
            }
            if (testFruits != null) {
                drawTestFruits(g2d, testFruits);
            }
        }

        // Mostrar mensaje solo si está habilitado
        if (showMessage) {
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

    // ========== NUEVOS MÉTODOS PARA NIVELES ==========

    /**
     * Dibujar obstáculos del nivel actual (CORREGIDO para incluir obstáculos móviles)
     */
    private void drawLevelObstacles(Graphics2D g2d, Level level) {
        if (level == null) {
            return;
        }

        // CAMBIO CRÍTICO: Usar getAllObstacles() en lugar de getObstacles()
        List<Point> allObstacles = level.getAllObstacles();
        if (allObstacles.isEmpty()) {
            return;
        }

        // Color del obstáculo según el nivel
        Color obstacleColor = getLevelObstacleColor(level.getId());

        // Dibujar cada obstáculo (estáticos + móviles)
        for (Point obstacle : allObstacles) {
            int x = UIConstants.gameToPixelX(obstacle.x);
            int y = UIConstants.gameToPixelY(obstacle.y);

            // Determinar si es móvil para aplicar efecto visual diferente
            boolean isMoving = level.getMovingObstacles().contains(obstacle);

            if (isMoving) {
                // Efecto especial para obstáculos móviles
                g2d.setColor(obstacleColor.brighter());
                g2d.fillRect(x, y, UIConstants.CELL_SIZE, UIConstants.CELL_SIZE);

                // Borde parpadeante para obstáculos móviles
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x, y, UIConstants.CELL_SIZE - 1, UIConstants.CELL_SIZE - 1);

                // Indicador de movimiento (flecha)
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(x + 6, y + 6, 6, 6);

            } else {
                // Obstáculo estático normal
                g2d.setColor(obstacleColor);
                g2d.fillRect(x, y, UIConstants.CELL_SIZE, UIConstants.CELL_SIZE);

                // Borde más oscuro
                g2d.setColor(obstacleColor.darker());
                g2d.drawRect(x, y, UIConstants.CELL_SIZE - 1, UIConstants.CELL_SIZE - 1);

                // Líneas decorativas para obstáculos estáticos
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(x + 2, y + 2, x + UIConstants.CELL_SIZE - 3, y + UIConstants.CELL_SIZE - 3);
                g2d.drawLine(x + UIConstants.CELL_SIZE - 3, y + 2, x + 2, y + UIConstants.CELL_SIZE - 3);
            }
        }

        // Debug: Mostrar información de obstáculos en consola
        if (level.getId() == 4 && level.hasMovingObstacles()) {
            System.out.println("Nivel 4: Dibujando " + allObstacles.size() + " obstáculos (" +
                    level.getMovingObstacles().size() + " móviles)");
        }
    }

    /**
     * Obtener color de obstáculos según el nivel
     */
    private Color getLevelObstacleColor(int levelId) {
        switch (levelId) {
            case 1: return new Color(100, 100, 100);    // Gris - Nivel básico
            case 2: return new Color(139, 69, 19);      // Marrón - Nivel intermedio
            case 3: return new Color(105, 105, 105);    // Gris oscuro - Nivel avanzado
            case 4: return new Color(128, 0, 0);        // Rojo oscuro - Nivel experto
            default: return Color.GRAY;
        }
    }

    /**
     * Método para actualizar cuando cambia el nivel
     */
    public void onLevelChanged(Level newLevel) {
        this.currentLevel = newLevel;

        // Reiniciar datos de testing para el nuevo nivel
        if (showTestData) {
            resetTestDataForLevel();
        }

        // Actualizar mensaje de testing
        if (showMessage) {
            showMessage = true; // Forzar mostrar mensaje del nuevo nivel
        }

        repaint();
    }

    /**
     * Reiniciar datos de testing para el nivel actual
     */
    private void resetTestDataForLevel() {
        if (animationTimer != null) animationTimer.stop();
        if (reviveTimer != null) reviveTimer.stop();
        if (fruitSpawnTimer != null) fruitSpawnTimer.stop();

        // Crear serpientes adaptadas al nivel
        if (currentLevel != null) {
            testSnakes = TestSnake.createTestSnakesForLevel(currentLevel);
        } else {
            testSnakes = TestSnake.createTestSnakes();
        }

        testFruits = TestFruit.createTestFruits();

        // Reiniciar timers
        animationTimer.restart();
        reviveTimer.restart();
        fruitSpawnTimer.restart();
    }

    private void initializeTestData() {
        // Usar serpientes adaptadas al nivel si existe
        if (currentLevel != null) {
            testSnakes = TestSnake.createTestSnakesForLevel(currentLevel);
        } else {
            testSnakes = TestSnake.createTestSnakes();
        }
        testFruits = TestFruit.createTestFruits();

        // Timer para animar serpientes con lógica completa
        animationTimer = new Timer(200, e -> {
            if (showTestData && testSnakes != null && testFruits != null) {
                // Mover cada serpiente con detección completa
                for (TestSnake snake : testSnakes) {
                    if (snake.isAlive()) {
                        snake.move(testSnakes, testFruits);
                    }
                }

                // Actualizar scoreboard en tiempo real
                updateScoreboard();
                repaint();
            }
        });
        animationTimer.start(); // Animación automática activada

        // Timer para revivir serpientes muertas
        reviveTimer = new Timer(5000, e -> {
            if (showTestData && testSnakes != null) {
                Random random = new Random();
                for (TestSnake snake : testSnakes) {
                    if (!snake.isAlive()) {
                        // Revivir serpiente en posición aleatoria segura
                        int safeX = 5 + random.nextInt(Math.max(1, UIConstants.BOARD_WIDTH - 10));
                        int safeY = 5 + random.nextInt(Math.max(1, 30 - 10)); // Usar límite Y=30
                        snake.reset(safeX, safeY, 5);
                        System.out.println("Serpiente " + snake.getPlayerId() + " revivida en (" + safeX + "," + safeY + ")");
                    }
                }
            }
        });
        reviveTimer.start();

        // Timer para generar nuevas frutas
        fruitSpawnTimer = new Timer(3000, e -> {
            if (showTestData && testFruits != null && testFruits.size() < 8) {
                spawnNewFruit();
            }
        });
        fruitSpawnTimer.start();

        // NUEVO: Timer para actualizar obstáculos móviles
        Timer obstacleUpdateTimer = new Timer(100, e -> {
            if (currentLevel != null && currentLevel.getId() == 4) {
                currentLevel.updateMovingObstacles();
            }
        });
        obstacleUpdateTimer.start();
    }

    /**
     * Actualizar scoreboard en tiempo real
     */
    private void updateScoreboard() {
        if (scoreboardPanel != null && testSnakes != null) {
            for (TestSnake snake : testSnakes) {
                scoreboardPanel.updatePlayerScore(
                        snake.getPlayerId(),
                        snake.getScore(),
                        snake.getFruitsEaten(),
                        snake.isAlive()
                );
            }
        }
    }

    /**
     * Generar nueva fruta en posición libre (CORREGIDO)
     */
    private void spawnNewFruit() {
        List<Point> occupied = new ArrayList<>();

        // Agregar posiciones ocupadas por serpientes
        for (TestSnake snake : testSnakes) {
            if (snake.isAlive()) {
                occupied.addAll(snake.getBody());
            }
        }

        // Agregar posiciones ocupadas por frutas existentes
        for (TestFruit fruit : testFruits) {
            if (fruit.isActive()) {
                occupied.add(fruit.getPosition());
            }
        }

        // CAMBIO CRÍTICO: Usar getAllObstacles() en lugar de getObstacles()
        if (currentLevel != null) {
            occupied.addAll(currentLevel.getAllObstacles()); // ← Cambio aquí
        }

        TestFruit newFruit = TestFruit.generateRandomFruit(occupied);
        if (newFruit != null) {
            testFruits.add(newFruit);
        }
    }

    /**
     * MODIFICADO: Mostrar información del nivel actual
     */
    private void drawTestingMessage(Graphics2D g2d) {
        g2d.setColor(UIConstants.TEXT_COLOR);
        g2d.setFont(UIConstants.TITLE_FONT);

        String message = "Snake Multijugador";
        if (currentLevel != null) {
            message += " - " + currentLevel.getName();
        }

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2 - 40;

        g2d.drawString(message, x, y);

        // Información del nivel actual
        if (currentLevel != null) {
            g2d.setFont(UIConstants.SCORE_FONT);
            String levelInfo = String.format("Nivel %d: %s | Meta: %d puntos | Velocidad: %dms",
                    currentLevel.getId(),
                    currentLevel.getDescription(),
                    currentLevel.getRequiredScore(),
                    currentLevel.getGameSpeed());

            fm = g2d.getFontMetrics();
            x = (getWidth() - fm.stringWidth(levelInfo)) / 2;
            y = y + 25;

            g2d.setColor(Color.CYAN);
            g2d.drawString(levelInfo, x, y);
        }

        // Instrucciones
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        String instructions = "WASD/Flechas: mover | G: grid | T: serpientes/frutas | M: mensaje | R: reset | L: cambiar nivel";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(instructions)) / 2;
        y = y + 25;

        g2d.setColor(Color.LIGHT_GRAY);
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
     * Actualizar estado del juego
     */
    public void updateGameState(String gameStateJson) {
        if (gameStateJson != null) {
            System.out.println("Estado actualizado: " + gameStateJson);
            repaint();
        }
    }

    /**
     * Conectar con el cliente real del juego
     */
    public void setGameClient(main.java.com.snake.client.GameClient client) {
        // Remover el KeyListener de testing
        for (java.awt.event.KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }

        // Agregar InputHandler real
        InputHandler realInputHandler = new InputHandler();
        realInputHandler.setGameClient(client);
        addKeyListener(realInputHandler);
        setFocusable(true);
        requestFocus();

        // Desactivar datos de testing y mostrar mensaje de conexión
        showTestData = false;
        showMessage = true;
        if (animationTimer != null) animationTimer.stop();
        if (reviveTimer != null) reviveTimer.stop();
        if (fruitSpawnTimer != null) fruitSpawnTimer.stop();

        System.out.println("GamePanel conectado con cliente real");
        repaint();
    }

    // ========== KEYLISTER MODIFICADO ==========

    /**
     * MODIFICADO: Incluir cambio de nivel con tecla L
     */
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
                if (animationTimer != null) {
                    if (showTestData) {
                        animationTimer.start();
                        reviveTimer.start();
                        fruitSpawnTimer.start();
                    } else {
                        animationTimer.stop();
                        reviveTimer.stop();
                        fruitSpawnTimer.stop();
                    }
                }
                repaint();
                System.out.println("Serpientes y frutas: " + (showTestData ? "ON" : "OFF"));
                break;
            case KeyEvent.VK_M:
                showMessage = !showMessage;
                repaint();
                System.out.println("Mensaje: " + (showMessage ? "ON" : "OFF"));
                break;
            case KeyEvent.VK_L:
                // NUEVO: Cambiar nivel con L
                if (levelManager != null) {
                    showLevelSelectionInGame();
                } else {
                    System.out.println("LevelManager no disponible");
                }
                break;
            case KeyEvent.VK_SPACE:
                if (testFruits != null && showTestData) {
                    spawnNewFruit();
                    repaint();
                    System.out.println("Nueva fruta generada");
                }
                break;
            case KeyEvent.VK_R:
                // Reiniciar datos de testing
                if (animationTimer != null) animationTimer.stop();
                if (reviveTimer != null) reviveTimer.stop();
                if (fruitSpawnTimer != null) fruitSpawnTimer.stop();
                initializeTestData();
                showTestData = true;
                showMessage = true;
                showGrid = true;
                repaint();
                System.out.println("Datos de testing reiniciados");
                break;
            default:
                break;
        }
    }

    /**
     * NUEVO: Mostrar selección de nivel durante el juego
     */
    private void showLevelSelectionInGame() {
        SwingUtilities.invokeLater(() -> {
            // Encontrar la ventana padre
            Container parent = getParent();
            while (parent != null && !(parent instanceof GameWindow)) {
                parent = parent.getParent();
            }

            if (parent instanceof GameWindow) {
                GameWindow gameWindow = (GameWindow) parent;
                LevelManager manager = gameWindow.getLevelManager();

                Level newLevel = LevelSelectionDialog.showLevelSelection(gameWindow, manager);
                if (newLevel != null) {
                    manager.setLevel(newLevel.getId());
                    onLevelChanged(newLevel);
                    System.out.println("Nivel cambiado a: " + newLevel.getName());
                }
            } else {
                // Fallback: usar LevelManager directo si está disponible
                if (levelManager != null) {
                    Level newLevel = LevelSelectionDialog.showLevelSelection(null, levelManager);
                    if (newLevel != null) {
                        levelManager.setLevel(newLevel.getId());
                        onLevelChanged(newLevel);
                        System.out.println("Nivel cambiado a: " + newLevel.getName());
                    }
                }
            }
        });
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