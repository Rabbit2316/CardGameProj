package Entities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Player extends JComponent {
    public ArrayList<Card> cards = new ArrayList<>();
    public ArrayList<Card> hand = new ArrayList<>();
    public ArrayList<Card> discard = new ArrayList<>();
    public int maxHealth = 100;
    public int currentHealth = 100;

    private Rectangle hitbox = new Rectangle(10, 0, 24, 99);
    private Rectangle healthBar = new Rectangle(0, 0, 75, 10);
    private ArrayList<BufferedImage[]> animations = new ArrayList<>();

    public Player() {
        setSize(new Dimension(130, 200));
        //setBorder(BorderFactory.createLineBorder(Color.BLACK));
        for(int i = 0; i<10; i++) {
            cards.add(new Card());
        }
        animations.add(importSprites("/Resources/EvilWizard/idleMap.png", 10, 1, 37, 53));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Calculate the centered position for the sprite
        int centeredX = (getWidth() - 33) / 2; // 33 is sprite width
        int centeredY = 25; // Matches the previous hitbox position

        // Update the hitbox's position (optional, if needed for other logic)
        hitbox.setLocation(centeredX, centeredY);

        // Draw animation instead of the hitbox
        drawAni(g, centeredX, centeredY);

        // Draw the health bar
        int barX = (getWidth() - healthBar.width) / 2;
        healthBar.setLocation(barX, 0);
        g.setColor(Color.white);
        g.fillRect(barX, 0, healthBar.width, healthBar.height);

        // Calculate current health bar width
        int healthBarWidth = (int) ((double) currentHealth / maxHealth * healthBar.width);

        // Draw health bar (green for health)
        g.setColor(Color.green);
        g.fillRect(healthBar.x, healthBar.y, healthBarWidth, healthBar.height);
    }


    public void takeDamage(int damage) {
        if(currentHealth > maxHealth)
            currentHealth = maxHealth;

        currentHealth = currentHealth - damage;
        if(currentHealth <= 0) {
            System.out.println("youre dead");
        } else {
            System.out.println("current health: "+currentHealth);
        }

        revalidate();
        repaint();
    }

    public static BufferedImage loadImage(String path) {
        try (InputStream is = Enemy.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Error: Image not found at " + path);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace(); // Print error details for debugging
            return null;
        }
    }


    public static BufferedImage[] importSprites(String pathName, int cols, int rows, int spriteWidth, int spriteHeight) {
        BufferedImage image = loadImage(pathName);
        if (image == null) {
            return new BufferedImage[0]; // Return empty array if loading fails
        }

        BufferedImage[] sprites = new BufferedImage[cols * rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                sprites[y * cols + x] = image.getSubimage(x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight);
            }
        }
        return sprites;
    }

    int aniCounter = 0;
    int aniSpeed = 5;
    int aniIndex = 0;
    public void animate() {
        //System.out.println(aniIndex);//Debugging for making sure the index is functioning correctly
        aniCounter++;//Increments by one every frame.
        if(aniCounter >= aniSpeed) {//If the number of frames I want per animation sprite has passed
            aniCounter = 0;//Reset the counter
            aniIndex++;//Increase the index by one to move to the next sprite
        }
        if(aniIndex >= this.animations.get(0).length) //If we reach the end of the sprite map
            aniIndex = 0;//Reset the index
    }
    public void drawAni(Graphics g, int x, int y) {
        animate();
        g.drawImage(animations.get(0)[aniIndex], 30, 22, 74, 106, null);

    }




}
