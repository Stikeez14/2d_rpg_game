package entities;

import frame.Panel;
import map.Map;
import map.Settings;
import inputs.KeyboardKeys;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {

    private final KeyboardKeys key = new KeyboardKeys();

    private BufferedImage standingEternalChestPlate, standingEternalHelmet, standingEternalLeggings,
            walkUpEternalHelmet, walkUp1EternalChestPlate, walkUp2EternalChestPlate, walkUp1EternalLeggings, walkUp2EternalLeggings,
            walkDownEternalHelmet, walkDown1EternalChestPlate, walkDown2EternalChestPlate, walkDown1EternalLeggings, walkDown2EternalLeggings,
            walkLeft2EternalChestPlate, walkLeft1EternalChestPlate , walkLeftEternalHelmet, walkLeft2EternalLeggings, walkLeft1EternalLeggings,
            walkRight1EternalChestPlate, walkRight2EternalChestPlate, walkRightEternalHelmet, walkRight1EternalLeggings, walkRight2EternalLeggings;

    private boolean isSprinting = false;
    private long sprintStartTime = 0;
    private long lastSprintTime = 0;

    private boolean hasHelmet = false;
    private boolean hasChestPlate = false;
    private boolean hasLeggings = false;

    public Player(int x, int y, Panel gamePanel) {
        super(x, y, gamePanel);
        gamePanel.addKeyListener(key);
        gamePanel.setFocusable(true); // helps with receiving key events
        gamePanel.requestFocusInWindow();
        loadEntityVisuals();
    }

    @Override
    public void setEntity() {
        int runSpeed;

        // normal speed is based on scale (odd | even)
        if(Settings.getScale() % 2 == 0) Speed = Settings.getScale() / 2;
        else Speed = (Settings.getScale() + 1) / 2;
        runSpeed = Speed + 1;

        long currentTime = System.currentTimeMillis();

        //* SPRINTING */
        if (isSprinting) { // checking if the player is allowed to sprint
            long sprintDuration = 3000; // 3 sec sprint duration

            if (currentTime - sprintStartTime < sprintDuration) {
                Speed = runSpeed;
            } else {
                isSprinting = false;
                int oldScale = Settings.getScale(); // store the scale before changing it
                Settings.setWalkScale(); // change the scale to a bigger value (=6)
                Map.setScale(Settings.getScale()); // update in Map Class the scale value for tiles & collision areas
                adjustPosition(oldScale, Settings.getScale()); // adjust player position to be the same as before scaling
                updateHitbox();  // scale player hitbox
                lastSprintTime = currentTime; // get the time when the sprint ended
            }
        } else {
            long sprintCooldown = 12000; // 12 sec cooldown after sprinting was used

            // check if cooldown ended and activate sprinting again
            if (currentTime - lastSprintTime >= sprintCooldown &&
                    ((key.upPressed && key.shiftPressed) ||
                            (key.downPressed && key.shiftPressed) ||
                            (key.leftPressed && key.shiftPressed) ||
                            (key.rightPressed && key.shiftPressed) ||
                            key.shiftPressed)) {
                isSprinting = true;
                int oldScale = Settings.getScale(); // store the scale before changing it
                Settings.setSprintScale(); // change the scale to a smaller value (=5)
                Map.setScale(Settings.getScale()); // update in Map Class the scale value for tiles & collision areas
                adjustPosition(oldScale, Settings.getScale()); // adjust player position to be the same as before scaling
                updateHitbox(); // scale player hitbox
                sprintStartTime = currentTime;
                Speed = runSpeed; // update speed
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

        gamePanel.ck.checkTileCollision(this); // check if the player is colliding with the tiles collision areas
        moveEntity();     // move the player based on collision flags

        // update sprite flag based on counter
        spriteCounter++;
        int spriteThreshold = isSprinting ? 10 : 20;

        if (spriteCounter > spriteThreshold) {
            spriteFlag = (spriteFlag == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        BufferedImage helmet = null;
        BufferedImage chestPlate = null;
        BufferedImage leggings = null;

        switch (direction) {
            case "down", "down&left", "down&right":
                if (hasHelmet) helmet = walkDownEternalHelmet;
                if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkDown1EternalChestPlate : walkDown2EternalChestPlate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkDown1EternalLeggings : walkDown2EternalLeggings;
                break;
            case "up", "up&left", "up&right":
                if (hasHelmet) helmet = walkUpEternalHelmet;
                if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkUp1EternalChestPlate : walkUp2EternalChestPlate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkUp1EternalLeggings : walkUp2EternalLeggings;
                break;
            case "left":
                if (hasHelmet) helmet = walkLeftEternalHelmet;
                if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkLeft1EternalChestPlate : walkLeft2EternalChestPlate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkLeft1EternalLeggings : walkLeft2EternalLeggings;
                break;
            case "right":
                if (hasHelmet) helmet = walkRightEternalHelmet;
                if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkRight1EternalChestPlate : walkRight2EternalChestPlate;
                if (hasLeggings) leggings = (spriteFlag == 1) ? walkRight1EternalLeggings : walkRight2EternalLeggings;
                break;
            case "standing":
                if (hasHelmet) helmet = standingEternalHelmet;
                if (hasChestPlate) chestPlate = standingEternalChestPlate;
                if (hasLeggings) leggings = standingEternalLeggings;
                break;
        }

        if (helmet != null) {
            g2.drawImage(helmet, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
        }
        if (chestPlate != null) {
            g2.drawImage(chestPlate, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
        }
        if (leggings != null) {
            g2.drawImage(leggings, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
        }
    }

    @Override
    public void loadEntityVisuals() {
        try {
            /* PLAYER DEFAULT SKIN */
            standing = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "malePlayer" + File.separator + "standingPlayer.png")));
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
            standingEternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalChestPlate.png")));
            standingEternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalLeggings.png")));

            walkUpEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUpEternalHelmet.png")));
            walkUp1EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp1EternalChestPlate.png")));
            walkUp2EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp2EternalChestPlate.png")));
            walkUp1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp1EternalLeggings.png")));
            walkUp2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp2EternalLeggings.png")));

            walkDownEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDownEternalHelmet.png")));
            walkDown1EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown1EternalChestPlate.png")));
            walkDown2EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown2EternalChestPlate.png")));
            walkDown1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown1EternalLeggings.png")));
            walkDown2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown2EternalLeggings.png")));

            walkLeftEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeftEternalHelmet.png")));
            walkLeft1EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft1EternalChestPlate.png")));
            walkLeft2EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft2EternalChestPlate.png")));
            walkLeft1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft1EternalLeggings.png")));
            walkLeft2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft2EternalLeggings.png")));

            walkRightEternalHelmet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRightEternalHelmet.png")));
            walkRight1EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight1EternalChestPlate.png")));
            walkRight2EternalChestPlate = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight2EternalChestPlate.png")));
            walkRight1EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight1EternalLeggings.png")));
            walkRight2EternalLeggings = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + "equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight2EternalLeggings.png")));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load Player Visuals!");
        }
    }

    /* JUST FOR TESTING */
    public void setArmour(boolean hasHelmet, boolean hasChestPlate, boolean hasLeggings) {
        this.hasHelmet=hasHelmet;
        this.hasChestPlate=hasChestPlate;
        this.hasLeggings=hasLeggings;
    }
}
