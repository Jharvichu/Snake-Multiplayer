package main.java.com.snake.game.levels;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de niveles
 */
public class LevelManager {
    private List<Level> levels;
    private int currentLevelId;

    public LevelManager() {
        this.levels = new ArrayList<>();
        this.currentLevelId = 1;
        initializeLevels();
    }

    // Inicializar los 3 niveles del juego
    private void initializeLevels() {
        // NIVEL 1: Principiante
        Level level1 = new Level(1, 600, 600, 200); // 200ms entre movimientos
        
        // NIVEL 2: Intermedi
        Level level2 = new Level(2, 700, 700, 150); // 150ms entre movimientos
        // Agregar obstáculos en forma de cruz
        level2.addObstacle(350, 300);
        level2.addObstacle(350, 320);
        level2.addObstacle(350, 340);
        level2.addObstacle(350, 360);
        level2.addObstacle(350, 380);
        level2.addObstacle(350, 400);
        
        level2.addObstacle(300, 350);
        level2.addObstacle(320, 350);
        level2.addObstacle(340, 350);
        level2.addObstacle(360, 350);
        level2.addObstacle(380, 350);
        level2.addObstacle(400, 350);
        
        // NIVEL 3: Avanzado
        Level level3 = new Level(3, 800, 800, 100); // 100ms entre movimientos
        // Crear un laberinto simple
        for (int i = 100; i < 700; i += 40) {
            level3.addObstacle(200, i);
            level3.addObstacle(600, i);
        }
        for (int i = 200; i < 600; i += 40) {
            level3.addObstacle(i, 200);
            level3.addObstacle(i, 600);
        }
        
        levels.add(level1);
        levels.add(level2);
        levels.add(level3);
    }

    // Obtener nivel actual
    public Level getCurrentLevel() {
        return levels.get(currentLevelId - 1);
    }

    // Cambiar al siguiente nivel
    public boolean nextLevel() {
        if (currentLevelId < levels.size()) {
            currentLevelId++;
            return true;
        }
        return false;
    }

    // Cambiar a un nivel específico
    public boolean setLevel(int levelNumber) {
        if (levelNumber >= 1 && levelNumber <= levels.size()) {
            currentLevelId = levelNumber;
            return true;
        }
        return false;
    }

    // Resetear al primer nivel
    public void resetToFirstLevel() {
        currentLevelId = 1;
    }

    // Getters
    public int getCurrentLevelId() { return currentLevelId; }
    public int getTotalLevels() { return levels.size(); }
    public boolean hasNextLevel() { return currentLevelId < levels.size(); }
    
    // Verificar si una posición tiene obstáculo en el nivel actual
    public boolean hasObstacleAt(int x, int y) {
        return getCurrentLevel().hasObstacleAt(x, y);
    }
}
