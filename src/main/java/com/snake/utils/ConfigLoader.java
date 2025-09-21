package main.java.com.snake.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties;
    private static final String CONFIG_FILE = "/config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigLoader.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                System.out.println("Configuración cargada exitosamente");
            } else {
                System.out.println("Archivo de configuración no encontrado: " + CONFIG_FILE);
                loadDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
            loadDefaultProperties();
        }
    }

    private static void loadDefaultProperties() {
        properties.setProperty("levels.count", "3");
        properties.setProperty("levels.default", "1");
        properties.setProperty("scoring.fruitBase", "10");
        properties.setProperty("fruit.normal.points", "10");
        properties.setProperty("level.progression.level2", "100");
        properties.setProperty("level.progression.level3", "250");
    }

    public static int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Error parseando propiedad " + key + ": " + e.getMessage());
            return defaultValue;
        }
    }

    public static int getDefaultLevel() {
        return getIntProperty("levels.default", 1);
    }

    public static int getFruitPoints(String fruitType) {
        String key = "fruit." + fruitType + ".points";
        return getIntProperty(key, 10);
    }

    public static int getLevelProgressionScore(int level) {
        String key = "level.progression.level" + level;
        return getIntProperty(key, 100);
    }
}