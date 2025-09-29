package main.java.com.snake.server;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceMonitor {
    private final AtomicLong messagesSent = new AtomicLong(0);
    private final AtomicLong messagesReceived = new AtomicLong(0);
    private final AtomicLong bytesTransferred = new AtomicLong(0);
    private final AtomicInteger peakPlayers = new AtomicInteger(0);

    private final long serverStartTime;

    public PerformanceMonitor() {
        this.serverStartTime = System.currentTimeMillis();
    }

    // Métodos para registrar métricas
    public void recordMessageSent(String message) {
        messagesSent.incrementAndGet();
        bytesTransferred.addAndGet(message.length());
    }

    public void recordMessageReceived(String message) {
        messagesReceived.incrementAndGet();
        bytesTransferred.addAndGet(message.length());
    }

    public void updatePeakPlayers(int currentPlayers) {
        peakPlayers.updateAndGet(current -> Math.max(current, currentPlayers));
    }

    // Generar reporte
    public String generatePerformanceReport() {
        long uptime = System.currentTimeMillis() - serverStartTime;
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();

        StringBuilder report = new StringBuilder();
        report.append("========== REPORTE DE RENDIMIENTO ==========\n");
        report.append(String.format("Tiempo funcionando: %s\n", formatUptime(uptime)));
        report.append(String.format("Mensajes enviados: %,d\n", messagesSent.get()));
        report.append(String.format("Mensajes recibidos: %,d\n", messagesReceived.get()));
        report.append(String.format("Bytes transferidos: %,d\n", bytesTransferred.get()));
        report.append(String.format("Pico de jugadores: %d\n", peakPlayers.get()));
        report.append(String.format("Memoria usada: %.2f MB\n", usedMemory / 1024.0 / 1024.0));
        report.append("==========================================");

        return report.toString();
    }

    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
}