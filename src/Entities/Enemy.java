package Entities;

import MainPackage.Config;
import MainPackage.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.imageio.ImageIO;

public abstract class Enemy extends JComponent {

   //Rpg stats for each Enemy
    protected int maxHealth;
    public int currentHealth;
    protected int attackPower;  //attack power (base attack stat
    protected int defense;      // base defense stat
    protected int agility;      //base agility
    protected int speed;        // base speed

    //States to determine enemy animation
    protected enum State {IDLE, WALKING, ATTACKING, DYING};
    protected State state = State.IDLE;

    //Variables needed for loading of animations
    protected int idleColCount, idleRowCount;
    protected int walkColCount, walkRowCount;
    protected int attackColCount, attackRowCount;
    protected int deathColCount, deathRowCount;

    //ArrayList holding arrays of enemy animations.
    protected ArrayList<BufferedImage[]> animations = new ArrayList<>();

    //Variables needed for running the animation logic
    //Idle Animation
    protected int aniIndex = 0;
    public int aniSpeed = 10;
    protected int aniCounter = 0;

    //Walking Animation
    protected int walkIndex = 0;
    protected int walkCounter = 0;
    protected int walkSpeed = 6;

    //Attacking Animation
    protected int attackIndex = 0;
    protected int attackCounter = 0;
    protected int attackSpeed = 6;

    //Death Animation
    protected int deathIndex = 0;
    protected int deathCounter = 0;
    protected int deathSpeed = 7;

    //Rectangles for the hitbox and healthbar
    protected Rectangle hitbox;
    protected Rectangle healthBar = new Rectangle(0, 0, 75, 10);

    //Variables needed for moving the sprite during the walk animation.
    protected boolean startXset = false;
    public int currentX, currentY;
    protected int movingSpeed =  9;

    //Unimplemented tracking for if the enemy is targeted by a card
    protected boolean isTargeted = false;


    public int x, y;
    public boolean dead = false;
    protected String name;
    InPlaceAnimation attackAnimation;

    // Loot Related properties
    protected Map<String, Object> lootTable = new HashMap<String, Object>();


    protected int spriteWidth, spriteHeight;

    // main constructor with all of the stats and specific size dimensions
    public Enemy(String name, int maxHealth, int attackPower, int defense, int agility, int speed, int w, int h, int sW, int sH, int idleColCount, int idleRolCount, int walkColCount, int walkRowCount, int attackColCount, int attackRowCount, int deathColCount, int deathRowCount, InPlaceAnimation attackAnimation) throws IOException {
        currentX = 33333300;
        this.attackAnimation = attackAnimation;
        this.aniSpeed = 5;
        //Setting enemy date
        this.name = name;

        //Setting enemy stats
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attackPower = attackPower;
        this.defense = defense;
        this.agility = agility;
        this.speed = speed;

        //Populating enemy animation data
        populateEnemyAnimationData(idleColCount, idleRolCount, walkColCount, walkRowCount, attackColCount, attackRowCount, deathColCount, deathRowCount);

        //Loading and initializing Animation Sprite Maps.
        //Setting up the file path
        this.spriteWidth = sW;
        this.spriteHeight = sH;
        String basePath = "../Resources/"+name+"/"+name;

        //Loading Maps
        addMapsToAnimations(basePath);

        //Setting the size of this enemy
        setSize(new Dimension(w, h));

        //Initializing hitbox
        hitbox = new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());

        //Populating loot table
        populateLootTable();

    }

    protected void populateEnemyAnimationData(int idleColCount, int idleRolCount, int walkColCount, int walkRowCount, int attackColCount, int attackRowCount, int deathColCount, int deathRowCount) {
        //Idle Animation
        this.idleColCount = idleColCount;
        this.idleRowCount = idleRolCount;

        //Walk Animation
        this.walkColCount = walkColCount;
        this.walkRowCount = walkRowCount;

        //Attack Animation
        this.attackColCount = attackColCount;
        this.attackRowCount = attackRowCount;

        //Death Animation
        this.deathColCount = deathColCount;
        this.deathRowCount = deathRowCount;
    }


    //Adding the enemy's animations to their respective arrays.
    protected void addMapsToAnimations(String basePath) {
        //System.out.println(idleColCount+"  |  "+idleRowCount+"  |  "+spriteWidth+"  |  "+spriteHeight);
        animations.add(importSprites(basePath+"IdleMap.png", idleColCount, idleRowCount, spriteWidth, spriteHeight));
        animations.add(importSprites(basePath+"WalkMap.png", walkColCount, walkRowCount, spriteWidth, spriteHeight));
        animations.add(importSprites(basePath+"AttackMap.png", attackColCount, attackRowCount, spriteWidth, spriteHeight));
        animations.add(importSprites(basePath+"DeathMap.png", deathColCount, deathRowCount, spriteWidth, spriteHeight));
    }

   // default enemy with stats
    public Enemy() throws IOException {
        //this(30, 5, 2, 1, 1, 50, 100);
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
                int reversedX = (cols - 1 - x) * spriteWidth; // Start from the rightmost column
                sprites[y * cols + x] = image.getSubimage(
                        reversedX, y * spriteHeight, spriteWidth, spriteHeight
                );
            }
        }
        return sprites;
    }
    public Rectangle getHitbox() {
        return this.hitbox;
    }

    public void animate() {

        //If Enemy is idle
        if(state == State.IDLE) {
            aniCounter++;
            if (aniCounter >= aniSpeed) {
                aniCounter = 0;
                aniIndex++;
                if (!animations.isEmpty() && aniIndex >= animations.get(0).length) {
                    aniIndex = 0;
                }
            }
        }

        //If Enemy is walking
        if(state == State.WALKING) {
            walkCounter++;
            if (walkCounter >= walkSpeed) {
                walkCounter = 0;
                walkIndex++;
                if (!animations.isEmpty() && walkIndex >= animations.get(1).length) {
                    walkIndex = 0;
                }
            }

            //Moving the Enemy's position while it walks
            Rectangle temp = this.getBounds();
            if(!startXset) {
                startBounds = this.getBounds();
                startXset = true;
            }
            currentX = temp.x;
            //System.out.println("Currrent X: "+currentX + "Attack Trigger X: "+attackTriggerX+" is attaacking: "+isAttacking);
            this.setBounds(temp.x -=movingSpeed, temp.y, temp.width, temp.height);
        }

        //If Enemy is attacking
        if(state == State.ATTACKING) {
            attackCounter++;
            if (attackCounter >= attackSpeed) {
                attackCounter = 0;
                attackIndex++;
                if (!animations.isEmpty() && attackIndex >= animations.get(2).length) {
                    attackIndex = 0;
                }
            }
        }

        //If the Enemy is dying and has not reached the end of its death animation
        if(state == State.DYING && deathIndex != animations.get(3).length-1) {
            deathCounter ++;
            if(deathCounter >= deathSpeed) {
                deathCounter = 0;
                deathIndex++;
            }
        }

    }

    //custome paint
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*


        // draw the hitbox (centered) and health bar
        g.setColor(isTargeted ? Color.GREEN : Color.RED);

        int centeredX = (getWidth() - hitbox.width) / 2;
        int centeredY = (getHeight() - hitbox.height) / 2;

        //g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        */
        // health bar across the top
        int barX = (getWidth() - healthBar.width) / 2;
        healthBar.setLocation(barX, 0);
        g.setColor(Color.red);
        g.fillRect(healthBar.x, healthBar.y, healthBar.width, healthBar.height);

        // fill portion of the bar for current health
        int healthBarWidth = (int)((double) currentHealth / maxHealth * healthBar.width);
        g.setColor(Color.GREEN);
        g.fillRect(healthBar.x, healthBar.y, healthBarWidth, healthBar.height);



        if(state == State.WALKING) {
            g.drawImage(animations.get(1)[walkIndex], 0, 0, 75, 75, null);
        }
        if(state == State.IDLE) {
            g.drawImage(animations.get(0)[aniIndex], 0, 0, 75, 75, null);
        }

        if(state == State.ATTACKING) {
            g.drawImage(animations.get(2)[attackIndex], 0, 0, 75, 75, null);
        }

        if(state == State.DYING) {
            g.drawImage(animations.get(3)[deathIndex], 0, 0, 75, 75, null);
        }
    }

    private Runnable onComplete;
    private boolean isAttacking = false;
    private int attackTriggerX = 600;
    private int slashX, slashY;

    private int deathDelayCounter = 0; // Counter to track delay frames
    private static final int DEATH_DELAY_FRAMES = 30;

    public void updateEnemyStatus() {

        if(currentX <= attackTriggerX && !isAttacking) {

            this.state = State.ATTACKING;
            isAttacking = true;
        }

        if(isAttacking) {
            if(attackIndex >= animations.get(2).length-1) {

                this.state = State.IDLE;
                attackIndex = 0;

                attackAnimation.placeAnimation(slashX, slashY);
                AttackPlane.addAniToQue(attackAnimation);
                AttackPlane.animations.get(0).startAnimation();
                Game.gui.gameScreen.northPanel.attackPlane.playAnimation(() -> {
                    AttackPlane.animations.get(0).stopAnimation(); // Stop animation
                    Random rand = new Random();
                    int dmg = rand.nextInt(10); // Random between 0-9
                    Game.player.takeDamage(dmg);  // Apply damage

                    // Reset position & state
                    this.state = State.IDLE;
                    this.setBounds(startBounds);
                    currentX = this.getBounds().x;
                    onComplete.run();
                });

            }
        }

        if(currentHealth <= 0 && state != State.DYING) {
            this.state = State.DYING;

        }

        if (state == State.DYING) {
            if (aniIndex >= animations.get(3).length - 1) {
                deathDelayCounter++; // Start counting after animation ends

                if (deathDelayCounter >= DEATH_DELAY_FRAMES) {
                    Game.gui.gameScreen.northPanel.removeEnemy(this);
                    this.dead = true;
                    return;
                }
            }
        }
    }

    public Rectangle startBounds;

    public void setStartBounds(Rectangle r) {
        this.startBounds = r;
    }

   // take damage mechanic
    public void takeDamage(int damage) {
        // reducing damage by defense
        int reducedDamage = Math.max(damage - defense, 1);
        currentHealth = Math.max(currentHealth - reducedDamage, 0);
        FloatingText.createEffect("-" + damage, this, Color.RED);
        //System.out.println("visuals have started ------");
        if (currentHealth <= 0) {
            System.out.println(this.name + " has been defeated!");
        } else {
            System.out.println(this.name + " Health: " + currentHealth + "/" + maxHealth);
        }

        // Refresh UI
        revalidate();
        repaint();
    }
    Animation ani = new Fireball();
    //example attack method from fireball

    public void attack(Runnable onComplete, int x, int y) throws IOException {

        this.onComplete = onComplete;
        attackIndex = 0;
        this.state = State.WALKING;
        this.isAttacking = false;
        this.slashX = x;
        this.slashY = y;

    }

    public abstract void populateLootTable();



    public Map<String, Object> getLootTable() {
        return lootTable;
    }

    public Map.Entry<String, Object> generateLoot(){
        if(Config.debug)
            System.out.println("--* "+getClass()+".generateLoot() CALLED *--\n");

        Random rng = new Random();
        int x = rng.nextInt(lootTable.size());

        Iterator<Map.Entry<String, Object>> iterator = lootTable.entrySet().iterator();
        for (int i = 0; i < x; i++) {
            iterator.next(); // Skip to the desired index
        }
        if(Config.debug)
            System.out.println("--* "+getClass()+".generateLoot() FINISHED *--\n");

        return iterator.next();
    }
}
