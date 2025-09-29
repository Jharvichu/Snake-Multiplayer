package main.java.com.snake.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Maneja la comunicación con un cliente individual
 * Ejecuta en su propio hilo para manejar múltiples clientes simultáneamente
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final int playerId;
    private final GameServer gameServer;

    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean isConnected = true;

    public ClientHandler(Socket clientSocket, int playerId, GameServer gameServer) {
        this.clientSocket = clientSocket;
        this.playerId = playerId;
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            System.err.println("Error manejando cliente " + playerId + ": " + e.getMessage());
        } finally {
            cleanupConnection();
        }
    }

    /**
     * Configurar streams de comunicación y comenzar a escuchar mensajes
     */
    public void handleClient() throws IOException {
        // Configurar streams de entrada y salida
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Enviar mensaje de bienvenida
        sendMessageToClient("WELCOME:" + playerId);

        // Comenzar a leer mensajes del cliente
        readClientMessages();
    }

    /**
     * Bucle principal para leer mensajes del cliente
     */
    public void readClientMessages() {
        while (isConnected && !clientSocket.isClosed()) {
            try {
                String message = in.readLine();

                if (message == null) {
                    // Cliente se desconectó
                    System.out.println("Cliente " + playerId + " se desconectó");
                    break;
                }

                processClientMessage(message.trim());

            } catch (SocketException e) {
                // Conexión cerrada por el cliente
                System.out.println("Cliente " + playerId + " cerró la conexión");
                break;
            } catch (IOException e) {
                System.err.println("Error leyendo mensaje del cliente " + playerId + ": " + e.getMessage());
                break;
            }
        }
    }

    /**
     * Procesar mensaje recibido del cliente
     */
    private void processClientMessage(String message) {
        if (message.isEmpty()) {
            return;
        }

        try {
            // Parsear el mensaje según el protocolo
            String[] parts = message.split(":", 2);
            if (parts.length < 1) {
                sendMessageToClient("ERROR:Formato de mensaje inválido");
                return;
            }

            String command = parts[0].toUpperCase();

            switch (command) {
                case "MOVE":
                    if (parts.length >= 2) {
                        String direction = parts[1].toUpperCase();
                        if (isValidDirection(direction)) {
                            // Usar el playerId de este handler
                            gameServer.processPlayerMove(playerId, direction);
                        } else {
                            sendMessageToClient("ERROR:Dirección inválida: " + direction);
                        }
                    } else {
                        sendMessageToClient("ERROR:Comando MOVE requiere dirección");
                    }
                    break;

                case "CONNECT":
                    if (parts.length >= 2) {
                        try {
                            int requestedPlayerId = Integer.parseInt(parts[1]);
                            // El playerId ya está asignado por el servidor
                            // Validar que el ID solicitado coincida con el asignado
                            if (requestedPlayerId == playerId) {
                                sendMessageToClient("CONNECTED:" + playerId);
                            } else {
                                sendMessageToClient("ERROR:Player ID no coincide");
                            }
                        } catch (NumberFormatException e) {
                            sendMessageToClient("ERROR:Player ID inválido");
                        }
                    } else {
                        // Cliente envió CONNECT sin ID, usar el asignado
                        sendMessageToClient("CONNECTED:" + playerId);
                    }
                    break;

                case "PING":
                    sendMessageToClient("PONG");
                    break;

                case "DISCONNECT":
                    System.out.println("Cliente " + playerId + " solicitó desconexión");
                    isConnected = false;
                    break;

                default:
                    sendMessageToClient("ERROR:Comando desconocido: " + command);
                    break;
            }

        } catch (Exception e) {
            System.err.println("Error procesando mensaje del cliente " + playerId + ": " + e.getMessage());
            sendMessageToClient("ERROR:Error interno del servidor");
        }
    }

    /**
     * Validar que la dirección es válida
     */
    private boolean isValidDirection(String direction) {
        return direction.equals("UP") || direction.equals("DOWN") ||
                direction.equals("LEFT") || direction.equals("RIGHT");
    }

    /**
     * Enviar mensaje al cliente
     */
    public void sendMessageToClient(String message) {
        if (out != null && !clientSocket.isClosed()) {
            try {
                out.println(message);
                if (out.checkError()) {
                    System.err.println("Error enviando mensaje a cliente " + playerId);
                    isConnected = false;
                }
            } catch (Exception e) {
                System.err.println("Error enviando mensaje a cliente " + playerId + ": " + e.getMessage());
                isConnected = false;
            }
        }
    }

    /**
     * Cerrar conexión y limpiar recursos
     */
    public void cleanupConnection() {
        isConnected = false;

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando BufferedReader: " + e.getMessage());
        }

        if (out != null) {
            out.close();
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando socket del cliente: " + e.getMessage());
        }

        // Notificar al servidor que este cliente se desconectó
        gameServer.removeDisconnectedClient(playerId);
    }

    /**
     * Verificar si el cliente está conectado
     */
    public boolean isConnected() {
        return isConnected && !clientSocket.isClosed();
    }

    public int getPlayerId() {
        return playerId;
    }
}