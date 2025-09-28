// ========== ACTUALIZACIÓN DE LevelManager.java ==========
package main.java.com.snake.game.levels;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

/**
 * Gestor mejorado de niveles con 4 niveles integrados en la UI
 */
public class LevelManager {
    private List<Level> levels;
    private int currentLevelId;
    private LevelObserver observer;

    public interface LevelObserver {
        void onLevelChanged(Level newLevel);
        void onLevelCompleted(Level completedLevel);
        void onAllLevelsCompleted();
    }

    public LevelManager() {
        this.levels = new ArrayList<>();
        this.currentLevelId = 1;
        initializeFourLevels();
    }

    /**
     * Inicializar 4 niveles progresivos
     */
    private void initializeFourLevels() {
        // NIVEL 1: Principiante - Sin obstáculos
        Level level1 = new Level(1, "Básico", 50, 35, 200);
        level1.setDescription("Nivel básico sin obstáculos");
        level1.setRequiredScore(50);
        level1.setMaxTime(120000); // 2 minutos

        // NIVEL 2: Intermedio - Obstáculos simples
        Level level2 = new Level(2, "Intermedio", 50, 35, 150);
        level2.setDescription("Obstáculos en cruz central");
        level2.setRequiredScore(100);
        level2.setMaxTime(180000); // 3 minutos

        // Obstáculos en forma de cruz
        addCrossObstacles(level2, 25, 17); // Centro del tablero

        // NIVEL 3: Avanzado - Laberinto
        Level level3 = new Level(3, "Avanzado", 50, 35, 120);
        level3.setDescription("Laberinto con pasillos");
        level3.setRequiredScore(150);
        level3.setMaxTime(240000); // 4 minutos

        // Crear laberinto
        addMazeObstacles(level3);

        // NIVEL 4: Experto - Laberinto complejo + movimiento
        Level level4 = new Level(4, "Experto", 50, 35, 100);
        level4.setDescription("Laberinto complejo con obstáculos móviles");
        level4.setRequiredScore(200);
        level4.setMaxTime(300000); // 5 minutos

        // Laberinto complejo
        addComplexMazeObstacles(level4);

        levels.add(level1);
        levels.add(level2);
        levels.add(level3);
        levels.add(level4);
    }

    /**
     * Agregar obstáculos en cruz
     */
    private void addCrossObstacles(Level level, int centerX, int centerY) {
        // Línea horizontal
        for (int x = centerX - 8; x <= centerX + 8; x++) {
            if (x != centerX) { // Dejar espacio en el centro
                level.addObstacle(x, centerY);
            }
        }

        // Línea vertical
        for (int y = centerY - 6; y <= centerY + 6; y++) {
            if (y != centerY) { // Dejar espacio en el centro
                level.addObstacle(centerX, y);
            }
        }
    }

    /**
     * Crear laberinto simple
     */
    private void addMazeObstacles(Level level) {
        // Paredes exteriores del laberinto
        for (int x = 5; x < 45; x++) {
            level.addObstacle(x, 8);  // Pared superior
            level.addObstacle(x, 26); // Pared inferior
        }

        for (int y = 8; y <= 26; y++) {
            level.addObstacle(5, y);  // Pared izquierda
            level.addObstacle(44, y); // Pared derecha
        }

        // Paredes internas
        for (int x = 15; x < 25; x++) {
            level.addObstacle(x, 15);
        }

        for (int x = 30; x < 40; x++) {
            level.addObstacle(x, 20);
        }

        // Columnas
        for (int y = 12; y < 16; y++) {
            level.addObstacle(20, y);
            level.addObstacle(35, y);
        }
    }

    /**
     * Crear laberinto complejo
     */
    private void addComplexMazeObstacles(Level level) {
        // Laberinto más complejo con múltiples caminos

        // Bordes principales
        for (int x = 3; x < 47; x++) {
            if (x % 5 != 0) { // Crear aberturas
                level.addObstacle(x, 6);
                level.addObstacle(x, 28);
            }
        }

        // Pasillos internos
        for (int section = 0; section < 4; section++) {
            int startX = 8 + section * 10;
            for (int x = startX; x < startX + 6; x++) {
                level.addObstacle(x, 12);
                level.addObstacle(x, 22);
            }
        }

        // Obstáculos en zigzag
        for (int i = 0; i < 20; i++) {
            int x = 10 + i * 2;
            int y = (i % 2 == 0) ? 16 : 18;
            if (x < 45) {
                level.addObstacle(x, y);
            }
        }
    }

    // Métodos de control de nivel
    public Level getCurrentLevel() {
        return levels.get(currentLevelId - 1);
    }

    public boolean nextLevel() {
        if (currentLevelId < levels.size()) {
            currentLevelId++;
            if (observer != null) {
                observer.onLevelChanged(getCurrentLevel());
            }
            return true;
        } else {
            if (observer != null) {
                observer.onAllLevelsCompleted();
            }
            return false;
        }
    }

    public boolean setLevel(int levelNumber) {
        if (levelNumber >= 1 && levelNumber <= levels.size()) {
            Level oldLevel = getCurrentLevel();
            currentLevelId = levelNumber;
            if (observer != null) {
                observer.onLevelChanged(getCurrentLevel());
            }
            return true;
        }
        return false;
    }

    public void resetToFirstLevel() {
        currentLevelId = 1;
        if (observer != null) {
            observer.onLevelChanged(getCurrentLevel());
        }
    }

    /**
     * Verificar si se puede avanzar al siguiente nivel
     */
    public boolean canAdvanceToNextLevel(int playerScore, long gameTime) {
        Level current = getCurrentLevel();
        return playerScore >= current.getRequiredScore() &&
                gameTime <= current.getMaxTime();
    }

    /**
     * Verificar si el nivel actual está completo
     */
    public boolean isCurrentLevelCompleted(int playerScore) {
        return playerScore >= getCurrentLevel().getRequiredScore();
    }

    // Getters y setters
    public int getCurrentLevelId() { return currentLevelId; }
    public int getTotalLevels() { return levels.size(); }
    public boolean hasNextLevel() { return currentLevelId < levels.size(); }
    public void setObserver(LevelObserver observer) { this.observer = observer; }

    public List<Level> getAllLevels() { return new ArrayList<>(levels); }

    public String getCurrentLevelInfo() {
        Level current = getCurrentLevel();
        return String.format("Nivel %d: %s - Puntos requeridos: %d",
                current.getId(), current.getName(), current.getRequiredScore());
    }
}