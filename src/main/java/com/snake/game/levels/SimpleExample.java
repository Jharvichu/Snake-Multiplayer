package main.java.com.snake.game.levels;

/**
 * DEMO: simulando el sistema de niveles y puntuación
 */
public class SimpleExample {
    
    public static void main(String[] args) {
        
        // Crear managers
        LevelManager levelManager = new LevelManager();
        ScoreManager scoreManager = new ScoreManager();
        
        // Agregar jugadores
        scoreManager.addPlayer(1, "Angie");
        scoreManager.addPlayer(2, "Pablo");
        scoreManager.addPlayer(3, "Mario");
        
        // Mostrar nivel actual
        Level currentLevel = levelManager.getCurrentLevel();
        System.out.println("Nivel actual: " + currentLevel);
        System.out.println("Dimensiones: " + currentLevel.getWidth() + "x" + currentLevel.getHeight());
        System.out.println("Velocidad: " + currentLevel.getGameSpeed() + "ms");
        System.out.println("Obstáculos: " + currentLevel.getObstacles().size());
        
        // Simular puntuación
        scoreManager.updateScore(1, 50);  // Alice: 50 puntos
        scoreManager.updateScore(2, 80);  // Bob: 80 puntos  
        scoreManager.updateScore(3, 120); // Charlie: 120 puntos
        
        // Mostrar ranking
        scoreManager.printRanking();
        
        // Cambiar de nivel
        if (levelManager.nextLevel()) {
            System.out.println("¡Avanzando al siguiente nivel!");
            Level newLevel = levelManager.getCurrentLevel();
            System.out.println("Nuevo nivel: " + newLevel);
            System.out.println("Nueva velocidad: " + newLevel.getGameSpeed() + "ms");
            System.out.println("Nuevos obstáculos: " + newLevel.getObstacles().size());
        }
        
        // Más puntuación
        scoreManager.updateScore(1, 30);
        scoreManager.updateScore(2, 20);
        scoreManager.updateScore(3, 10);
        
        // Ranking final
        System.out.println("Ranking final:");
        scoreManager.printRanking();
        
        System.out.println("Jugador ganador: " + scoreManager.getPlayerName(scoreManager.getTopPlayer()));
    }
}