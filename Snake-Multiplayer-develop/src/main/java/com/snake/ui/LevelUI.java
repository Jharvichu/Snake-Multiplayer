package main.java.com.snake.ui;

import main.java.com.snake.game.levels.Level;
import main.java.com.snake.game.levels.LevelManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Componente UI para mostrar y controlar niveles
 */
public class LevelUI extends JPanel implements LevelManager.LevelObserver {
    private LevelManager levelManager;
    private JLabel currentLevelLabel;
    private JLabel levelDescLabel;
    private JLabel requirementsLabel;
    private JLabel timeRemainingLabel;
    private JProgressBar progressBar;
    private JButton nextLevelButton;
    private JButton resetButton;

    private long gameStartTime;
    private Timer uiUpdateTimer;

    public LevelUI() {
        initializeUI();
        setupTimer();
    }

    private void initializeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Control de Niveles"));
        setBackground(new Color(60, 60, 60));

        // Panel de información del nivel actual
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setOpaque(false);

        currentLevelLabel = new JLabel("Nivel 1: Básico");
        currentLevelLabel.setForeground(Color.WHITE);
        currentLevelLabel.setFont(new Font("Arial", Font.BOLD, 14));

        levelDescLabel = new JLabel("Nivel básico sin obstáculos");
        levelDescLabel.setForeground(Color.LIGHT_GRAY);
        levelDescLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        requirementsLabel = new JLabel("Puntos requeridos: 50");
        requirementsLabel.setForeground(Color.YELLOW);
        requirementsLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        timeRemainingLabel = new JLabel("Tiempo: 2:00");
        timeRemainingLabel.setForeground(Color.CYAN);
        timeRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(currentLevelLabel);
        infoPanel.add(levelDescLabel);
        infoPanel.add(requirementsLabel);
        infoPanel.add(timeRemainingLabel);

        // Barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Progreso: 0%");

        // Botones de control
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        nextLevelButton = new JButton("Siguiente Nivel");
        nextLevelButton.setEnabled(false);
        nextLevelButton.addActionListener(this::onNextLevel);

        resetButton = new JButton("Reiniciar");
        resetButton.addActionListener(this::onReset);

        buttonPanel.add(nextLevelButton);
        buttonPanel.add(resetButton);

        // Agregar todos los componentes
        add(infoPanel);
        add(Box.createVerticalStrut(10));
        add(progressBar);
        add(Box.createVerticalStrut(10));
        add(buttonPanel);
    }

    private void setupTimer() {
        uiUpdateTimer = new Timer(1000, e -> updateTimeDisplay());
        gameStartTime = System.currentTimeMillis();
        uiUpdateTimer.start();
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        levelManager.setObserver(this);
        updateUI(levelManager.getCurrentLevel());
    }

    /**
     * Actualizar progreso basado en puntuación del jugador
     */
    public void updateProgress(int currentScore) {
        if (levelManager == null) return;

        Level current = levelManager.getCurrentLevel();
        int required = current.getRequiredScore();
        int progress = Math.min(100, (currentScore * 100) / required);

        progressBar.setValue(progress);
        progressBar.setString(String.format("Progreso: %d%% (%d/%d pts)",
                progress, currentScore, required));

        // Habilitar botón de siguiente nivel si se cumplieron los requisitos
        nextLevelButton.setEnabled(currentScore >= required && levelManager.hasNextLevel());

        // Auto-avance si se completa el nivel
        if (currentScore >= required && levelManager.hasNextLevel()) {
            // Opcional: Auto-avance después de 2 segundos
            Timer autoAdvance = new Timer(2000, evt -> {
                if (nextLevelButton.isEnabled()) {
                    onNextLevel(null);
                }
            });
            autoAdvance.setRepeats(false);
            autoAdvance.start();
        }
    }

    private void updateTimeDisplay() {
        if (levelManager == null) return;

        Level current = levelManager.getCurrentLevel();
        long elapsed = System.currentTimeMillis() - gameStartTime;
        long remaining = Math.max(0, current.getMaxTime() - elapsed);

        long minutes = remaining / 60000;
        long seconds = (remaining % 60000) / 1000;

        timeRemainingLabel.setText(String.format("Tiempo: %d:%02d", minutes, seconds));

        // Cambiar color según tiempo restante
        if (remaining < 30000) { // Menos de 30 segundos
            timeRemainingLabel.setForeground(Color.RED);
        } else if (remaining < 60000) { // Menos de 1 minuto
            timeRemainingLabel.setForeground(Color.ORANGE);
        } else {
            timeRemainingLabel.setForeground(Color.CYAN);
        }

        // Game over si se acaba el tiempo
        if (remaining <= 0) {
            uiUpdateTimer.stop();
            JOptionPane.showMessageDialog(this,
                    "¡Tiempo agotado!\nReinicia el nivel para intentar de nuevo.",
                    "Tiempo Agotado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateUI(Level level) {
        if (level == null) return;

        currentLevelLabel.setText(String.format("Nivel %d: %s", level.getId(), level.getName()));
        levelDescLabel.setText(level.getDescription());
        requirementsLabel.setText(String.format("Puntos requeridos: %d", level.getRequiredScore()));

        long minutes = level.getMaxTime() / 60000;
        timeRemainingLabel.setText(String.format("Tiempo límite: %d:00", minutes));

        progressBar.setValue(0);
        progressBar.setString("Progreso: 0%");
        nextLevelButton.setEnabled(false);

        // Reiniciar timer
        gameStartTime = System.currentTimeMillis();
        if (uiUpdateTimer != null) {
            uiUpdateTimer.restart();
        }
    }

    private void onNextLevel(ActionEvent e) {
        if (levelManager != null) {
            boolean advanced = levelManager.nextLevel();
            if (!advanced) {
                JOptionPane.showMessageDialog(this,
                        "¡Felicitaciones!\n¡Has completado todos los niveles!",
                        "¡Victoria Total!",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void onReset(ActionEvent e) {
        if (levelManager != null) {
            levelManager.resetToFirstLevel();
            gameStartTime = System.currentTimeMillis();
            if (uiUpdateTimer != null) {
                uiUpdateTimer.restart();
            }
        }
    }

    // Implementación de LevelObserver
    @Override
    public void onLevelChanged(Level newLevel) {
        SwingUtilities.invokeLater(() -> {
            updateUI(newLevel);
            JOptionPane.showMessageDialog(this,
                    String.format("¡Nivel %d desbloqueado!\n%s", newLevel.getId(), newLevel.getDescription()),
                    "Nuevo Nivel",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @Override
    public void onLevelCompleted(Level completedLevel) {
        SwingUtilities.invokeLater(() -> {
            // CORRECCIÓN: Cambiar SUCCESS_MESSAGE por INFORMATION_MESSAGE
            JOptionPane.showMessageDialog(this,
                    String.format("¡Nivel %d completado!\n%s", completedLevel.getId(), completedLevel.getName()),
                    "Nivel Completado",
                    JOptionPane.INFORMATION_MESSAGE); // CORREGIDO
        });
    }

    @Override
    public void onAllLevelsCompleted() {
        SwingUtilities.invokeLater(() -> {
            uiUpdateTimer.stop();
            JOptionPane.showMessageDialog(this,
                    "¡FELICITACIONES!\n¡Has dominado todos los niveles del Snake Multijugador!",
                    "¡MAESTRO SNAKE!",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void cleanup() {
        if (uiUpdateTimer != null) {
            uiUpdateTimer.stop();
        }
    }
}