package main.java.com.snake.game.levels;

import main.java.com.snake.utils.ConfigLoader;

/**
 * Clase de ejemplo que demuestra el uso del sistema de niveles y puntuación
 * Esta clase simula un juego básico con múltiples jugadores
 */
public class GameExample {
    
    public static void main(String[] args) {
        System.out.println("=== DEMO: Sistema de Niveles y Puntuación ===\n");
        
        // Mostrar configuración cargada
        System.out.println("1. Configuración del juego:");
        
        // Inicializar sistemas
        LevelManager levelManager = new LevelManager();
        ScoreManager scoreManager = new ScoreManager();
        
        // Registrar jugadores
        System.out.println("\n2. Registrando jugadores:");
        scoreManager.registerPlayer(1, "Alice");
        scoreManager.registerPlayer(2, "Bob");
        scoreManager.registerPlayer(3, "Charlie");
        
        // Mostrar nivel actual
        System.out.println("\n3. Nivel actual:");
        Level currentLevel = levelManager.getCurrentLevel();
        System.out.println(currentLevel.toString());
        System.out.println("Dimensiones: " + currentLevel.getDimensions()[0] + "x" + currentLevel.getDimensions()[1]);
        System.out.println("Velocidad: " + currentLevel.getGameSpeed());
        System.out.println("Obstáculos: " + currentLevel.getObstaclePositions().size());
        
        // Simular puntuación
        System.out.println("\n4. Simulando juego:");
        
        // Alice come 5 frutas
        for (int i = 0; i < 5; i++) {
            scoreManager.addFruitEaten(1, ConfigLoader.getFruitPoints("normal"));
            try { Thread.sleep(100); } catch (InterruptedException _) {}
        }
        
        // Bob come 8 frutas
        for (int i = 0; i < 8; i++) {
            scoreManager.addFruitEaten(2, ConfigLoader.getFruitPoints("normal"));
            try { Thread.sleep(100); } catch (InterruptedException _) {}
        }
        
        // Charlie come 12 frutas
        for (int i = 0; i < 12; i++) {
            scoreManager.addFruitEaten(3, ConfigLoader.getFruitPoints("normal"));
            try { Thread.sleep(100); } catch (InterruptedException _) {}
        }
        
        // Mostrar estadísticas
        System.out.println("\n5. Estadísticas actuales:");
        for (int playerId : scoreManager.getActivePlayers()) {
            System.out.println(scoreManager.getPlayerStats(playerId));
        }
        
        // Verificar progresión de nivel
        System.out.println("\n6. Verificando progresión de nivel:");
        int charlieScore = scoreManager.getPlayerScore(3);
        boolean levelChanged = levelManager.checkLevel(charlieScore);
        
        if (levelChanged) {
            System.out.println("¡Nivel cambiado!");
            Level newLevel = levelManager.getCurrentLevel();
            System.out.println("Nuevo nivel: " + newLevel.toString());
            System.out.println("Nueva velocidad: " + newLevel.getGameSpeed());
        } else {
            System.out.println("Aún en el mismo nivel");
        }
        
        // Aplicar bonus points
        System.out.println("\n7. Aplicando puntos bonus:");
        for (int playerId : scoreManager.getActivePlayers()) {
            scoreManager.applyBonusPoints(playerId);
        }
        
        // Mostrar leaderboard final
        System.out.println("\n8. Leaderboard final:");
        scoreManager.printLeaderboard();
        
        // Mostrar top 3
        System.out.println("9. Top 3 jugadores:");
        var top3 = scoreManager.getTopPlayers(3);
        for (int i = 0; i < top3.size(); i++) {
            System.out.println((i + 1) + ". " + top3.get(i));
        }

    }
}