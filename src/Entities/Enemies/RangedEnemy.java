package Entities.Enemies;

import Entities.Animations.Animation;
import GUI.AttackPlane;
import Entities.Animations.XaxisAnimation;
import MainPackage.Config;
import MainPackage.Game;
import MainPackage.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

public class RangedEnemy extends Enemy {


    public RangedEnemy(String name, int maxHealth, int attackPower, int defense, int agility, int speed, int w, int h, int sW, int sH, int idleColCount, int idleRolCount, int walkColCount, int walkRowCount, int attackColCount, int attackRowCount, int deathColCount, int deathRowCount, Animation attackAnimation, int weight) throws IOException {
        super(name, maxHealth, attackPower, defense, agility, speed, w, h, sW, sH, idleColCount, idleRolCount, walkColCount, walkRowCount, attackColCount, attackRowCount, deathColCount, deathRowCount, attackAnimation, weight);
        this.attackAnimation = attackAnimation;
    }

    @Override
    public void populateLootTable() {

    }

    @Override
    protected void addMapsToAnimations(String basePath) {
        //System.out.println(idleColCount+"  |  "+idleRowCount+"  |  "+spriteWidth+"  |  "+spriteHeight);
        animations.add(Tools.importSpritesCutFromLeft(basePath + "IdleMap.png", idleColCount, idleRowCount, spriteWidth, spriteHeight));
        animations.add(Tools.importSpritesCutFromLeft(basePath+"WalkMap.png", walkColCount, walkRowCount, spriteWidth, spriteHeight));
        animations.add(Tools.importSpritesCutFromLeft(basePath+"AttackMap.png", attackColCount, attackRowCount, spriteWidth, spriteHeight));
        animations.add(Tools.importSpritesCutFromLeft(basePath+"DeathMap.png", deathColCount, deathRowCount, spriteWidth, spriteHeight));
    }

    @Override
    public void updateEnemyStatus(Iterator<Enemy> iterator) {

        if (isAttacking) {
            //System.out.println(attackIndex + " " + (animations.get(2).length - 1));
            if (attackIndex >= animations.get(2).length) {

                this.state = State.IDLE;
                attackIndex = 0;

                AttackPlane.addAniToQue(attackAnimation);


                Runnable onComp = () -> {
                    Random rand = new Random();
                    int dmg = rand.nextInt(10); // Random between 0-9
                    Game.player.takeDamage(dmg);  // Apply damage

                    // Reset position & state
                    this.state = State.IDLE;
                    onComplete.run();

                };

                AttackPlane.animations.get(0).onComplete = onComp;
                AttackPlane.animations.get(0).currentState = Animation.State.MOVING;
                AttackPlane.animations.get(0).startAnimation();
                //

            }
        }

        if (currentHealth <= 0 && state != State.DYING) {
            this.state = State.DYING;

        }

        if (state == State.DYING) {

            Game.enemyWaiting = true;
            this.dead = true;
            if (deathIndex >= animations.get(3).length - 1) {
                deathDelayCounter++; // Start counting after animation ends
                // System.out.println(deathDelayCounter+" | "+DEATH_DELAY_FRAMES);

                // iterator.remove();
                Game.gui.gameScreen.northPanel.removeEnemy(this);
                iterator.remove();
                // System.out.println("enemy waiting set to false");
                Game.enemyWaiting = false;
            }
        }
    }

    @Override
    public void attack(Runnable onComplete, int x, int y) throws IOException {
        this.attackAnimation = attackAnimation.clone();
        ((XaxisAnimation) attackAnimation).initAnimation(this, Game.player);
        this.onComplete = onComplete;
        attackIndex = 0;
        this.state = State.ATTACKING;
        this.isAttacking = true;
        //this.slashX = x;
        //this.slashY = y;

    }

    @Override
    public void animate() {

        //If Enemy is idle
        if (state == State.IDLE) {
            aniCounter++;
            if (aniCounter >= aniSpeed) {
                aniCounter = 0;
                aniIndex++;
                if (!animations.isEmpty() && aniIndex >= animations.get(0).length) {
                    aniIndex = 0;
                }
            }
        }


        //If Enemy is attacking
        if (state == State.ATTACKING) {
            attackCounter++;
            if (attackCounter >= attackSpeed) {
                attackCounter = 0;
                attackIndex++;
                if (!animations.isEmpty() && attackIndex > animations.get(2).length) {
                    attackIndex = 0;
                }
            }
        }

        //If the Enemy is dying and has not reached the end of its death animation
        if (state == State.DYING && deathIndex != animations.get(3).length - 1) {
            deathCounter++;
            if (deathCounter >= deathSpeed) {
                deathCounter = 0;
                deathIndex++;
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {

        ///Painting the hitboxes if in developer mode.////////////
        if (Config.hitboxesOn) {
            g.setColor(Color.red);
            g.drawRect(rangedOrigin.x, rangedOrigin.y, 10, 10);
            g.setColor(Color.white);
            g.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            if (this.getBorder() == null) {
                setBorder(BorderFactory.createLineBorder(Color.white));
            }
        } else {
            this.setBorder(null);
        }
        /////////////////////////////////////////////////////////


        ////Painting the health bar.////////////////////////////
        //Center the health bar above the hitbox
        int barX = hitbox.x + (hitbox.width - healthBar.width) / 2;
        int barY = hitbox.y - healthBar.height - 5; // Offset by 5 pixels above the hitbox

        //Draw background bar (red)
        g.setColor(Color.red);
        g.fillRect(barX, barY, healthBar.width, healthBar.height);

        //Calculate and draw current health (green)
        int healthBarWidth = (int) ((double) currentHealth / maxHealth * healthBar.width);
        g.setColor(Color.green);
        g.fillRect(barX, barY, healthBarWidth, healthBar.height);
        ////////////////////////////////////////////////////////////


        ////////////////////Painting the sprites for different states.
        if (state == State.WALKING) {
            g.drawImage(animations.get(1)[walkIndex], 0, 0, 56, 77, null);
        }
        if (state == State.IDLE) {
            g.drawImage(animations.get(0)[aniIndex], 0, 0, 128, 128, null);
        }

        if (state == State.ATTACKING) {
            g.drawImage(animations.get(2)[attackIndex], 0, 0, 128, 128, null);
        }

        if (state == State.DYING) {
            g.drawImage(animations.get(3)[deathIndex], 0, 0, 128, 128, null);
        }
    }
}
