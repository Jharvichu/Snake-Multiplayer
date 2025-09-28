package main.java.com.snake.ui;

import main.java.com.snake.game.levels.LevelManager;
import main.java.com.snake.game.levels.Level;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private ScoreboardPanel scoreboardPanel;
    private LevelUI levelUI;
    private LevelManager levelManager;
    private JPanel scorePanel;
    private JLabel statusLabel;
    private JLabel playersLabel;
    private JLabel currentLevelLabel;

    public GameWindow() {
        initializeLevelManager();

        // Mostrar selección de nivel al inicio
        if (!showLevelSelection()) {
            System.exit(0);
            return;
        }

        initializeWindow();
        setupComponents();
        setupLayout();
    }

    private void initializeLevelManager() {
        levelManager = new LevelManager();
    }

    private boolean showLevelSelection() {
        Level selectedLevel = LevelSelectionDialog.showLevelSelection(this, levelManager);

        if (selectedLevel != null) {
            levelManager.setLevel(selectedLevel.getId());
            return true;
        }
        return false;
    }

    private void initializeWindow() {
        setTitle(UIConstants.WINDOW_TITLE);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(UIConstants.WINDOW_RESIZABLE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void setupComponents() {
        // Panel principal del juego
        gamePanel = new GamePanel();
        gamePanel.setLevelManager(levelManager);

        // Panel de puntuaciones
        scoreboardPanel = new ScoreboardPanel();
        gamePanel.setScoreboardPanel(scoreboardPanel);

        // Panel de control de niveles
        levelUI = new LevelUI();
        levelUI.setLevelManager(levelManager);
        levelUI.setPreferredSize(new Dimension(520, 200));

        // Panel de información básica
        scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(UIConstants.WINDOW_WIDTH, UIConstants.SCORE_PANEL_HEIGHT));
        scorePanel.setBackground(Color.DARK_GRAY);
        scorePanel.setBorder(BorderFactory.createTitledBorder("Estado del Juego"));

        statusLabel = new JLabel("Conectando al servidor...");
        statusLabel.setForeground(UIConstants.TEXT_COLOR);
        statusLabel.setFont(UIConstants.SCORE_FONT);

        playersLabel = new JLabel("Jugadores: 0");
        playersLabel.setForeground(UIConstants.TEXT_COLOR);
        playersLabel.setFont(UIConstants.SCORE_FONT);

        // Label para mostrar nivel actual
        Level currentLevel = levelManager.getCurrentLevel();
        currentLevelLabel = new JLabel("Nivel: " + currentLevel.getId() + " - " + currentLevel.getName());
        currentLevelLabel.setForeground(Color.CYAN);
        currentLevelLabel.setFont(new Font("Arial", Font.BOLD, 14));

        scorePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        scorePanel.add(statusLabel);
        scorePanel.add(Box.createHorizontalStrut(20));
        scorePanel.add(playersLabel);
        scorePanel.add(Box.createHorizontalStrut(20));
        scorePanel.add(currentLevelLabel);

        // Botón para cambiar nivel durante el juego
        JButton changeLevelButton = new JButton("Cambiar Nivel");
        changeLevelButton.addActionListener(e -> changeLevelDuringGame());
        scorePanel.add(Box.createHorizontalStrut(20));
        scorePanel.add(changeLevelButton);
    }

    private void changeLevelDuringGame() {
        Level newLevel = LevelSelectionDialog.showLevelSelection(this, levelManager);
        if (newLevel != null) {
            levelManager.setLevel(newLevel.getId());
            currentLevelLabel.setText("Nivel: " + newLevel.getId() + " - " + newLevel.getName());

            // CORRECCIÓN: Usar onLevelChanged en lugar de resetGame
            if (gamePanel != null) {
                gamePanel.onLevelChanged(newLevel);
            }

            // Mostrar mensaje de confirmación
            JOptionPane.showMessageDialog(this,
                    "Nivel cambiado a: " + newLevel.getName() + "\nEl juego se reiniciará.",
                    "Nivel Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        add(scorePanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scoreboardPanel, BorderLayout.CENTER);
        rightPanel.add(levelUI, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
    }

    public void updateLevelProgress(int playerScore) {
        if (levelUI != null) {
            levelUI.updateProgress(playerScore);
        }
    }

    public void updateScoreDisplay(List<String> playerScores) {
        if (scoreboardPanel != null) {
            // CORRECCIÓN: Usar updatePlayerScore en lugar de updateScores
            // Esto es un placeholder - deberías adaptarlo a tu estructura real de datos
            for (int i = 0; i < playerScores.size(); i++) {
                scoreboardPanel.updatePlayerScore(i, i * 100, i * 5, true);
            }
        }
        if (playersLabel != null) {
            playersLabel.setText("Jugadores: " + playerScores.size());
        }
    }

    public void showGameOverDialog(String winner) {
        String message = "¡Juego terminado!\n\nGanador: " + winner;
        int option = JOptionPane.showOptionDialog(this, message, "Fin del Juego",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"Nuevo Juego", "Cambiar Nivel", "Salir"}, "Nuevo Juego");

        if (option == 0) { // Nuevo Juego
            statusLabel.setText("Iniciando nuevo juego...");
            levelManager.resetToFirstLevel();
            if (gamePanel != null) {
                // CORRECCIÓN: Usar onLevelChanged en lugar de resetGame
                gamePanel.onLevelChanged(levelManager.getCurrentLevel());
            }
        } else if (option == 1) { // Cambiar Nivel
            changeLevelDuringGame();
        } else { // Salir
            System.exit(0);
        }
    }

    public void updateConnectionStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    public void setGameClient(main.java.com.snake.client.GameClient gameClient) {
        gamePanel.setGameClient(gameClient);
        updateConnectionStatus("Cliente conectado");
    }

    public GamePanel getGamePanel() { return gamePanel; }
    public LevelManager getLevelManager() { return levelManager; }

    private void handleWindowClosing() {
        if (levelUI != null) {
            levelUI.cleanup();
        }
        System.out.println("Cerrando aplicación...");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // CORRECCIÓN: Usar look and feel cross-platform
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            GameWindow window = new GameWindow();
            window.setVisible(true);
            window.updateConnectionStatus("Modo de prueba - Nivel seleccionado");
        });
    }
}