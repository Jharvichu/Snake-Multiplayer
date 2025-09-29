package main.java.com.snake.ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel avanzado de puntuaciones para Snake Multijugador
 * @author Ariana
 */
public class ScoreboardPanel extends JPanel {

    private List<PlayerScore> playerScores;
    private long gameStartTime;
    private javax.swing.Timer updateTimer;
    private boolean animateScores;

    // Configuración visual
    private static final int PLAYER_ROW_HEIGHT = 35;
    private static final int PADDING = 10;
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font PLAYER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font STAT_FONT = new Font("Arial", Font.PLAIN, 12);

    public ScoreboardPanel() {
        initializePanel();
        initializeTestData();
        startUpdateTimer();
    }

    private void initializePanel() {
        setBackground(new Color(45, 45, 45));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        gameStartTime = System.currentTimeMillis();
        animateScores = true;
    }

    private void initializeTestData() {
        playerScores = new ArrayList<>();
        // Solo inicializar en modo demo - se reemplaza con datos reales en multiplayer
        playerScores.add(new PlayerScore(0, "Jugador 1", 150, 15, true, gameStartTime));
        playerScores.add(new PlayerScore(1, "Jugador 2", 89, 8, true, gameStartTime + 5000));
        playerScores.add(new PlayerScore(2, "Jugador 3", 203, 20, false, gameStartTime + 10000));
        playerScores.add(new PlayerScore(3, "Jugador 4", 67, 6, true, gameStartTime + 15000));

        // Solo simular en modo demo
        if (isDemoMode) {
            javax.swing.Timer scoreChangeTimer = new javax.swing.Timer(3000, e -> simulateScoreChanges());
            scoreChangeTimer.start();
        }
    }
    
    private boolean isDemoMode = true; // Flag para modo demo
    
    public void setMultiplayerMode(boolean isMultiplayer) {
        this.isDemoMode = !isMultiplayer;
        if (isMultiplayer) {
            resetScores(); // Limpiar datos de demo
        }
    }

    private void startUpdateTimer() {
        updateTimer = new javax.swing.Timer(100, e -> repaint());
        updateTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawHeader(g2d);
        drawPlayerRanking(g2d);
        drawGameStatistics(g2d);

        g2d.dispose();
    }

    private void drawHeader(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(HEADER_FONT);

        String title = "🏆 RANKING MULTIJUGADOR";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;

        g2d.drawString(title, x, 25);

        // Línea separadora
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(PADDING, 35, getWidth() - PADDING, 35);
    }

    private void drawPlayerRanking(Graphics2D g2d) {
        // Ordenar jugadores por puntaje
        List<PlayerScore> sortedPlayers = new ArrayList<>(playerScores);
        sortedPlayers.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        int startY = 50;

        for (int i = 0; i < sortedPlayers.size(); i++) {
            PlayerScore player = sortedPlayers.get(i);
            int y = startY + (i * PLAYER_ROW_HEIGHT);

            drawPlayerRow(g2d, player, i + 1, y);
        }
    }

    private void drawPlayerRow(Graphics2D g2d, PlayerScore player, int rank, int y) {
        Color playerColor = UIConstants.getPlayerColor(player.getPlayerId());

        // Fondo de la fila con animación
        Color bgColor = new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), 30);
        if (animateScores && player.hasRecentScoreChange()) {
            bgColor = new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), 60);
        }

        g2d.setColor(bgColor);
        g2d.fillRoundRect(5, y - 20, getWidth() - 10, PLAYER_ROW_HEIGHT - 5, 10, 10);

        // Indicador de ranking
        g2d.setColor(getRankColor(rank));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("#" + rank, 15, y);

        // Indicador de color del jugador
        g2d.setColor(playerColor);
        g2d.fillOval(50, y - 15, 15, 15);
        g2d.setColor(playerColor.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(50, y - 15, 15, 15);

        // Nombre del jugador
        g2d.setColor(Color.WHITE);
        g2d.setFont(PLAYER_FONT);
        g2d.drawString(player.getName(), 75, y);

        // Estado (vivo/muerto)
        String status = player.isAlive() ? "🐍 VIVO" : "💀 MUERTO";
        g2d.setColor(player.isAlive() ? Color.GREEN : Color.RED);
        g2d.setFont(STAT_FONT);
        g2d.drawString(status, 180, y);

        // Puntaje con animación
        g2d.setColor(Color.YELLOW);
        g2d.setFont(PLAYER_FONT);
        String scoreText = String.valueOf(player.getScore()) + " pts";
        if (animateScores && player.hasRecentScoreChange()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
        }
        g2d.drawString(scoreText, 270, y);

        // Frutas comidas
        g2d.setColor(Color.ORANGE);
        g2d.setFont(STAT_FONT);
        g2d.drawString("🍎 " + player.getFruitsEaten(), 360, y);

        // Tiempo vivo
        long survivalTime = player.isAlive() ?
                System.currentTimeMillis() - player.getJoinTime() :
                player.getDeathTime() - player.getJoinTime();

        g2d.setColor(Color.CYAN);
        g2d.drawString("⏱️ " + formatTime(survivalTime), 420, y);
    }

    private void drawGameStatistics(Graphics2D g2d) {
        int statsY = 50 + (playerScores.size() * PLAYER_ROW_HEIGHT) + 20;

        // Separador
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(PADDING, statsY - 10, getWidth() - PADDING, statsY - 10);

        // Estadísticas generales
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(STAT_FONT);

        long gameTime = System.currentTimeMillis() - gameStartTime;
        int alivePlayers = (int) playerScores.stream().mapToInt(p -> p.isAlive() ? 1 : 0).sum();
        int totalFruits = playerScores.stream().mapToInt(PlayerScore::getFruitsEaten).sum();

        g2d.drawString("🕒 Tiempo de juego: " + formatTime(gameTime), 15, statsY + 10);
        g2d.drawString("👥 Jugadores vivos: " + alivePlayers + "/" + playerScores.size(), 200, statsY + 10);
        g2d.drawString("🍎 Total frutas: " + totalFruits, 350, statsY + 10);
    }

    private Color getRankColor(int rank) {
        switch (rank) {
            case 1: return new Color(255, 215, 0); // Oro
            case 2: return new Color(192, 192, 192); // Plata
            case 3: return new Color(205, 127, 50); // Bronce
            default: return Color.WHITE;
        }
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Métodos públicos para actualización desde GameWindow
    public void updatePlayerScore(int playerId, int score, int fruitsEaten, boolean isAlive) {
        for (PlayerScore player : playerScores) {
            if (player.getPlayerId() == playerId) {
                player.updateScore(score, fruitsEaten, isAlive);
                break;
            }
        }
    }

    public void addPlayer(int playerId, String name) {
        playerScores.add(new PlayerScore(playerId, name, 0, 0, true, System.currentTimeMillis()));
    }

    public void removePlayer(int playerId) {
        playerScores.removeIf(p -> p.getPlayerId() == playerId);
    }

    public void resetScores() {
        gameStartTime = System.currentTimeMillis();
        playerScores.clear(); // Limpiar lista de jugadores
    }

    // Simulación para demo
    private void simulateScoreChanges() {
        Random random = new Random();
        for (PlayerScore player : playerScores) {
            if (player.isAlive() && random.nextDouble() < 0.7) {
                int scoreIncrease = random.nextInt(20) + 5;
                player.updateScore(
                        player.getScore() + scoreIncrease,
                        player.getFruitsEaten() + 1,
                        true
                );
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int height = 80 + (playerScores.size() * PLAYER_ROW_HEIGHT) + 50;
        return new Dimension(500, height);
    }

    public void cleanup() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}