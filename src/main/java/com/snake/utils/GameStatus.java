package main.java.com.snake.utils;

public enum GameStatus {
    WAITING_FOR_PLAYERS("Esperando jugadores"),
    READY("Listo para iniciar"),
    RUNNING("En ejecución"),
    PAUSED("Pausado"),
    STOPPED("Detenido"),
    FINISHED("Terminado");
    
    private final String description;
    
    GameStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
