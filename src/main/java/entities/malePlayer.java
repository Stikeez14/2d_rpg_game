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

public class malePlayer {

    private final keyHandler key = new keyHandler();

    private BufferedImage standingPlayer, walkDown1, walkDown2 , walkUp1, walkUp2, walkLeft1, walkLeft2, walkRight1, walkRight2, standingPlayerChestIronArmour;
    private String direction;

    private int spriteCounter = 0;
    private int spriteFlag = 1;

    private int x;
    private int y;

    private final int width;
    private final int height;

    Rectangle hitbox;

    private boolean isSprinting = false;
    private long sprintStartTime = 0;
    private long lastSprintTime = 0;

    public malePlayer(int x, int y, Panel gamePanel) {
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
        int runSpeed = 4;
        int Speed = 2;

        if (isSprinting) { //checking if the player is allowed to sprint

            long currentTime = System.currentTimeMillis();
            long sprintDuration = 3000; //5 sec

            if (currentTime - sprintStartTime < sprintDuration) {
                Speed = runSpeed;
            } else {
                isSprinting = false;
                lastSprintTime = currentTime; // get the time when the sprint ended
            }
        } else {
            long currentTime = System.currentTimeMillis();
            long sprintCooldown = 15000; //15 sec

            if (currentTime - lastSprintTime >= sprintCooldown && key.shiftPressed) { //check if cooldown ended
                isSprinting = true;
                sprintStartTime = currentTime;
                Speed = runSpeed;
            }
        }

        if (key.upPressed && key.leftPressed) {
            direction = "up&left";
            y -= Speed;
            x -= Speed;
        } else if (key.upPressed && key.rightPressed) {
            direction = "up&right";
            y -= Speed;
            x += Speed;
        } else if (key.downPressed && key.leftPressed) {
            direction = "down&left";
            y += Speed;
            x -= Speed;
        } else if (key.downPressed && key.rightPressed) {
            direction = "down&right";
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
            case "down", "down&left", "down&right":
                if (spriteFlag == 1) player = walkDown1;
                if (spriteFlag == 2) player = walkDown2;
                break;
            case "up", "up&left", "up&right":
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
            case "standing": player = standingPlayerChestIronArmour; break;
        }

        g2.drawImage(player, x, y, width, height, null);

    }

    public void getPlayerImage() {
        try {
            standingPlayer = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "standingPlayer.png")));
            standingPlayerChestIronArmour = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator+"equipment"+File.separator+"armours"+File.separator+"standingEternalFull.png")));
            walkDown1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkDown1.png")));
            walkDown2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkDown2.png")));
            walkUp1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkUp1.png")));
            walkUp2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkUp2.png")));
            walkLeft1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkLeft1.png")));
            walkLeft2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkLeft2.png")));
            walkRight1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkRight1.png")));
            walkRight2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkRight2.png")));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
