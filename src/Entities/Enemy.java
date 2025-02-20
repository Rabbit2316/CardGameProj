package Entities;

import MainPackage.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public abstract class Enemy extends JComponent {

   // Rpg stats for each Enemy
    protected int maxHealth;
    public int currentHealth;
    protected int attackPower;  //attack power (base attack stat
    protected int defense;      // base defense stat
    protected int agility;      //base agility
    protected int speed;        // base speed


    protected ArrayList<BufferedImage[]> animations = new ArrayList<>();
    protected int aniIndex = 0;
    protected int aniSpeed = 10;
    protected int aniCounter = 0;

    // Health bar & hitbox
    protected Rectangle hitbox = new Rectangle(10, 0, 24, 99);
    protected Rectangle healthBar = new Rectangle(0, 0, 75, 10);

    // Track if this Enemy is targeted
    protected boolean isTargeted = false;




    // main constructor with all of the stats and specific size dimensions
    public Enemy(int maxHealth, int attackPower, int defense, int agility, int speed) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attackPower = attackPower;
        this.defense = defense;
        this.agility = agility;
        this.speed = speed;
        setSize(new Dimension(100, 150));
    }

   // default enemy with stats
    public Enemy() {
        this(30, 5, 2, 1, 1);
    }

  // setting the target
    public void setTargeted(boolean targeted) {
        this.isTargeted = targeted;
        repaint();
    }

    public boolean isTargeted() {
        return this.isTargeted;
    }

   // loading the image from input path
    public static BufferedImage loadImage(String path) {
        try (InputStream is = Enemy.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Error: Image not found at " + path);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage[] importSprites(String pathName, int cols, int rows,
                                                int spriteWidth, int spriteHeight) {
        BufferedImage image = loadImage(pathName);
        if (image == null) {
            return new BufferedImage[0];
        }
        BufferedImage[] sprites = new BufferedImage[cols * rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                sprites[y * cols + x] = image.getSubimage(
                        x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight
                );
            }
        }
        return sprites;
    }

    // animation speed and counter
    public void animate() {
        aniCounter++;
        if (aniCounter >= aniSpeed) {
            aniCounter = 0;
            aniIndex++;

            if (!animations.isEmpty() && aniIndex >= animations.get(0).length) {
                aniIndex = 0;
            }
        }
    }

  //custome paint
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // animate and draw sprite sheet if available
        animate();
        if (!animations.isEmpty() && animations.get(0).length > 0) {
            g.drawImage(animations.get(0)[aniIndex], 10, 10, 75, 75, null);
        }

        // draw the hitbox (centered) and health bar
        g.setColor(isTargeted ? Color.GREEN : Color.RED);

        int centeredX = (getWidth() - hitbox.width) / 2;
        int centeredY = (getHeight() - hitbox.height) / 2;
        hitbox.setLocation(centeredX, centeredY);
        g.fillRect(hitbox.x, hitbox.y - 3, hitbox.width, hitbox.height);

        // health bar across the top
        int barX = (getWidth() - healthBar.width) / 2;
        healthBar.setLocation(barX, 0);
        g.fillRect(healthBar.x, healthBar.y, healthBar.width, healthBar.height);

        // fill portion of the bar for current health
        int healthBarWidth = (int)((double) currentHealth / maxHealth * healthBar.width);
        g.setColor(Color.GREEN);
        g.fillRect(healthBar.x, healthBar.y, healthBarWidth, healthBar.height);
    }

   // take damage mechanic
    public void takeDamage(int damage) {
        // reducin gdamage by defense
        int reducedDamage = Math.max(damage - defense, 1);
        currentHealth = Math.max(currentHealth - reducedDamage, 0);

        if (currentHealth <= 0) {
            System.out.println(getEnemyType() + " has been defeated!");
        } else {
            System.out.println(getEnemyType() + " Health: " + currentHealth + "/" + maxHealth);
        }

        // Refresh UI
        revalidate();
        repaint();
    }

    //example attack method from fireball
    public void attack(Runnable onComplete) {

        Animation ani = new Fireball(700, 20, 0, 0);
        Random rand = new Random();
        int dmg = rand.nextInt(10); // random between 0-9


        Game.player.takeDamage(dmg);


        AttackPlane.addAniToQue(ani);

        // start of the animation (attack)
        AttackPlane.animations.get(0).startAnimation();
        Game.gui.gameScreen.northPanel.attackPlane.playAnimation(() -> {
            AttackPlane.animations.get(0).stopAnimation();
            onComplete.run();
        });
    }

    //Get the enemy type from sub-classes
    public abstract String getEnemyType();
}
