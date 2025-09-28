public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("client")) {
            // Ejecutar cliente
            main.java.com.snake.client.ClientMain.main(new String[]{
                "127.0.0.1", "8080"
            });
        } else {
            // Ejecutar servidor por defecto
            main.java.com.snake.server.ServerMain.main(new String[]{
                "8080", "4"
            });
        }
    }
}