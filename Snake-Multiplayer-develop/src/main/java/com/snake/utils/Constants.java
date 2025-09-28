package main.java.com.snake.utils;

public class Constants {

    // === CONFIGURACIÓN DEL TABLERO ===
    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 600;
    public static final int CELL_SIZE = 20;
    public static final int GRID_WIDTH = 40;  // BOARD_WIDTH / CELL_SIZE = 40
    public static final int GRID_HEIGHT = 30; // BOARD_HEIGHT / CELL_SIZE = 30

    // === CONFIGURACIÓN DEL JUEGO ===
    public static final int GAME_SPEED = 100; // ms entre actualizaciones
    public static final int MAX_PLAYERS = 4;
    public static final int MIN_PLAYERS = 1;
    public static final int INITIAL_SNAKE_LENGTH = 3;
    public static final int POINTS_PER_FRUIT = 10;

    // === CONFIGURACIÓN DE RED ===
    public static final int DEFAULT_PORT = 8080;
    public static final String DEFAULT_HOST = "localhost";
    public static final int CONNECTION_TIMEOUT = 5000; // ms
    public static final int MAX_RECONNECT_ATTEMPTS = 3;

    // === CONFIGURACIÓN DE FRUTAS ===
    public static final int MAX_FRUITS_ON_BOARD = 5;
    public static final int FRUIT_SPAWN_INTERVAL = 3000; // ms

    // === CONFIGURACIÓN DE NIVELES ===
    public static final int LEVEL_1_SPEED = 150;
    public static final int LEVEL_2_SPEED = 120;
    public static final int LEVEL_3_SPEED = 100;
    public static final int LEVEL_4_SPEED = 80;

    // === CONFIGURACIÓN DE UI ===
    public static final String GAME_TITLE = "Snake Multiplayer";
    public static final int WINDOW_WIDTH = BOARD_WIDTH + 200; // Espacio para info
    public static final int WINDOW_HEIGHT = BOARD_HEIGHT + 100;

    // === COLORES PREDETERMINADOS PARA JUGADORES ===
    public static final int[] PLAYER_COLORS = {
            0x00FF00, // Verde
            0xFF0000, // Rojo
            0x0000FF, // Azul
            0xFFFF00  // Amarillo
    };

    // === CONFIGURACIÓN DE EVENTOS ===
    public static final String EVENT_PLAYER_JOIN = "PLAYER_JOIN";
    public static final String EVENT_PLAYER_LEAVE = "PLAYER_LEAVE";
    public static final String EVENT_GAME_START = "GAME_START";
    public static final String EVENT_GAME_END = "GAME_END";
    public static final String EVENT_PLAYER_MOVE = "PLAYER_MOVE";
    public static final String EVENT_FRUIT_EATEN = "FRUIT_EATEN";
    public static final String EVENT_COLLISION = "COLLISION";

    // Prevenir instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }
}