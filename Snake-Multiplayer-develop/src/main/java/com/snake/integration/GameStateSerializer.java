package main.java.com.snake.integration;

import main.java.com.snake.game.entities.GameState;
import main.java.com.snake.game.entities.Player;
import main.java.com.snake.game.entities.Snake;
import main.java.com.snake.game.entities.Fruit;

import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 * Serializador simple para GameState → JSON
 * Sin librerías externas, solo StringBuilder
 */
public class GameStateSerializer {

    /**
     * Convertir GameState a JSON string
     */
    public String toJson(GameState gameState) {
        if (gameState == null) {
            return "{}";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");

        // Información básica del juego
        appendField(json, "gameStatus", gameState.getGameStatus().toString(), true);
        appendField(json, "gameDuration", gameState.getGameDuration(), false);
        appendField(json, "totalFruitsEaten", gameState.getTotalFruitsEaten(), false);

        // Jugadores
        json.append(",\"players\":");
        appendPlayersJson(json, gameState.getPlayersInfo());

        // Frutas activas
        json.append(",\"fruits\":");
        appendFruitsJson(json, gameState.getActiveFruits());

        // Ranking
        json.append(",\"ranking\":");
        appendRankingJson(json, gameState.getPlayerRanking(), gameState.getPlayersInfo());

        json.append("}");
        return json.toString();
    }

    /**
     * Serializar jugadores
     */
    private void appendPlayersJson(StringBuilder json, Map<Integer, Player> players) {
        json.append("[");

        boolean first = true;
        for (Player player : players.values()) {
            if (!first) json.append(",");
            first = false;

            json.append("{");
            appendField(json, "id", player.getPlayerId(), false);
            appendField(json, "name", player.getPlayerName(), true);
            appendField(json, "score", player.getScore(), false);
            appendField(json, "alive", player.getAlive(), false);

            // Serpiente
            if (player.getSnake() != null) {
                json.append(",\"snake\":");
                appendSnakeJson(json, player.getSnake());
            }

            json.append("}");
        }

        json.append("]");
    }

    /**
     * Serializar una serpiente
     */
    private void appendSnakeJson(StringBuilder json, Snake snake) {
        json.append("{");
        appendField(json, "alive", snake.getAlive(), false);
        appendField(json, "direction", snake.getCurrentDirection().toString(), true);

        // Cuerpo de la serpiente
        json.append(",\"body\":");
        appendPointsJson(json, snake.getBody());

        json.append("}");
    }

    /**
     * Serializar frutas
     */
    private void appendFruitsJson(StringBuilder json, List<Fruit> fruits) {
        json.append("[");

        boolean first = true;
        for (Fruit fruit : fruits) {
            if (!first) json.append(",");
            first = false;

            json.append("{");
            appendField(json, "x", fruit.getPosition().x, false);
            appendField(json, "y", fruit.getPosition().y, false);
            appendField(json, "value", fruit.getGrowthValue(), false);
            json.append("}");
        }

        json.append("]");
    }

    /**
     * Serializar ranking
     */
    private void appendRankingJson(StringBuilder json, List<Integer> ranking, Map<Integer, Player> players) {
        json.append("[");

        boolean first = true;
        for (int i = 0; i < ranking.size(); i++) {
            Integer playerId = ranking.get(i);
            Player player = players.get(playerId);

            if (player == null) continue;

            if (!first) json.append(",");
            first = false;

            json.append("{");
            appendField(json, "position", i + 1, false);
            appendField(json, "playerId", playerId, false);
            appendField(json, "name", player.getPlayerName(), true);
            appendField(json, "score", player.getScore(), false);
            json.append("}");
        }

        json.append("]");
    }

    /**
     * Serializar lista de puntos (para cuerpo de serpiente)
     */
    private void appendPointsJson(StringBuilder json, List<Point> points) {
        json.append("[");

        boolean first = true;
        for (Point point : points) {
            if (!first) json.append(",");
            first = false;

            json.append("{");
            appendField(json, "x", point.x, false);
            appendField(json, "y", point.y, false);
            json.append("}");
        }

        json.append("]");
    }

    /**
     * Agregar campo al JSON
     */
    private void appendField(StringBuilder json, String key, Object value, boolean isString) {
        json.append("\"").append(key).append("\":");

        if (isString) {
            json.append("\"").append(escapeString(value.toString())).append("\"");
        } else {
            json.append(value);
        }
    }

    /**
     * Escapar caracteres especiales en strings
     */
    private String escapeString(String str) {
        if (str == null) return "";

        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}