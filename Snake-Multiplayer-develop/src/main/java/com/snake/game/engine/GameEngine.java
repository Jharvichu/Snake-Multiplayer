package main.java.com.snake.game.engine;

import main.java.com.snake.game.entities.*;
import main.java.com.snake.utils.Constants;
import main.java.com.snake.utils.Direction;
import main.java.com.snake.utils.GameStatus;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEngine {
    private static GameEngine instance;
    private static final Object lock = new Object();
    
    // Estado del juego
    private GameState gameState;
    private Map<Integer, Player> players;
    private List<Fruit> fruits;
    private CollisionDetector collisionDetector;
    private FruitGenerator fruitGenerator;
    
    // Control del game loop
    private boolean isRunning;
    private boolean isPaused;
    private Thread gameThread;
    private long lastUpdateTime;
    private long lastFruitSpawnTime;
    
    // Observadores del juego
    private List<GameEngineObserver> observers;
    
    private GameEngine() {
        initialize();
    }
    
    // Singleton
    public static GameEngine getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new GameEngine();
                }
            }
        }
        return instance;
    }

    private void initialize() {
        this.gameState = new GameState();
        this.players = new HashMap<>();
        this.fruits = new CopyOnWriteArrayList<>();
        this.collisionDetector = new CollisionDetector();
        this.fruitGenerator = new FruitGenerator();
        this.observers = new CopyOnWriteArrayList<>();
        this.isRunning = false;
        this.isPaused = false;
        this.lastUpdateTime = System.currentTimeMillis();
        this.lastFruitSpawnTime = System.currentTimeMillis();
    }
    
    // Start
    public synchronized void startGame() {
        if (isRunning) return;
        
        if (players.size() < Constants.MIN_PLAYERS) {
            throw new IllegalStateException("Se necesitan al menos " + Constants.MIN_PLAYERS + " jugadores para iniciar");
        }
        
        isRunning = true;
        isPaused = false;
        gameState.setGameStatus(GameStatus.RUNNING);
        
        // Inicializar
        initializePlayerSnakes();
        generateInitialFruits();
        
        // Iniciar game loop
        startGameLoop();
        
        notifyObservers(Constants.EVENT_GAME_START);
    }
    
    // Pause
    public synchronized void togglePause() {
        isPaused = !isPaused;
        gameState.setGameStatus(isPaused ? GameStatus.PAUSED : GameStatus.RUNNING);
        notifyObservers(isPaused ? "GAME_PAUSED" : "GAME_RESUMED");
    }
    
    // Parar juego
    public synchronized void stopGame() {
        isRunning = false;
        isPaused = false;
        gameState.setGameStatus(GameStatus.STOPPED);
        
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }
        
        notifyObservers(Constants.EVENT_GAME_END);
    }
    
    public synchronized boolean addPlayer(Player player) {
        if (players.size() >= Constants.MAX_PLAYERS) {
            return false;
        }
        
        players.put(player.getPlayerId(), player);
        notifyObservers(Constants.EVENT_PLAYER_JOIN);
        return true;
    }
    
    public synchronized void removePlayer(int playerId) {
        players.remove(playerId);
        notifyObservers(Constants.EVENT_PLAYER_LEAVE);
        
        // Si quedan muy pocos jugadores, pausar el juego
        if (players.size() < Constants.MIN_PLAYERS && isRunning) {
            togglePause();
        }
    }
    
    // Recibe el input del jugador
    public void processPlayerInput(int playerId, Direction direction) {
        Player player = players.get(playerId);
        if (player != null && player.getSnake() != null) {
            player.getSnake().setDirection(direction);
        }
    }
    
    // Loop del Juego 

    private void startGameLoop() {
        gameThread = new Thread(this::gameLoop);
        gameThread.setName("GameEngine-Loop");
        gameThread.start();
    }
    
    private void gameLoop() {
        while (isRunning) {
            try {
                long currentTime = System.currentTimeMillis();
                if (!isPaused) {
                    if (currentTime - lastUpdateTime >= Constants.GAME_SPEED) {
                        update();
                        lastUpdateTime = currentTime;
                    }
                    // Generar fruta
                    if (currentTime - lastFruitSpawnTime >= Constants.FRUIT_SPAWN_INTERVAL) {
                        generateFruit();
                        lastFruitSpawnTime = currentTime;
                    }
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void update() {
        for (Player player : players.values()) {
            Snake snake = player.getSnake();
            if (snake != null && snake.getAlive()) {
                snake.move();
                checkCollisions(player);
            }
        }
        
        checkGameEnd();
        gameState.updateState(players.values(), fruits);
    }
    
    // Verifica todas las colisiones para un jugador
    private void checkCollisions(Player player) {
        Snake snake = player.getSnake();
        Point head = snake.getHead();
        
        if (collisionDetector.checkWallCollision(head)) {
            handlePlayerDeath(player);
            return;
        }
        if (collisionDetector.checkSelfCollision(snake)) {
            handlePlayerDeath(player);
            return;
        }
        for (Player otherPlayer : players.values()) {
            if (otherPlayer.getPlayerId() != player.getPlayerId()) {
                if (collisionDetector.checkSnakeCollision(snake, otherPlayer.getSnake())) {
                    handlePlayerDeath(player);
                    return;
                }
            }
        }
        
        Fruit eatenFruit = collisionDetector.checkFruitCollision(head, fruits);
        if (eatenFruit != null) {
            handleFruitEaten(player, eatenFruit);
        }
    }
    
    // Maneja la muerte de un jugador
    private void handlePlayerDeath(Player player) {
        player.getSnake().setAlive(false);
        notifyObservers(Constants.EVENT_COLLISION);
    }
    
    // Maneja cuando un jugador come una fruta
    private void handleFruitEaten(Player player, Fruit fruit) {
        player.getSnake().grown(fruit);
        
        int newScore = player.getScore() + (fruit.getGrowthValue() * Constants.POINTS_PER_FRUIT);
        player.setScore(newScore);
        
        fruits.remove(fruit);
        
        notifyObservers(Constants.EVENT_FRUIT_EATEN);
    }
    
    // Inicializa las serpientes de los jugadores en posiciones seguras
    private void initializePlayerSnakes() {
        int playerCount = players.size();
        int spacing = Constants.GRID_WIDTH / (playerCount + 1);
        
        System.out.println("DEBUG GameEngine: Inicializando " + playerCount + " serpientes");
        
        int index = 0;
        for (Player player : players.values()) {
            int startX = spacing * (index + 1);
            int startY = Constants.GRID_HEIGHT / 2;
            
            Color color = new Color(Constants.PLAYER_COLORS[index % Constants.PLAYER_COLORS.length]);
            Snake snake = new Snake(startX, startY, color);
            player.setSnake(snake);
            
            System.out.println("DEBUG GameEngine: Jugador " + player.getPlayerId() + " - serpiente creada en (" + startX + "," + startY + ") alive: " + snake.getAlive());
            
            index++;
        }
    }
    
    private void generateInitialFruits() {
        for (int i = 0; i < Constants.MAX_FRUITS_ON_BOARD / 2; i++) {
            generateFruit();
        }
    }
    
    private void generateFruit() {
        if (fruits.size() >= Constants.MAX_FRUITS_ON_BOARD) {
            return;
        }
        
        Set<Point> occupiedPositions = getOccupiedPositions();
        Set<Point> freePositions = getFreePositions(occupiedPositions);
        
        Fruit newFruit = fruitGenerator.generateRandomFruit(freePositions);
        if (newFruit != null) {
            fruits.add(newFruit);
        }
    }
    
    // Informacion del Mapa

    private Set<Point> getOccupiedPositions() {
        Set<Point> occupied = new HashSet<>();
        
        for (Player player : players.values()) {
            if (player.getSnake() != null) {
                occupied.addAll(player.getSnake().getBody());
            }
        }

        for (Fruit fruit : fruits) {
            occupied.add(fruit.getPosition());
        }
        
        return occupied;
    }
    
    private Set<Point> getFreePositions(Set<Point> occupiedPositions) {
        Set<Point> freePositions = new HashSet<>();
        
        for (int x = 0; x < Constants.GRID_WIDTH; x++) {
            for (int y = 0; y < Constants.GRID_HEIGHT; y++) {
                Point point = new Point(x, y);
                if (!occupiedPositions.contains(point)) {
                    freePositions.add(point);
                }
            }
        }
        
        return freePositions;
    }
    
    // Verifica cuando el juego tiene que parar
    private void checkGameEnd() {
        long alivePlayers = players.values().stream()
            .filter(p -> p.getSnake() != null && p.getSnake().getAlive())
            .count();
            
        System.out.println("DEBUG GameEngine: checkGameEnd - total players: " + players.size() + ", alive players: " + alivePlayers + ", MIN_PLAYERS: " + Constants.MIN_PLAYERS);
        
        if (alivePlayers < Constants.MIN_PLAYERS) {
            System.out.println("DEBUG GameEngine: Deteniendo juego - jugadores vivos insuficientes");
            stopGame();
        }
    }
    
    // MÉTODOS DE OBSERVADOR
    
    public void addObserver(GameEngineObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GameEngineObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers(String event) {
        for (GameEngineObserver observer : observers) {
            observer.onGameEvent(event, gameState);
        }
    }
    
    // GETTERS
    
    public GameState getGameState() { return gameState; }
    public Map<Integer, Player> getPlayers() { return new HashMap<>(players); }
    public List<Fruit> getFruits() { return new ArrayList<>(fruits); }
    public boolean isRunning() { return isRunning; }
    public boolean isPaused() { return isPaused; }
    
    /**
     * Interface para observadores del GameEngine
     */
    public interface GameEngineObserver {
        void onGameEvent(String event, GameState gameState);
    }
}
