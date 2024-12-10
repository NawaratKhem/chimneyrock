import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Boss extends JPanel implements AutoCloseable {
    private final Point baseOffset = new Point(0, 0);
    private final Dimension spriteDimension = new Dimension(128, 64);
    private int currentFrame = 0;
    private Animation currentAnimation = Animation.JUMP;
    private boolean isSelected = false;

    // UI Components
    private JProgressBar pgbBossHP;
    private JLabel bossNameLabel;
    private JPanel bossPanel;

    // Health-related variables
    private int _Hp;
    private int _bossMaxHp;

    // Animation-related variables
    private Timer animationTimer;

    public Boss(int hp, String bossname) {
        this._Hp = hp;
        this._bossMaxHp = hp;

        initializeComponents();
        setBossName(bossname);
    }

    private void initializeComponents() {
        bossPanel = new JPanel();
        bossPanel.setLayout(new BoxLayout(bossPanel, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(300, 200));

        JPanel northC = new JPanel();
        northC.setLayout(new BoxLayout(northC, BoxLayout.Y_AXIS));

        pgbBossHP = new JProgressBar(0, _bossMaxHp) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 8));
                String percentText = getHp() + " / " + getBossMaxHp();
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(percentText);
                int textHeight = fm.getHeight() / 2;
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2;
                g.drawString(percentText, x, y);
            }
        };
        pgbBossHP.setPreferredSize(new Dimension(100, 18));
        pgbBossHP.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        pgbBossHP.setValue(_Hp);

        bossNameLabel = new JLabel("Boss");
        bossNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bossNameLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        bossNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel labelWrapper_healthBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelWrapper_healthBar.add(pgbBossHP);

        bossPanel.add(this);
        bossPanel.add(bossNameLabel);
        bossPanel.add(labelWrapper_healthBar);
        bossPanel.add(northC);
        bossNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pgbBossHP.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Highlight background if selected
        if (isSelected) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Set a very light gray background
            g2d.setColor(new Color(211, 211, 211, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw a white border for clearer selection
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            g2d.dispose();
        }

        // Draw the boss image or other components
        try {
            BufferedImage img = ImageIO.read(new File(currentAnimation.getPath()));
            Rectangle croppedArea = new Rectangle(baseOffset, spriteDimension);
            croppedArea.translate(spriteDimension.width * currentFrame, 0);
            croppedArea = validateCropArea(img, croppedArea);
            BufferedImage croppedImg = cropImage(img, croppedArea);

            Graphics2D g2d = (Graphics2D) g.create();

            // Dynamically adjust image dimensions to fit better
            int targetWidth = (int) (getWidth() * 1.0);
            int targetHeight = (int) (getHeight() * 1.0);

            // Maintain the aspect ratio of the image
            double aspectRatio = (double) croppedImg.getWidth() / croppedImg.getHeight();
            if (targetWidth / (double) targetHeight > aspectRatio) {
                targetWidth = (int) (targetHeight * aspectRatio);
            } else {
                targetHeight = (int) (targetWidth / aspectRatio);
            }

            // Center the image within the component
            int imageX = (getWidth() - targetWidth);
            int imageY = (getHeight() - targetHeight);

            // Draw the boss image with adjusted dimensions
            g2d.drawImage(croppedImg, imageX, imageY, targetWidth, targetHeight, this);

            g2d.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Rectangle validateCropArea(BufferedImage img, Rectangle rect) {
        int x = Math.max(0, rect.x);
        int y = Math.max(0, rect.y);
        int width = Math.min(rect.width, img.getWidth() - x);
        int height = Math.min(rect.height, img.getHeight() - y);
        return new Rectangle(x, y, width, height);
    }

    private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if(selected){
            this.animateIdeal();
        }else{
            stopAnimation();
        }
        repaint();
    }

    private void stopAnimation() {
        // Stop the animation timer if it exists
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        // Reset to first frame
        currentFrame = 0;
        
        // Repaint to show the first frame
        repaint();
    }

    public int getHp() {
        return _Hp;
    }

    public int getBossMaxHp() {
        return _bossMaxHp;
    }

    public void setBossName(String name) {
        bossNameLabel.setText(name);
    }

    public void updateHP(int damage) {
        System.out.println("Boss taking damage: " + damage);
        System.out.println("Current HP before: " + _Hp);
        
        this._Hp = Math.max(0, this._Hp - damage);
        pgbBossHP.setValue(_Hp);
        
        System.out.println("Current HP after: " + _Hp);
        
        // Force repaint to ensure visual update
        pgbBossHP.revalidate();
        pgbBossHP.repaint();
        
        // If boss panel exists, update it
        if (bossPanel != null) {
            bossPanel.revalidate();
            bossPanel.repaint();
        }
    }

    public JPanel getBossPanel() {
        return bossPanel;
    }

    public void selectBoss() {
        setAnimation(Animation.IDLE);
    }

    private void startAnimation(Animation animation) {
        // Stop any existing animation
        if (animationTimer != null) {
            animationTimer.stop();
        }

        currentAnimation = animation;
        currentFrame = 0;

        animationTimer = new Timer(100, e -> {
            // Increment frame
            currentFrame++;

            // Check if animation should stop
            if (currentFrame >= currentAnimation.getFrames()) {
                if (currentAnimation.isLoop()) {
                    currentFrame = 0; // Reset to loop
                } else {
                    currentFrame = currentAnimation.getFrames() - 1; // Stop at last frame
                    ((Timer) e.getSource()).stop();

                    // If it's the DEAD animation, remove related components
                    if (currentAnimation == Animation.DEAD) {
                        removeRelatedComponents();
                    }
                }
            }

            // Trigger repaint
            repaint();
        });
        animationTimer.start();
    }

    public void setAnimation(Animation anim) {
        startAnimation(anim);
    }

    public void animateIdeal() {
        startAnimation(Animation.IDLE);
    }

    public void animateDead() {
        startAnimation(Animation.DEAD);

        // Use Timer to wait for animation to complete before removal
        Timer removalTimer = new Timer(1200, e -> {
            removeRelatedComponents();
            ((Timer) e.getSource()).stop();
        });
        removalTimer.setRepeats(false);
        removalTimer.start();
    }

    public void animateAttack() {
        startAnimation(Animation.JUMP);

        // Use Timer to wait for animation to complete before removal
        Timer removalTimer = new Timer(800, e -> {
            animateIdeal();
            ((Timer) e.getSource()).stop();
        });
        removalTimer.setRepeats(false);
        removalTimer.start();
    }

    public void animateHurt() {
        startAnimation(Animation.HURT);

        // Use Timer to wait for animation to complete before removal
        Timer removalTimer = new Timer(600, e -> {
            animateIdeal();
            ((Timer) e.getSource()).stop();
        });
        removalTimer.setRepeats(false);
        removalTimer.start();
    }

    private void removeRelatedComponents() {
        if (bossPanel != null) {
            // Remove all direct children of the bossPanel
            bossPanel.removeAll();

            // Ensure the panel is empty
            while (bossPanel.getComponentCount() > 0) {
                bossPanel.remove(0);
            }

            // Revalidate and repaint the boss panel
            bossPanel.revalidate();
            bossPanel.repaint();
        }
    }

    @Override
    public void close() {
        // Stop animation timer
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }

        // Remove all UI components
        removeRelatedComponents();

        // Final cleanup
        if (bossPanel != null) {
            bossPanel.removeAll();
            bossPanel = null;
        }

        // Call parent panel cleanup
        removeAll();
    }

    public enum Animation {
        IDLE("assets/idle.png", 7),
        JUMP("assets/jump.png", 8),
        HURT("assets/hurt.png", 6),
        DEAD("assets/dead.png", 9, false);

        private final String path;
        private final int frames;
        private final boolean loop;

        Animation(String path, int frames) {
            this(path, frames, true);
        }

        Animation(String path, int frames, boolean loop) {
            this.path = path;
            this.frames = frames;
            this.loop = loop;
        }

        public String getPath() {
            return path;
        }

        public int getFrames() {
            return frames;
        }

        public boolean isLoop() {
            return loop;
        }
    }
}