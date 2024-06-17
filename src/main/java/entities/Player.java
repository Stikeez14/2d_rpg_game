package entities;

import frame.Panel;
import inputs.keyHandler;
import map.mapSettings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Player {

    private final keyHandler key = new keyHandler();

    private BufferedImage standingPlayer, walkDown1, walkDown2 , walkUp1, walkUp2, walkLeft1, walkLeft2, walkRight1, walkRight2;
    private String direction;

    private int spriteCounter = 0;
    private int spriteFlag = 1;

    private int x;
    private int y;

    private final int width;
    private final int height;

    Rectangle hitbox;

    public Player(int x, int y, Panel gamePanel) {
        this.x = x;
        this.y = y;

        width = mapSettings.getTileSize();
        height = mapSettings.getTileSize();
        hitbox = new Rectangle(x, y, width, height);

        gamePanel.addKeyListener(key);
        gamePanel.setFocusable(true); // helps with receiving key events
        gamePanel.requestFocusInWindow();

        getPlayerImage();
    }

    public void setPlayer() {
        int walkSpeed = 2, runSpeed = 4;
        int Speed = key.shiftPressed ? runSpeed : walkSpeed;

        if (key.upPressed && key.leftPressed) {
            y -= Speed;
            x -= Speed;
        } else if (key.upPressed && key.rightPressed) {
            y -= Speed;
            x += Speed;
        } else if (key.downPressed && key.leftPressed) {
            y += Speed;
            x -= Speed;
        } else if (key.downPressed && key.rightPressed) {
            y += Speed;
            x += Speed;
        } else if (key.upPressed) {
            direction = "up";
            y -= Speed;
        } else if (key.downPressed) {
            direction = "down";
            y += Speed;
        } else if (key.leftPressed) {
            direction = "left";
            x -= Speed;
        } else if (key.rightPressed) {
            direction = "right";
            x += Speed;
        }
        else direction = "standing";

        hitbox.x = x;
        hitbox.y = y;

        spriteCounter++;
        if (spriteCounter > 20) {
            if (spriteFlag == 1) spriteFlag = 2;
            else if (spriteFlag == 2) spriteFlag = 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {

        BufferedImage player = null;

        switch (direction) {
            case "down":
                if (spriteFlag == 1) player = walkDown1;
                if (spriteFlag == 2) player = walkDown2;
                break;
            case "up":
                if (spriteFlag == 1) player = walkUp1;
                if (spriteFlag == 2) player = walkUp2;
                break;
            case "left":
                if (spriteFlag == 1) player = walkLeft1;
                if (spriteFlag == 2) player = walkLeft2;
                break;
            case "right":
                if (spriteFlag == 1) player = walkRight1;
                if (spriteFlag == 2) player = walkRight2;
                break;
            case "standing": player = standingPlayer; break;
        }

        g2.drawImage(player, x, y, width, height, null);

    }

    public void getPlayerImage() {
        try {
            standingPlayer = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "standingPlayer.png")));
            walkDown1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkDown1.png")));
            walkDown2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkDown2.png")));
            walkUp1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkUp1.png")));
            walkUp2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkUp2.png")));
            walkLeft1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkLeft1.png")));
            walkLeft2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkLeft2.png")));
            walkRight1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkRight1.png")));
            walkRight2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "player" + File.separator + "walkRight2.png")));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
