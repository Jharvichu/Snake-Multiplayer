package main.java.com.snake.ui;

import java.awt.*;

/**
 * Constantes visuales para el juego Snake Multijugador
 * @author Ariana
 */
public class UIConstants {

    // Dimensiones del tablero de juego - MÁS GRANDE
    public static final int BOARD_WIDTH = 50;   // Era 40
    public static final int BOARD_HEIGHT = 35;  // Era 30
    public static final int CELL_SIZE = 18;     // Era 15

    // Dimensiones de la ventana - AJUSTADAS
    public static final int WINDOW_WIDTH = 920;  // 50 × 18 + 20 padding
    public static final int WINDOW_HEIGHT = 710; // 35 × 18 + 80 score + 50 padding
    public static final int GAME_PANEL_HEIGHT = 630; // 35 × 18
    public static final int SCORE_PANEL_HEIGHT = 80; // Igual

    // Colores base
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final Color GRID_COLOR = new Color(30, 30, 30);
    public static final Color TEXT_COLOR = Color.WHITE;

    // Colores para jugadores (máximo 8 jugadores)
    public static final Color[] PLAYER_COLORS = {
            new Color(255, 100, 100), // Rojo claro - Jugador 1
            new Color(100, 255, 100), // Verde claro - Jugador 2
            new Color(100, 100, 255), // Azul claro - Jugador 3
            new Color(255, 255, 100), // Amarillo - Jugador 4
            new Color(255, 100, 255), // Magenta - Jugador 5
            new Color(100, 255, 255), // Cyan - Jugador 6
            new Color(255, 200, 100), // Naranja - Jugador 7
            new Color(200, 100, 255)  // Violeta - Jugador 8
    };

    // Colores para frutas según valor
    public static final Color FRUIT_1_COLOR = Color.GREEN;      // Fruta valor 1
    public static final Color FRUIT_3_COLOR = Color.YELLOW;     // Fruta valor 3
    public static final Color FRUIT_5_COLOR = Color.ORANGE;     // Fruta valor 5
    public static final Color FRUIT_7_COLOR = Color.RED;        // Fruta valor 7

    // Fuentes
    public static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font FRUIT_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);

    // Configuración de ventana
    public static final String WINDOW_TITLE = "Snake Multijugador";
    public static final boolean WINDOW_RESIZABLE = false;

    // Métodos utilitarios
    public static Color getPlayerColor(int playerId) {
        if (playerId >= 0 && playerId < PLAYER_COLORS.length) {
            return PLAYER_COLORS[playerId];
        }
        return Color.WHITE; // Color por defecto
    }

    public static Color getFruitColor(int fruitValue) {
        switch (fruitValue) {
            case 1: return FRUIT_1_COLOR;
            case 3: return FRUIT_3_COLOR;
            case 5: return FRUIT_5_COLOR;
            case 7: return FRUIT_7_COLOR;
            default: return Color.WHITE;
        }
    }

    // Conversión de coordenadas del juego a píxeles
    public static int gameToPixelX(int gameX) {
        return gameX * CELL_SIZE;
    }

    public static int gameToPixelY(int gameY) {
        return gameY * CELL_SIZE;
    }
}