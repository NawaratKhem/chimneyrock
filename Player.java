import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Player extends JPanel {
    private int _playerHp;
    private int _playerMaxHp;
    private int _currentLevel;
    private String _playerName;

    private JLabel playerNameLabel;
    private JProgressBar pgbPlayerHP;
    //private JLabel levelLabel;
    private JButton restartButton;
    private JLabel turnLabel;

    public Player(int hp, String playerName, int startLevel) {
        this._playerHp = hp;
        this._playerMaxHp = hp;
        this._currentLevel = startLevel;
        this._playerName = playerName;
        initializeComponents();
        setPlayerName(playerName);

        this.setBackground(Color.WHITE);
    }

    private void initializeComponents() {
        // Use BorderLayout for the Player panel
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 50, 10, 50));
    
        // Left: Player Name and HP (Stacked Vertically)
        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS)); // Vertical stacking
        playerNameLabel = new JLabel("Hero");
        playerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        pgbPlayerHP = new JProgressBar(0, _playerMaxHp);
        pgbPlayerHP.setValue(_playerHp);
        pgbPlayerHP.setString(getHpText()); // Show currentHP / totalHP
        pgbPlayerHP.setStringPainted(true); // Enable the string display
        pgbPlayerHP.setPreferredSize(new Dimension(200, 20));
        pgbPlayerHP.setAlignmentX(Component.LEFT_ALIGNMENT); // Align to the left
        leftContainer.add(playerNameLabel);
        leftContainer.add(Box.createVerticalStrut(10)); // Add spacing
        leftContainer.add(pgbPlayerHP);
    
        // Right: Level and Restart Button (Stacked Vertically)
        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BoxLayout(rightContainer, BoxLayout.Y_AXIS));
        rightContainer.setBackground(Color.WHITE);
        //levelLabel = new JLabel("Level: " + _currentLevel);
        //levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        //levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align for better symmetry
        restartButton = new JButton("Restart");
        restartButton.addActionListener(_ -> resetPlayer());
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align for better symmetry
        //rightContainer.add(levelLabel);
        rightContainer.add(Box.createVerticalStrut(10)); // Add spacing
        rightContainer.add(restartButton);
    
        // Center: Turn Indicator Label
        turnLabel = new JLabel(getPlayerName() +"'s Turn"); // Default text
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align horizontally
        add(turnLabel, BorderLayout.CENTER); // Add turnLabel to the center of the panel
    
        // Add containers to the Player panel
        add(leftContainer, BorderLayout.WEST);  // Left side
        add(rightContainer, BorderLayout.EAST); // Right side
    }

    public void setPlayerName(String name) {
        playerNameLabel.setText(name);
    }
    
    public String getPlayerName() {
        return _playerName;
    }

    public void setLevel(int level) {
        _currentLevel = level;
        //levelLabel.setText("Level: " + _currentLevel);
    }

    public void updateHP(int damage) {
        // Create a Timer to delay the update by 1 second (1000 milliseconds)
        Timer timer = new Timer(1000, e -> {
            this._playerHp = Math.max(0, this._playerHp - damage);
            pgbPlayerHP.setValue(_playerHp);
            pgbPlayerHP.setString(getHpText()); // Update the text on the health bar
            ((Timer) e.getSource()).stop(); // Stop the timer after execution
        });
        timer.setRepeats(false); // Ensure the timer runs only once
        timer.start();
    }

    public void setTurnIndicator(String text) {
        turnLabel.setText(text); // Update the turn label dynamically
    }

    public void resetPlayer() {
        _playerHp = _playerMaxHp;
        pgbPlayerHP.setValue(_playerHp);
        pgbPlayerHP.setString(getHpText()); // Update the text on the health bar
    }

    public void nextLevel(){
        resetPlayer();
        _currentLevel++;
    }

    public int getCurrentLevel(){
        return _currentLevel;
    }

    public int getHp(){
        return _playerHp;
    }

    private String getHpText() {
        return _playerHp + " / " + _playerMaxHp; // Format for HP display
    }

    public void changeTurn(boolean isPlayerTurn) {
        SwingUtilities.invokeLater(() -> {
            if (isPlayerTurn) {
                turnLabel.setText("Player's Turn");
            } else {
                turnLabel.setText("Boss's Turn");
            }
        });
    }
    
    public JPanel getPlayerPanel() {
        return this;
    }
}
