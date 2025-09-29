package main.java.com.snake.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Manejo centralizado de entrada de teclado para el juego
 * @author Ariana
 */
public class InputHandler implements KeyListener {

    // Cliente del juego para enviar comandos
    private main.java.com.snake.client.GameClient gameClient;

    // Estado de teclas para evitar spam
    private boolean[] keysPressed = new boolean[256];

    public InputHandler() {
        // Constructor básico
    }

    /**
     * Configurar el cliente del juego para envío de comandos
     * @param gameClient Cliente que maneja la comunicación
     */
    public void setGameClient(main.java.com.snake.client.GameClient gameClient) {
        this.gameClient = gameClient;
        System.out.println("InputHandler: Cliente configurado");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Evitar procesamiento repetido de la misma tecla
        if (keysPressed[keyCode]) {
            return;
        }
        keysPressed[keyCode] = true;

        // Procesar solo teclas válidas
        if (isValidKey(keyCode)) {
            String direction = getDirectionFromKey(keyCode);
            if (direction != null) {
                handleMovement(direction);
            }
        }

        // Teclas especiales para testing/debugging
        handleSpecialKeys(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed[keyCode] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No necesario para este juego
    }

    /**
     * Validar si una tecla es permitida en el juego
     * @param keyCode Código de la tecla presionada
     * @return true si la tecla es válida
     */
    public boolean isValidKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_A:
            case KeyEvent.VK_S:
            case KeyEvent.VK_D:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Convertir código de tecla a dirección
     * @param keyCode Código de la tecla
     * @return Dirección como string ("UP", "DOWN", "LEFT", "RIGHT")
     */
    private String getDirectionFromKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                return "UP";
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                return "DOWN";
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                return "LEFT";
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                return "RIGHT";
            default:
                return null;
        }
    }

    /**
     * Procesar movimiento y enviarlo al servidor
     * @param direction Dirección del movimiento
     */
    private void handleMovement(String direction) {
        System.out.println("InputHandler: Movimiento detectado - " + direction);

        if (gameClient != null) {
            gameClient.sendMovement(direction);
        }

        logMovement(direction);
    }

    /**
     * Manejar teclas especiales para debugging
     * @param keyCode Código de la tecla especial
     */
    private void handleSpecialKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE:
                System.out.println("InputHandler: ESC presionado - Salir del juego");
                break;
            case KeyEvent.VK_P:
                System.out.println("InputHandler: P presionado - Pausar/Reanudar");
                break;
            case KeyEvent.VK_R:
                System.out.println("InputHandler: R presionado - Reiniciar juego");
                break;
            case KeyEvent.VK_F1:
                System.out.println("InputHandler: F1 presionado - Mostrar ayuda");
                break;
            default:
                // Otras teclas no manejadas
                break;
        }
    }

    /**
     * Registro de movimientos para debugging
     * @param direction Dirección del movimiento
     */
    private void logMovement(String direction) {
        long timestamp = System.currentTimeMillis();
        System.out.printf("[%d] Comando enviado: %s%n", timestamp, direction);
    }

    /**
     * Verificar si hay teclas presionadas actualmente
     * @return true si alguna tecla de movimiento está presionada
     */
    public boolean isAnyMovementKeyPressed() {
        return keysPressed[KeyEvent.VK_W] ||
                keysPressed[KeyEvent.VK_A] ||
                keysPressed[KeyEvent.VK_S] ||
                keysPressed[KeyEvent.VK_D] ||
                keysPressed[KeyEvent.VK_UP] ||
                keysPressed[KeyEvent.VK_DOWN] ||
                keysPressed[KeyEvent.VK_LEFT] ||
                keysPressed[KeyEvent.VK_RIGHT];
    }

    /**
     * Reiniciar estado de todas las teclas
     */
    public void resetKeyStates() {
        for (int i = 0; i < keysPressed.length; i++) {
            keysPressed[i] = false;
        }
        System.out.println("InputHandler: Estado de teclas reiniciado");
    }
}