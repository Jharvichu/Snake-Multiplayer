package main.java.com.snake.ui;

import main.java.com.snake.game.levels.Level;
import main.java.com.snake.game.levels.LevelManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Diálogo de selección de nivel al inicio del juego
 */
public class LevelSelectionDialog extends JDialog {
    private LevelManager levelManager;
    private Level selectedLevel;
    private boolean levelSelected = false;

    private JPanel levelCardsPanel;
    private JButton startButton;
    private JButton cancelButton;

    public LevelSelectionDialog(Frame parent, LevelManager levelManager) {
        super(parent, "Seleccionar Nivel - Snake Multijugador", true);
        this.levelManager = levelManager;

        initializeDialog();
        createLevelCards();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeDialog() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Estilo oscuro
        getContentPane().setBackground(new Color(40, 40, 40));
    }

    private void createLevelCards() {
        levelCardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        levelCardsPanel.setOpaque(false);
        levelCardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        List<Level> levels = levelManager.getAllLevels();

        for (Level level : levels) {
            JPanel card = createLevelCard(level);
            levelCardsPanel.add(card);
        }
    }

    private JPanel createLevelCard(Level level) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createRaisedBevelBorder());
        card.setBackground(new Color(60, 60, 60));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Header con número y nombre del nivel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel levelNumber = new JLabel(String.valueOf(level.getId()));
        levelNumber.setFont(new Font("Arial", Font.BOLD, 36));
        levelNumber.setForeground(getLevelColor(level.getId()));

        JLabel levelName = new JLabel(level.getName());
        levelName.setFont(new Font("Arial", Font.BOLD, 18));
        levelName.setForeground(Color.WHITE);

        headerPanel.add(levelNumber);
        headerPanel.add(Box.createHorizontalStrut(10));
        headerPanel.add(levelName);

        // Panel central con información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Descripción
        JLabel descLabel = new JLabel("<html><center>" + level.getDescription() + "</center></html>");
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Estadísticas del nivel
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        statsPanel.setOpaque(false);

        statsPanel.add(createStatLabel("⚡ Velocidad:", getSpeedText(level.getGameSpeed())));
        statsPanel.add(createStatLabel("🎯 Meta:", level.getRequiredScore() + " puntos"));
        statsPanel.add(createStatLabel("⏱️ Tiempo:", formatTime(level.getMaxTime())));
        statsPanel.add(createStatLabel("🧱 Obstáculos:", level.getObstacles().size() + " bloques"));

        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(statsPanel);

        // Preview visual del nivel
        JPanel previewPanel = createLevelPreview(level);

        // Ensamblar la tarjeta
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(previewPanel, BorderLayout.SOUTH);

        // Evento de selección
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectLevel(level);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(80, 80, 80));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(getLevelColor(level.getId()), 2),
                        BorderFactory.createRaisedBevelBorder()
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedLevel != level) {
                    card.setBackground(new Color(60, 60, 60));
                    card.setBorder(BorderFactory.createRaisedBevelBorder());
                }
            }
        });

        return card;
    }

    private JPanel createStatLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(Color.YELLOW);
        labelComp.setFont(new Font("Arial", Font.BOLD, 11));

        JLabel valueComp = new JLabel(" " + value);
        valueComp.setForeground(Color.WHITE);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 11));

        panel.add(labelComp);
        panel.add(valueComp);

        return panel;
    }

    private JPanel createLevelPreview(Level level) {
        JPanel preview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMiniLevel(g, level);
            }
        };

        preview.setPreferredSize(new Dimension(150, 80));
        preview.setBackground(Color.BLACK);
        preview.setBorder(BorderFactory.createLoweredBevelBorder());

        return preview;
    }

    private void drawMiniLevel(Graphics g, Level level) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Escala para ajustar el nivel al preview
        double scaleX = width / (double) level.getWidth();
        double scaleY = height / (double) level.getHeight();
        double scale = Math.min(scaleX, scaleY) * 0.8; // 80% para margen

        // Dibujar grid
        g2d.setColor(new Color(30, 30, 30));
        for (int x = 0; x < level.getWidth(); x++) {
            int pixelX = (int) (x * scale);
            g2d.drawLine(pixelX, 0, pixelX, height);
        }
        for (int y = 0; y < level.getHeight(); y++) {
            int pixelY = (int) (y * scale);
            g2d.drawLine(0, pixelY, width, pixelY);
        }

        // Dibujar obstáculos
        g2d.setColor(getLevelColor(level.getId()));
        for (Point obstacle : level.getObstacles()) {
            int x = (int) (obstacle.x * scale);
            int y = (int) (obstacle.y * scale);
            int size = Math.max(2, (int) scale);
            g2d.fillRect(x, y, size, size);
        }

        // Dibujar bordes
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, width - 1, height - 1);
    }

    private void selectLevel(Level level) {
        // Limpiar selección anterior
        clearPreviousSelection();

        // Marcar nueva selección
        selectedLevel = level;
        levelSelected = true;

        // Actualizar UI
        Component[] cards = levelCardsPanel.getComponents();
        for (int i = 0; i < cards.length; i++) {
            if (i == level.getId() - 1) { // ID del nivel - 1 = índice
                JPanel card = (JPanel) cards[i];
                card.setBackground(new Color(100, 100, 100));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(getLevelColor(level.getId()), 3),
                        BorderFactory.createRaisedBevelBorder()
                ));
            }
        }

        startButton.setEnabled(true);
        startButton.setText("Iniciar " + level.getName());
    }

    private void clearPreviousSelection() {
        Component[] cards = levelCardsPanel.getComponents();
        for (Component card : cards) {
            JPanel panel = (JPanel) card;
            panel.setBackground(new Color(60, 60, 60));
            panel.setBorder(BorderFactory.createRaisedBevelBorder());
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Título
        JLabel titleLabel = new JLabel("Selecciona tu Nivel de Desafío", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        startButton = new JButton("Selecciona un Nivel");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setPreferredSize(new Dimension(200, 40));
        startButton.setEnabled(false);

        cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(cancelButton);

        add(titleLabel, BorderLayout.NORTH);
        add(levelCardsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        startButton.addActionListener(e -> {
            if (selectedLevel != null) {
                levelManager.setLevel(selectedLevel.getId());
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            levelSelected = false;
            selectedLevel = null;
            dispose();
        });
    }

    // Métodos utilitarios
    private Color getLevelColor(int levelId) {
        Color[] colors = {
                new Color(46, 204, 113),  // Verde - Fácil
                new Color(52, 152, 219),  // Azul - Intermedio
                new Color(230, 126, 34),  // Naranja - Avanzado
                new Color(231, 76, 60)    // Rojo - Experto
        };
        return colors[Math.min(levelId - 1, colors.length - 1)];
    }

    private String getSpeedText(int gameSpeed) {
        if (gameSpeed >= 200) return "Lenta";
        if (gameSpeed >= 150) return "Normal";
        if (gameSpeed >= 120) return "Rápida";
        return "Muy Rápida";
    }

    private String formatTime(long milliseconds) {
        long minutes = milliseconds / 60000;
        return minutes + " min";
    }

    // Getters
    public Level getSelectedLevel() { return selectedLevel; }
    public boolean isLevelSelected() { return levelSelected; }

    /**
     * Mostrar diálogo y devolver el nivel seleccionado
     */
    public static Level showLevelSelection(Frame parent, LevelManager levelManager) {
        LevelSelectionDialog dialog = new LevelSelectionDialog(parent, levelManager);
        dialog.setVisible(true);

        if (dialog.isLevelSelected()) {
            return dialog.getSelectedLevel();
        }
        return null;
    }
}