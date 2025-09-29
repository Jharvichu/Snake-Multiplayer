package main.java.com.snake.server;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestiona el envío de mensajes a clientes conectados
 * Permite broadcasting a todos, a todos excepto uno, o a cliente específico
 */
public class ServerMessageBroadcaster {
    private final ConcurrentHashMap<Integer, ClientHandler> clients = new ConcurrentHashMap<>();

    /**
     * Agregar cliente al broadcaster
     */
    public void addClient(int playerId, ClientHandler handler) {
        clients.put(playerId, handler);
        System.out.println("Cliente agregado al broadcaster: Player " + playerId);
    }

    /**
     * Remover cliente del broadcaster
     */
    public void removeClient(int playerId) {
        ClientHandler removed = clients.remove(playerId);
        if (removed != null) {
            System.out.println("Cliente removido del broadcaster: Player " + playerId);
        }
    }

    /**
     * Enviar mensaje a todos los clientes conectados
     */
    public void broadcastToAll(String message) {
        if (clients.isEmpty()) {
            return;
        }

        System.out.println("Broadcasting a todos (" + clients.size() + " clientes): " + message);

        for (ClientHandler client : clients.values()) {
            if (client.isConnected()) {
                client.sendMessageToClient(message);
            }
        }
    }

    /**
     * Enviar mensaje a todos los clientes excepto uno específico
     */
    public void broadcastToOthers(String message, int excludePlayerId) {
        if (clients.isEmpty()) {
            return;
        }

        int recipientCount = clients.size() - (clients.containsKey(excludePlayerId) ? 1 : 0);
        System.out.println("Broadcasting a otros (" + recipientCount + " clientes): " + message);

        for (ClientHandler client : clients.values()) {
            if (client.getPlayerId() != excludePlayerId && client.isConnected()) {
                client.sendMessageToClient(message);
            }
        }
    }

    /**
     * Enviar mensaje a un cliente específico
     */
    public void sendToSpecificPlayer(String message, int playerId) {
        ClientHandler client = clients.get(playerId);
        if (client != null && client.isConnected()) {
            client.sendMessageToClient(message);
        } else {
            System.err.println("No se pudo enviar mensaje a Player " + playerId + ": cliente no encontrado o desconectado");
        }
    }

    /**
     * Obtener número de clientes conectados
     */
    public int getConnectedClientCount() {
        return clients.size();
    }

    /**
     * Verificar si un cliente específico está conectado
     */
    public boolean isClientConnected(int playerId) {
        ClientHandler client = clients.get(playerId);
        return client != null && client.isConnected();
    }

    /**
     * Limpiar clientes desconectados del broadcaster
     */
    public void cleanupDisconnectedClients() {
        clients.entrySet().removeIf(entry -> !entry.getValue().isConnected());
    }
}
