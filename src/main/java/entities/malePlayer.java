package entities;

import frame.Panel;
import frame.Window;
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

    private BufferedImage standingPlayer, walkDown1, walkDown2 , walkUp1, walkUp2, walkLeft1, walkLeft2, walkRight1, walkRight2,
    standingEternalChestplate, standingEternalHelmet, standingEternalLeggings,
    walkupEternalHelmet, walkup1EternalChestplate, walkup2EternalChestplate, walkup1EternalLeggings, walkup2EternalLeggings,
    walkdownEternalHelmet, walkdown1EternalChestplate, walkdown2EternalChestplate, walkdown1EternalLeggings, walkdown2EternalLeggings,
    walkleft2EternalChestplate, walkleft1EternalChestplate , walkleftEternalHelmet, walkleft2EternalLeggings, walkleft1EternalLeggings,
    walkright1EternalChestplate, walkright2EternalChestplate, walkrightEternalHelmet, walkright1EternalLeggings, walkright2EternalLeggings;

    Panel gamePanel;

    public boolean collisionUp, collisionDown, collisionLeft, collisionRight;

    public int Speed;
    public String direction;

    private int spriteCounter = 0;
    private int spriteFlag = 1;

    public int x; // coordinates used for spawning the player on the map
    public int y;

    public final int screenX; // coordinates used for camera
    public final int screenY;

    private final int width;
    private final int height;

    public Rectangle hitbox;

    public boolean collision = false;

    private boolean isSprinting = false;
    private long sprintStartTime = 0;
    private long lastSprintTime = 0;

    private boolean hasHelmet = false;
    private boolean hasChestplate = false;
    private boolean hasLeggings = false;

    public malePlayer(int x, int y, Panel gamePanel) {
        this.x = x;
        this.y = y;
        this.gamePanel=gamePanel;

        screenX = Window.getScreenWidth()/2 - (mapSettings.getTileSize()/2); // subtracting tile size
        screenY = Window.getScreenHeight()/2 - (mapSettings.getTileSize()/2); // for positioning the player in the center

        width = mapSettings.getTileSize();
        height = mapSettings.getTileSize();

        hitbox = new Rectangle(37, 74, 50, 50);


        gamePanel.addKeyListener(key);
        gamePanel.setFocusable(true); // helps with receiving key events
        gamePanel.requestFocusInWindow();

        loadPlayerVisuals();
    }

    public void setPlayer() {
        Speed = 2;
        int runSpeed = 4;
        long currentTime = System.currentTimeMillis();

        //* SPRINTING */
        if (isSprinting) { // checking if the player is allowed to sprint
            long sprintDuration = 3000; // 3 sec

            if (currentTime - sprintStartTime < sprintDuration) {
                Speed = runSpeed;
            } else {
                isSprinting = false;
                lastSprintTime = currentTime; // get the time when the sprint ended
            }
        } else {
            long sprintCooldown = 15000; // 15 sec

            if (currentTime - lastSprintTime >= sprintCooldown && key.shiftPressed) { // check if cooldown ended
                isSprinting = true;
                sprintStartTime = currentTime;
                Speed = runSpeed;
            }
        }

        // update player direction
        if (key.upPressed) {
            if (key.leftPressed) {
                direction = "up&left";
            } else if (key.rightPressed) {
                direction = "up&right";
            } else {
                direction = "up";
            }
        } else if (key.downPressed) {
            if (key.leftPressed) {
                direction = "down&left";
            } else if (key.rightPressed) {
                direction = "down&right";
            } else {
                direction = "down";
            }
        } else if (key.leftPressed) {
            direction = "left";
        } else if (key.rightPressed) {
            direction = "right";
        } else {
            direction = "standing";
        }

        gamePanel.ck.checkTile(this);

        if (direction.contains("up") && !collisionUp) {
            y -= Speed;
        }
        if (direction.contains("down") && !collisionDown) {
            y += Speed;
        }
        if (direction.contains("left") && !collisionLeft) {
            x -= Speed;
        }
        if (direction.contains("right") && !collisionRight) {
            x += Speed;
        }

        // update sprite flag based on counter
        spriteCounter++;
        int spriteThreshold = isSprinting ? 10 : 20;

        if (spriteCounter > spriteThreshold) {
            spriteFlag = (spriteFlag == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {

        BufferedImage player = null;
        BufferedImage helmet = null;
        BufferedImage chestplate = null;
        BufferedImage leggings = null;

        switch (direction) {
            case "down", "down&left", "down&right":
                if (spriteFlag == 1) player = walkDown1;
                if (spriteFlag == 2) player = walkDown2;
                if (hasHelmet) helmet = walkdownEternalHelmet;
                if (hasChestplate) chestplate = (spriteFlag == 1) ? walkdown1EternalChestplate : walkdown2EternalChestplate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkdown1EternalLeggings : walkdown2EternalLeggings;
                break;
            case "up", "up&left", "up&right":
                if (spriteFlag == 1) player = walkUp1;
                if (spriteFlag == 2) player = walkUp2;
                if (hasHelmet) helmet = walkupEternalHelmet;
                if (hasChestplate) chestplate = (spriteFlag == 1) ? walkup1EternalChestplate : walkup2EternalChestplate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkup1EternalLeggings : walkup2EternalLeggings;
                break;
            case "left":
                if (spriteFlag == 1) player = walkLeft1;
                if (spriteFlag == 2) player = walkLeft2;
                if (hasHelmet) helmet = walkleftEternalHelmet;
                if (hasChestplate) chestplate = (spriteFlag == 1) ? walkleft1EternalChestplate : walkleft2EternalChestplate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkleft1EternalLeggings : walkleft2EternalLeggings;
                break;
            case "right":
                if (spriteFlag == 1) player = walkRight1;
                if (spriteFlag == 2) player = walkRight2;
                if (hasHelmet) helmet = walkrightEternalHelmet;
                if (hasChestplate) chestplate = (spriteFlag == 1) ? walkright1EternalChestplate : walkright2EternalChestplate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkright1EternalLeggings : walkright2EternalLeggings;
                break;
            case "standing":
                player = standingPlayer;
                if (hasHelmet) helmet = standingEternalHelmet;
                if (hasChestplate) chestplate = standingEternalChestplate;
                if (hasLeggings) leggings = standingEternalLeggings;
                break;
        }

        g2.drawImage(player, screenX, screenY, width, height, null);

        if (helmet != null) {
            g2.drawImage(helmet, screenX, screenY, width, height, null);
        }
        if (chestplate != null) {
            g2.drawImage(chestplate, screenX, screenY, width, height, null);
        }
        if (leggings != null) {
            g2.drawImage(leggings, screenX, screenY, width, height, null);
        }

        // TESTING FOR PLAYER COLLISION

        int screenX = x - gamePanel.player.x + gamePanel.player.screenX;
        int screenY = y - gamePanel.player.y + gamePanel.player.screenY;

        g2.setColor(Color.RED);
        g2.drawRect(screenX + hitbox.x, screenY + hitbox.y, hitbox.width, hitbox.height);
    }

    public void loadPlayerVisuals() {
        try {
            /* PLAYER DEFAULT SKIN */
            standingPlayer = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "standingPlayer.png")));
            walkDown1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkDown1.png")));
            walkDown2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkDown2.png")));
            walkUp1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkUp1.png")));
            walkUp2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkUp2.png")));
            walkLeft1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkLeft1.png")));
            walkLeft2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkLeft2.png")));
            walkRight1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkRight1.png")));
            walkRight2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "walkRight2.png")));

           /* ETERNAL ARMOUR SKIN */
            standingEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalHelmet.png")));
            standingEternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalChestplate.png")));
            standingEternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalLeggings.png")));

            walkupEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkup" + File.separator + "walkupEternalHelmet.png")));
            walkup1EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkup" + File.separator + "walkup1EternalChestplate.png")));
            walkup2EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkup" + File.separator + "walkup2EternalChestplate.png")));
            walkup1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkup" + File.separator + "walkup1EternalLeggings.png")));
            walkup2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkup" + File.separator + "walkup2EternalLeggings.png")));

            walkdownEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkdown" + File.separator + "walkdownEternalHelmet.png")));
            walkdown1EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkdown" + File.separator + "walkdown1EternalChestplate.png")));
            walkdown2EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkdown" + File.separator + "walkdown2EternalChestplate.png")));
            walkdown1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkdown" + File.separator + "walkdown1EternalLeggings.png")));
            walkdown2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkdown" + File.separator + "walkdown2EternalLeggings.png")));

            walkleftEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkleft" + File.separator + "walkleftEternalHelmet.png")));
            walkleft1EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkleft" + File.separator + "walkleft1EternalChestplate.png")));
            walkleft2EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkleft" + File.separator + "walkleft2EternalChestplate.png")));
            walkleft1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkleft" + File.separator + "walkleft1EternalLeggings.png")));
            walkleft2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkleft" + File.separator + "walkleft2EternalLeggings.png")));

            walkrightEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkright" + File.separator + "walkrightEternalHelmet.png")));
            walkright1EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkright" + File.separator + "walkright1EternalChestplate.png")));
            walkright2EternalChestplate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkright" + File.separator + "walkright2EternalChestplate.png")));
            walkright1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkright" + File.separator + "walkright1EternalLeggings.png")));
            walkright2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkright" + File.separator + "walkright2EternalLeggings.png")));

        } catch(IOException e){
            throw new RuntimeException("Failed to load player visuals!", e);
        }
    }

    /* JUST FOR TESTING */
    public void setArmour(boolean hasHelmet, boolean hasChestplate, boolean hasLeggings) {
        this.hasHelmet=hasHelmet;
        this.hasChestplate=hasChestplate;
        this.hasLeggings=hasLeggings;
    }
}
