package Entities.Cards;

import Entities.Animations.SatyrCardAttackAnimation;
import Entities.Animations.XaxisAnimation;
import Entities.Enemies.Enemy;
import Entities.Player;

import java.awt.*;
import java.io.IOException;

public class Satyr_MCard extends Card {

    private static final String IMAGE_PATH = "/Resources/Cards/Satyr_MonsterCard.png";

    public Satyr_MCard() throws IOException {
        super(IMAGE_PATH);
        this.image = loadImage(IMAGE_PATH);
        this.animation = new SatyrCardAttackAnimation();
    }

    @Override
    public void resetAnimation() throws IOException {

    }

    @Override
    public void effect() {


    }
    @Override
    public void initCardAniBounds(Player player, Enemy enemy) throws IOException {
        ((XaxisAnimation) animation).initAnimation(player, enemy);
    }

    @Override
    public void initCardAniBounds(Enemy enemy, Player player) throws IOException {
        ((XaxisAnimation) animation).initAnimation(enemy, player);
    }

    //paint component for drawing the cards image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//clears old drawings and renders cards

        if (image != null) {//check if image is available, draw it if so or use placeholder
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);//draw card image
        } else {//else add red placeholder
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight()); // Red box if image is missing
        }
    }
}

