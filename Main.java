import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                createGame();
            }
        });
    }

    private static void createGame() {

        Player player = new Player(1000, "Khim", 1);

        SpellTableModel table = new SpellTableModel();
        // Adding appropriate spells
        table.add(new Spell("Zoltraak", 6, 15000));
        table.add(new Spell("Balterie", 3, 7000));
        table.add(new Spell("Starlight Breaker", 5, 12000));
        table.add(new Spell("Inferno Blaze", 4, 8000));
        table.add(new Spell("Frost Nova", 2, 3000));
        table.add(new Spell("Arcane Surge", 3, 5000));
        table.add(new Spell("Thunder Strike", 4, 9000));
        table.add(new Spell("Shadow Veil", 2, 2500));
        table.add(new Spell("Light's Redemption", 5, 10000));
        table.add(new Spell("Earthquake Tremor", 3, 6000));

        new Game(table, player);
    }
}
