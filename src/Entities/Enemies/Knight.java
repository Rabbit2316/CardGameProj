package Entities.Enemies;

import Entities.Animations.GoblinAttackAnimation;

import java.awt.*;
import java.io.IOException;

import static MainPackage.Tools.importSpritesCutFromLeft;

public class Knight extends Enemy {
    public Knight() throws IOException {
        super("Knight", 30, 10, 5, 2, 1, 108, 86, 96, 96, 4,1, 7,1, 4,1, 6,1, new GoblinAttackAnimation(), 10); // (HP, Attack, Defense, Agility, Speed, Weight)
        this.hitbox = new Rectangle(30, 20, 50, 70);
    }



    @Override
    protected void addMapsToAnimations(String basePath) {
        //System.out.println(idleColCount+"  |  "+idleRowCount+"  |  "+spriteWidth+"  |  "+spriteHeight);
        animations.add(importSpritesCutFromLeft(basePath+"IdleMap.png", idleColCount, idleRowCount, 72, 86));
        animations.add(importSpritesCutFromLeft(basePath+"WalkMap.png", walkColCount, walkRowCount, 72, 86));
        animations.add(importSpritesCutFromLeft(basePath+"AttackMap.png", attackColCount, attackRowCount, 108, 86));
        animations.add(importSpritesCutFromLeft(basePath+"DeathMap.png", deathColCount, deathRowCount, 80, 86));
    }


    @Override
    public void populateLootTable() {
        this.lootTable.put("Gold", 400);
        this.lootTable.put("knights Pendant", 0);
    }

}
