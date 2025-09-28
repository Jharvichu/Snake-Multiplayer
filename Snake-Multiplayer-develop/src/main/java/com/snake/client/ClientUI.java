package main.java.com.snake.client;

/** =====================
 * INTERFAZ MÍNIMA DE UI
 * =====================
 * La implementará la clase GameWindow de la interfaz gráfica.
 */
public interface ClientUI {
    void onConnected(int playerId);
    void onDisconnected(String reason);
    void onGameState(String rawStateJson);
    void onPlayerJoined(int playerId);
    void onGameOver(String winnerIdStr);
}
