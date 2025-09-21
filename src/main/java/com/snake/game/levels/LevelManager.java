package main.java.com.snake.game.levels;

import main.java.com.snake.utils.ConfigLoader;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private final List<Level> levels;
    private int currentLevelId;
    private Level currentLevel;

    public LevelManager() {
        this.levels = new ArrayList<>();
        this.currentLevelId = ConfigLoader.getDefaultLevel();
        initializeLevels();
        loadLevel(currentLevelId);
    }

    // Inicializar niveles por dificultad
    private void initializeLevels() {
        // NIVEL 1: Sin obstáculos, velocidad lenta
        Level level1 = new Level(1, new int[]{500, 500},
                                new String[]{"ninguno"}, 4, 1, 100, 0.7);
        
        // NIVEL 2: Con obstáculos básicos, velocidad media
        Level level2 = new Level(2, new int[]{600, 600},
                                new String[]{"cuadrado"}, 4, 2, 150, 0.5);
        
        // Agregar obstáculos en el centro para el nivel 2
        level2.addObstacle(250, 250);
        level2.addObstacle(250, 270);
        level2.addObstacle(270, 250);
        level2.addObstacle(270, 270);
        level2.addObstacle(350, 350);
        level2.addObstacle(350, 370);
        level2.addObstacle(370, 350);
        level2.addObstacle(370, 370);
        
        // NIVEL 3: Avanzado - Muchos obstáculos, velocidad alta
        Level level3 = new Level(3, new int[]{700, 700},
                                new String[]{"cuadrado", "rectangulo"}, 6, 3, 200, 0.3);
        
        // Agregar obstáculos complejos para el nivel 3
        // Crear un patrón de cruz
        for (int i = 320; i <= 380; i += 20) {
            level3.addObstacle(i, 300); // línea horizontal superior
            level3.addObstacle(i, 400); // línea horizontal inferior
        }
        for (int i = 300; i <= 400; i += 20) {
            level3.addObstacle(300, i); // línea vertical izquierda
            level3.addObstacle(400, i); // línea vertical derecha
        }
        
        // Obstáculos en las esquinas
        level3.addObstacle(100, 100);
        level3.addObstacle(120, 100);
        level3.addObstacle(100, 120);
        
        level3.addObstacle(580, 100);
        level3.addObstacle(600, 100);
        level3.addObstacle(600, 120);
        
        levels.add(level1);
        levels.add(level2);
        levels.add(level3);
    }

    // Cargar configuración de un nivel
    public void loadLevel(int levelNumber) {
        if (levelNumber >= 1 && levelNumber <= levels.size()) {
            this.currentLevelId = levelNumber;
            this.currentLevel = levels.get(levelNumber - 1);
            System.out.println("Cargado: " + currentLevel.toString());
        } else {
            System.out.println("Nivel no válido: " + levelNumber);
        }
    }

    // Obtener nivel actual
    public Level getCurrentLevel() {
        return currentLevel;
    }

    // Ver si cambiar de nivel
    public boolean checkLevel(int playerScore) {

        // Cambiar de nivel
        if (currentLevelId == 1 && playerScore >= ConfigLoader.getLevelProgressionScore(2)) {

            loadLevel(2);

            return true;
        } else if (currentLevelId == 2 && playerScore >= ConfigLoader.getLevelProgressionScore(3)) {
            loadLevel(3);
            return true;
        }
        return false;
    }

    // Obtener configuración
    public Level getLevelConfiguration(int level) {
        if (level >= 1 && level <= levels.size()) {
            return levels.get(level - 1);
        }
        return null;
    }

    // Lista de niveles
    public List<Level> getAvailableLevels() {
        return new ArrayList<>(levels);
    }

    // Obtener información de todos los niveles
    public String[] getLevelInfo() {
        return new String[levels.size()];
    }

    // Reiniciar al primer nivel
    public void resetToFirstLevel() {
        loadLevel(1);
    }

    // Obtener ID del nivel actual
    public int getCurrentLevelId() {
        return currentLevelId;
    }

    // Verificar si hay siguiente nivel
    public boolean hasNextLevel() {
        return currentLevelId < levels.size();
    }

    // Verificar si una posición tiene un obstáculo
    public boolean hasObstacleAt(int x, int y) {
        return currentLevel != null && currentLevel.hasObstacleAt(x, y);
    }
}
