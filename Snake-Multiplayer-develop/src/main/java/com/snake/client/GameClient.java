package main.java.com.snake.client;


import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** =====================
 * CLIENTE DEL JUEGO
 * =====================
 * - Usa ConnectionManager
 * - Hilo de escucha para mensajes del servidor
 * - Expone sendMovement / disconnect
 */
public class GameClient implements Closeable {
    private final int playerId;
    private final ConnectionManager conn;
    private final ExecutorService ioPool = Executors.newSingleThreadExecutor(r->{
        Thread t = new Thread(r, "client-listener");
        t.setDaemon(true);
        return t;
    });

    private volatile boolean listening = false;
    private ClientUI ui;

    public GameClient(int playerId, String ip, int port) {
        this.playerId = playerId;
        this.conn = new ConnectionManager(ip, port);
    }

    public void setUi(ClientUI ui){this.ui=ui;}

    /** Conecta y manda CONNECT:<playerId> */
    public void connectToServer() throws IOException {
        conn.establishConnection();
        conn.sendLine(MessageProtocol.createConnectMessage(playerId));
        if (ui != null) ui.onConnected(playerId);
    }

    /** Lanza un hilo que lee líneas continuamente y las procesa. */
    public void startListening() {
        if (listening) return;
        listening = true;
        ioPool.submit(() -> {
            while (listening) {
                try {
                    String line = conn.readLine();
                    if (line == null) {
// El servidor cerró la conexión limpiamente
                        if (ui != null) ui.onDisconnected("Servidor cerró la conexión");
                        break;
                    }
                    processServerMessage(line);
                } catch (IOException e) {
                    if (listening) {
                        if (ui != null) ui.onDisconnected("Error de E/S: " + e.getMessage());
// Intento de reconexión: opcional, descomenta si quieres reconectar aquí
// tryReconnect();
                        break; // salimos del bucle; el caller decide
                    }
                }
            }
        });
    }

    /** Envío de movimiento MOVE:<playerId>:<DIR> */
    public void sendMovement(String direction) {
        String msg = MessageProtocol.createMoveMessage(direction, playerId);
        try {
            conn.sendLine(msg);
        } catch (IOException e) {
            if (ui != null) ui.onDisconnected("No se pudo enviar movimiento: " + e.getMessage());
        }
    }

    /** Procesa cada línea recibida del servidor. */
    public void processServerMessage(String message) {
        if (message == null || message.isBlank()) return;
        if (message.startsWith("STATE")) {
            MessageProtocol.StateDTO dto = MessageProtocol.parseGameStateMessage(message);
            if (dto != null && ui != null) ui.onGameState(dto.rawJson);
            return;
        }
        if (message.startsWith("JOINED")) {
            String[] parts = message.split(MessageProtocol.SEP, 2);
            if (parts.length == 2 && ui != null) {
                try { ui.onPlayerJoined(Integer.parseInt(parts[1].trim())); }
                catch (NumberFormatException ignored) {}
            }
            return;
        }
        if (message.startsWith("GAMEOVER")) {
            String[] parts = message.split(MessageProtocol.SEP, 2);
            if (ui != null) ui.onGameOver(parts.length > 1 ? parts[1].trim() : "");
            return;
        }
        if (message.equals("PING")) {
            // opcional: responder PONG
            return;
        }
        // Otros tipos...
    }

    /** Ejemplo de reconexión manual (puedes llamarlo desde la UI). */
    public boolean tryReconnect() {
        try {
            conn.attemptReconnection(() -> {
                // hook antes de reintento
            });
            conn.sendLine(MessageProtocol.createConnectMessage(playerId));
            if (ui != null) ui.onConnected(playerId);
            return true;
        } catch (Exception e) {
            if (ui != null) ui.onDisconnected("Fallo al reconectar: " + e.getMessage());
            return false;
        }
    }

    public boolean isConnected() { return conn.isConnected(); }


    public int getPlayerId() { return playerId; }


    @Override
    public void close() {
        listening = false;
        try { ioPool.shutdownNow(); ioPool.awaitTermination(500, TimeUnit.MILLISECONDS); } catch (InterruptedException ignored) {}
        conn.close();
    }


    public void disconnect() { close(); }
}