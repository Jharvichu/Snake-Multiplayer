package main.java.com.snake.client;



/** =====================
 * PROTOCOLO DE MENSAJES
 * =====================
 * Formato de ejemplo (línea por mensaje, delimitado por \n):
 * - CONNECT:<playerId>
 * - MOVE:<playerId>:<UP|DOWN|LEFT|RIGHT>
 * - PING
 * - STATE:<jsonCompacto>
 * - JOINED:<playerId>
 * - GAMEOVER:<winnerPlayerId>
 */
public class MessageProtocol {
    static final String SEP = ":";  // Separador para mensajes

    static String createConnectMessage(int playerId) {
        return "CONNECT" + SEP + playerId; // Mensaje conexion de jugador. Ejem: "CONNECT:12"
    }
    // Envia movimiento con direccion normalizada
    static String createMoveMessage(String direction, int playerId) {
        String dir = normalizeDirection(direction);
        return "MOVE" + SEP + playerId + SEP + dir; // MOVE:12:UP
    }

    // Sirve como heartbeat para saber si cliente y servidor siguen vivos
    static String createPing() {
        return "PING"; // latido opcional
    }


    /**
     * Normaliza entradas tipo "W", "A", "S", "D" o palabras completas.
     */
    static String normalizeDirection(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toUpperCase();
        return switch (s) {
            case "W", "UP" -> "UP";
            case "S", "DOWN" -> "DOWN";
            case "A", "LEFT" -> "LEFT";
            case "D", "RIGHT" -> "RIGHT";
            default -> s; // deja pasar por si la UI ya validó
        };
    }


    /**
     * Ejemplo mínimo de parseo de estado (a adaptar cuando definan GameState).
     * Aquí devolvemos un DTO con el payload crudo; el servidor puede mandar JSON.
     */
    static StateDTO parseGameStateMessage(String line) {
    // Espera algo como: STATE:{"tick":123,"players":[...]} -> payload a la derecha
        int idx = line.indexOf(SEP);
        if (idx < 0 || !line.startsWith("STATE")) return null;
        String payload = line.substring(idx + 1).trim();
        return new StateDTO(payload);
    }


    /** DTO de estado crudo (puedes reemplazar por GameState real cuando lo tengan). */
    static class StateDTO {
        public final String rawJson;
        StateDTO(String rawJson) { this.rawJson = rawJson; }
        @Override
        public String toString() { return rawJson; }
    }
}
