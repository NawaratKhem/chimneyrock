import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private int mana;
    private Player player;
    private ArrayList<Boss> bosses;
    private Boss currentBoss;

    public GameState() {
        this.mana = 24; // Initial mana
        this.player = new Player(100, "Player", 1);
        this.bosses = new ArrayList<>();
        initializeBosses(1); // Example: Start with 1 boss
        this.currentBoss = bosses.get(0); // Select the first boss by default
    }

    public void initializeBosses(int quantity) {
        bosses.clear();      
        String[] bossNames = {"Fire Boss", "Water Boss", "Ice Boss", "Earth Boss", "Metal Boss", "Alkaline Boss"};
        Random random = new Random();

        for (int i = 0; i < quantity; i++) {
            int bossHp = random.nextInt((20 - 5) + 1) + 5;
            Boss boss = new Boss(bossHp * 1000, bossNames[i]);
            bosses.add(boss);
        }
    }

    public void resetGameState() {
    player.resetPlayer();
    player.nextLevel();
    mana = 24 + (player.getCurrentLevel() * 10);
    initializeBosses(player.getCurrentLevel());
    currentBoss = bosses.isEmpty() ? null : bosses.get(0);  // Add null check
    
    // Signal that game state has been reset and UI needs updating
    notifyGameStateReset();
}

// Add a listener pattern to notify when game state changes
private List<GameStateListener> listeners = new ArrayList<>();

public interface GameStateListener {
    void onGameStateReset();
}

public void addGameStateListener(GameStateListener listener) {
    listeners.add(listener);
}

private void notifyGameStateReset() {
    for (GameStateListener listener : listeners) {
        listener.onGameStateReset();
    }
}

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Boss> getBosses() {
        return bosses;
    }

    public Boss getCurrentBoss() {
        return currentBoss;
    }

    public void setCurrentBoss(Boss boss) {
        this.currentBoss = boss;
    }

    public int getMana() {
        return mana;
    }

    public void spendMana(int amount) {
        this.mana -= amount;
    }

    public void addMana(int amount) {
        this.mana += amount;
    }

    public boolean hasMana(int cost) {
        return mana >= cost;
    }

    public boolean isGameOver() {
        return player.getHp() <= 0;
    }

    public boolean isGameWon() {
        return bosses.isEmpty();
    }
}
