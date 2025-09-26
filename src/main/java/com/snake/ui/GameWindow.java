package main.java.com.snake.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;

/**
 * Ventana principal del juego Snake Multijugador
 * @author Ariana
 */
public class GameWindow extends JFrame {

    private GamePanel gamePanel;
    private JPanel scorePanel;
    private JLabel statusLabel;
    private JLabel playersLabel;

    public GameWindow() {
        initializeWindow();
        setupComponents();
        setupLayout();
    }

    /**
     * Configurar propiedades básicas de la ventana
     */
    private void initializeWindow() {
        setTitle(UIConstants.WINDOW_TITLE);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana
        setResizable(UIConstants.WINDOW_RESIZABLE);

        // Configurar cierre limpio
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    /**
     * Crear y configurar componentes de la interfaz
     */
    private void setupComponents() {
        // Panel principal del juego
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(
                UIConstants.WINDOW_WIDTH,
                UIConstants.GAME_PANEL_HEIGHT
        ));

        // Panel de puntuaciones en la parte superior
        scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(
                UIConstants.WINDOW_WIDTH,
                UIConstants.SCORE_PANEL_HEIGHT
        ));
        scorePanel.setBackground(Color.DARK_GRAY);
        scorePanel.setBorder(BorderFactory.createTitledBorder("Puntuaciones"));

        // Labels informativos
        statusLabel = new JLabel("Conectando al servidor...");
        statusLabel.setForeground(UIConstants.TEXT_COLOR);
        statusLabel.setFont(UIConstants.SCORE_FONT);

        playersLabel = new JLabel("Jugadores: 0");
        playersLabel.setForeground(UIConstants.TEXT_COLOR);
        playersLabel.setFont(UIConstants.SCORE_FONT);

        // Agregar componentes al panel de puntuaciones
        scorePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        scorePanel.add(statusLabel);
        scorePanel.add(Box.createHorizontalStrut(20));
        scorePanel.add(playersLabel);
    }

    /**
     * Configurar el layout de la ventana
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(scorePanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
    }

    /**
     * Actualizar el display de puntuaciones de los jugadores
     * @param playerScores Lista de jugadores con sus puntuaciones
     */
    public void updateScoreDisplay(java.util.List<String> playerScores) {
        // Por ahora solo actualizar el contador de jugadores
        playersLabel.setText("Jugadores: " + playerScores.size());

        // TODO: Crear labels individuales para cada jugador
        // cuando tengamos la clase Player definida
    }

    /**
     * Mostrar diálogo de fin de juego
     * @param winner Nombre del jugador ganador
     */
    public void showGameOverDialog(String winner) {
        String message = "¡Juego terminado!\n\nGanador: " + winner;

        int option = JOptionPane.showOptionDialog(
                this,
                message,
                "Fin del Juego",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Nuevo Juego", "Salir"},
                "Nuevo Juego"
        );

        if (option == JOptionPane.YES_OPTION) {
            // TODO: Reiniciar juego
            statusLabel.setText("Iniciando nuevo juego...");
        } else {
            // Cerrar aplicación
            System.exit(0);
        }
    }

    /**
     * Actualizar el estado de conexión mostrado
     * @param status Mensaje de estado actual
     */
    public void updateConnectionStatus(String status) {
        statusLabel.setText(status);
    }

    /**
     * Conectar con el cliente del juego
     * @param gameClient Cliente que manejará la comunicación
     */
    public void setGameClient(Object gameClient) {
        // TODO: Implementar cuando tengamos la clase GameClient
        // this.gameClient = gameClient;
        updateConnectionStatus("Cliente conectado");
    }

    /**
     * Obtener el panel principal del juego
     * @return Panel donde se renderiza el juego
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Manejar el cierre de la ventana de forma limpia
     */
    private void handleWindowClosing() {
        // TODO: Desconectar del servidor limpiamente
        // TODO: Guardar configuraciones si es necesario
        System.out.println("Cerrando aplicación...");
    }

    /**
     * Método main para testing de la ventana
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
            window.updateConnectionStatus("Modo de prueba - Sin conexión");
        });
    }
}