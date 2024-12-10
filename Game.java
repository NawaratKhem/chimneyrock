import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Game extends JFrame {

    // game states
    int mana = 15;
    int manaCost;
    int dmgDeal;

    SpellsSelector ss;
    SpellTableModel spellTableModel;
    boolean casted = false;

    JLabel lblExpectedDMG;
    JLabel lblMana;
    //JButton btnAuto;
    JButton btnCastSpells;

    private ArrayList<Boss> bosses = new ArrayList<>();
    private Boss currentBoss;
    private Player player;

    public Game(SpellTableModel spellTableModel, Player player) {
        // construct and set title
        super("Chimneyrock");

        // this.setPreferredSize(new Dimension(900, 600));
        this.setResizable(false);

        this.player = player;
        this.spellTableModel = spellTableModel;
        NewGame();

        // default closing behavior
        setDefaultCloseOperation(Game.EXIT_ON_CLOSE);

        // pack to smallest size
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void NewGame() {
        // initialize game states
        manaCost = 0;
        dmgDeal = 0;

        // draw UI
        drawUI();

        currentBoss = bosses.get(0);
        currentBoss.selectBoss();
    }

    public SpellTableModel spells() {
        return spellTableModel;
    }

    public void drawUI() {
        // Main layout
        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        // North - Player Info Panel
        c.add(player.getPlayerPanel(), BorderLayout.NORTH);

        JPanel outerPanel = getBossesPanel();

        // Add the outer panel to the center
        c.add(outerPanel, BorderLayout.CENTER);

        // South - Mana, Expected DMG, Cast Button
        JPanel southPanel = getSothJPanel();

        c.add(southPanel, BorderLayout.SOUTH);

        // East - Spell Selector
        JPanel eastPanel = getSpellJPanel();

        c.add(eastPanel, BorderLayout.EAST);

        // Table Model Listener to update mana and damage
        spellTableModel.addTableModelListener(_ -> addTableModelListener());

        // Add action listeners for buttons
        //btnAuto.addActionListener(_ -> );
        btnCastSpells.addActionListener(_ -> castSpells());
    }

    private void castSpells() {

        if(manaCost <= 0){
            JOptionPane.showMessageDialog(this, "Please select a spell first", "Hey!",JOptionPane.INFORMATION_MESSAGE);
        }

        if (mana <= 0) {
            JOptionPane.showMessageDialog(this, "You have run out of mana! Game over.", "Game Over",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0); // End the game
        }

        if (manaCost > mana) {
            JOptionPane.showMessageDialog(this, "Lack of mana, rechoose spells", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mana -= manaCost;
        currentBoss.updateHP(dmgDeal);

        lblMana.setText("Mana: " + manaCost + " / " + mana);
        lblExpectedDMG.setText("Expected DMG: " + dmgDeal);

        if (currentBoss.getHp() > 0) {
            currentBoss.animateHurt();
            player.changeTurn(false);

            // Create timers for attack animations
            Timer timer = new Timer(2000, e -> {
                currentBoss.animateAttack();
                player.updateHP(27); // Example damage dealt by boss
                ((Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();

            Timer timer2 = new Timer(2500, e -> {
                player.changeTurn(true);
                ((Timer) e.getSource()).stop();
            });
            timer2.setRepeats(false);
            timer2.start();

            return;
        }

        currentBoss.animateDead();
        bosses.remove(currentBoss);
        ValideGame();
    }

    private void addTableModelListener() {
        int accumulatedCost = 0;
        int accumulatedDMG = 0;

        for (int i = 0; i < spellTableModel.getRowCount(); i++) {
            if (spellTableModel.isSelected(i)) {
                accumulatedCost += spellTableModel.getSpell(i).cost();
                accumulatedDMG += spellTableModel.getSpell(i).dmgDeal();
            }
        }

        manaCost = accumulatedCost;
        dmgDeal = accumulatedDMG;

        lblMana.setText("Mana: " + manaCost + " / " + mana);
        lblExpectedDMG.setText("Expected DMG: " + dmgDeal);
    }

    private JPanel getBossesPanel() {
        // Center - Boss Info
        JPanel bossWrapper = new JPanel();
        bossWrapper.setLayout(new GridLayout(2, 3, 10, 10)); // Adjusted to 2x3 grid with gaps
        bossWrapper.setBackground(Color.LIGHT_GRAY);

        String[] bossesNames = new String[] { "Fire Boss", "Water Boss", "Ice Boss", "Earth Boss", "Metal Boss",
                "Alkaline Boss" };

        Random random = new Random();
        int min = 5;
        int max = 20;

        for (int i = 0; i < bossesNames.length; i++) {

            // Generate a random number between 5 and 20 (inclusive)
            int bossHp = random.nextInt((max - min) + 1) + min;

            // Create and add bosses to the wrapper
            Boss boss = new Boss((bossHp * 1000), bossesNames[i]);
            bosses.add(boss);
            bossWrapper.add(boss.getBossPanel());
        }

        // Initial selection of the first boss
        selectBoss(bosses.get(0));

        // Add mouse listeners for boss selection
        for (Boss boss : bosses) {
            boss.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectBoss(boss);
                }
            });
        }

        // Wrap bossWrapper with padding
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        outerPanel.add(bossWrapper, BorderLayout.CENTER);

        return outerPanel;
    }

    private JPanel getSpellJPanel() {
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add the spell table
        JTable spellTable = new JTable(spellTableModel);
        spellTable.setAutoCreateColumnsFromModel(true);
        spellTable.setAutoCreateRowSorter(true);
        eastPanel.add(new JScrollPane(spellTable), BorderLayout.NORTH);

        // Add the auto button
        // btnAuto = new JButton("<<< Auto <<<");
        // eastPanel.add(btnAuto, BorderLayout.SOUTH);

        // Create the panel for radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JRadioButton maxDamageButton = new JRadioButton("MaxDamage");
        JRadioButton minManaButton = new JRadioButton("MinMana");
        JRadioButton allInButton = new JRadioButton("All-in");
        JRadioButton clear = new JRadioButton("Clear selection");

        // Group the radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(maxDamageButton);
        buttonGroup.add(minManaButton);
        buttonGroup.add(allInButton);
        buttonGroup.add(clear);

        // Add the buttons to the panel
        radioPanel.add(maxDamageButton);
        radioPanel.add(minManaButton);
        radioPanel.add(allInButton);
        radioPanel.add(clear);

        // Add ActionListener for radio buttons
        ActionListener radioListener = _ -> {
            if (maxDamageButton.isSelected()) {
                ss = new MaximizeDmgSelector();
            } else if (minManaButton.isSelected()) {
                ss = new MinimizeManaSelector();
            } else if (allInButton.isSelected()) {
                ss = new GreedySelector();
            }else if (clear.isSelected()) {
                spellTableModel.clearSelection();
            }
            ss.optimize(spellTableModel, mana, currentBoss.getBossMaxHp());
        };

        maxDamageButton.addActionListener(radioListener);
        minManaButton.addActionListener(radioListener);
        allInButton.addActionListener(radioListener);
        clear.addActionListener(radioListener);

        // Add the radio button panel to the east panel
        eastPanel.add(radioPanel, BorderLayout.SOUTH);

        return eastPanel;
    }

    private JPanel getSothJPanel() {
        // South - Mana, Expected DMG, Cast Button
        JPanel southPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getParent().getWidth(), 100); // Full width, fixed height
            }
        };
        southPanel.setLayout(new GridBagLayout());
        southPanel.setBackground(Color.LIGHT_GRAY);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14); // Custom font
        Color labelColor = Color.WHITE; // Custom text color

        // Create components
        lblMana = new JLabel("Mana: " + manaCost + " / " + mana);
        lblExpectedDMG = new JLabel("Expected DMG: " + dmgDeal);
        btnCastSpells = new JButton("Cast !!");

        // Customize components
        lblMana.setFont(labelFont);
        lblMana.setForeground(labelColor);

        lblExpectedDMG.setFont(labelFont);
        lblExpectedDMG.setForeground(labelColor);

        btnCastSpells.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCastSpells.setForeground(Color.BLACK);
        btnCastSpells.setBackground(new Color(60, 120, 180)); // Custom button color

        // Use GridBagLayout for alignment and spacing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Spacing around components

        gbc.gridx = 0;
        gbc.gridy = 0;
        southPanel.add(lblMana, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        southPanel.add(lblExpectedDMG, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        southPanel.add(btnCastSpells, gbc);
        return southPanel;
    }

    // Method to update player health
    public void damagePlayer(int damage) {
        player.updateHP(damage);
    }

    private void selectBoss(Boss boss) {

        spellTableModel.clearSelection();

        // Deselect all bosses
        for (Boss b : bosses) {
            b.setSelected(false);
        }

        // Select the clicked boss
        boss.setSelected(true);

        // Trigger revalidation and repaint for the whole panel
        boss.getBossPanel().revalidate();
        boss.getBossPanel().repaint();

        currentBoss = boss;
    }

    private void ValideGame() {
        if (bosses.isEmpty()) {
            if (player.getHp() > 0) {
                JOptionPane.showMessageDialog(this, "Congratulations! You've defeated all bosses!", "Victory",
                        JOptionPane.INFORMATION_MESSAGE);
                NewGame();
                player.nextLevel();
                mana += 10;
            } else {
                JOptionPane.showMessageDialog(this, "Game Over! You were defeated.", "Game Over",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0); // End the game
            }
        }
    }
}