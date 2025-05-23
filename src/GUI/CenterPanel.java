package GUI;

import Entities.CardSlot;
import Entities.Enemies.Enemy;
import MainPackage.Config;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

//This is the visual component class that cards are played on top of.
//The main function of this class is to take the bounds of the CardSlots in the GameplayPane, and draw corrosponding visual references for the positions.
public class CenterPanel extends JPanel {

    public CenterPanel() {
        //setBackground(Color.green);
        setPreferredSize(new Dimension(100, (int) (Config.frameSize.height * 0.3)));
    }

    private BufferedImage background = loadImage("/Resources/MiddleDone.png");

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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        BufferedImage bck = background.getSubimage(0, 0, 1000, 333);
        g.drawImage(bck, 0, -6, 1000, this.getHeight()+6, null);
        g.setColor(Color.black);

        //We are itterating through the slots currently present in the GamePlayPane, and drawing a box underneath each.
        for(CardSlot slot : GameplayPane.cardSlots) {

            int y = (((int) (Config.frameSize.height * 0.3)) - 210) / 2;//Y calculation due to the different parents.
            g.fillRect(slot.x, y, 160, 220);//160x220 standard slot size.
            //System.out.println("size of slot array "+GameplayPane.cardSlots.size());
        }
    }

}
//TODO:
/*
For each CardSlot in array in GameplayPane, we need to draw a rectangle at there coords.
 */
