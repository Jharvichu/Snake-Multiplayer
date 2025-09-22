package main.java.com.snake.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** =====================
 * GESTOR DE CONEXIÓN TCP
 * =====================
 * - Abre y cierra el socket
 * - Expone métodos seguros para enviar y recibir líneas
 * - Incluye reconexión con backoff
 */
public class ConnectionManager implements Closeable {
    private final String ip;
    private final int port;
    private final int connectTimeoutMs;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ConnectionManager(String ip, int port){
        this(ip, port, 4000);
    }

    public ConnectionManager(String ip, int port, int connectTimeoutMs){
        this.ip = ip;
        this.port = port;
        this.connectTimeoutMs = connectTimeoutMs;
    }

    /** Establece la conexion y abre streams. */
    public synchronized void establishConnection() throws IOException{
        close();
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), connectTimeoutMs);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(0);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    /** Envío de una línea (thread-safe). */
    public synchronized void sendLine(String line) throws IOException {
        if (!isConnected()) throw new IOException("Socket no conectado");
        out.write(line);
        out.write('\n');
        out.flush();
    }

    /** Lectura bloqueante de una línea. Retorna null si se cerró limpio. */
    public String readLine() throws IOException {
        if (!isConnected()) throw new IOException("Socket no conectado");
        return in.readLine();
    }

    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /** Intenta reconectar con backoff exponencial simple. */
    public void attemptReconnection(Runnable onBeforeRetry) throws IOException, InterruptedException {
        int attempt = 0;
        IOException last = null;
        while (attempt < 6) { // ~6 intentos máx
            attempt++;
            try {
                if (onBeforeRetry != null) onBeforeRetry.run();
                establishConnection();
                return; // éxito
            } catch (IOException e) {
                last = e;
                long backoffMs = Math.min(1500L * (1L << (attempt - 1)), 12_000L);
                Thread.sleep(backoffMs);
            }
        }
        throw last == null ? new IOException("Fallo de reconexión desconocido") : last;
    }

    @Override
    public synchronized void close() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        in = null; out = null; socket = null;
    }
}