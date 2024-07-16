package entities;

import frame.Panel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Bandit extends Entity {

    private static final int MIN_DIRECTION_CHANGE_INTERVAL = 240;
    private static final int MAX_DIRECTION_CHANGE_INTERVAL = 480;

    private int countCooldown;
    private final Random rand;
    private String lastCollisionDirection;

    String weaponType;

    public Bandit(int x, int y, Panel gamePanel, String banditType, String weaponType) {
        super(x, y, gamePanel);
        this.rand = new Random();
        this.direction = "standing";
        this.Speed = 1;
        this.lastCollisionDirection = "none";
        this.weaponType = weaponType;
        health = 20;
        loadEntityVisuals(banditType);
    }

    @Override
    public void setEntity() {

        gamePanel.ck.checkTileCollision(this); // check if the bandit is colliding with the tile collision areas

        for (Entity entity : gamePanel.getEntities()) {
            gamePanel.ck.checkEntityCollision(this, entity); // check if the bandit is colliding with other entities
        }

        checkEntityHealth(this);

        moveEntity();

        updateHitbox();

        updateSpriteFlag();
    }

    @Override
    protected void loadEntityVisuals(String banditType) {
        try {
            String path = banditType + File.separator;
            standing = loadImage(path + "standing.png");
            walkDown1 = loadImage(path + "walkDown1.png");
            walkDown2 = loadImage(path + "walkDown2.png");
            walkUp1 = loadImage(path + "walkUp1.png");
            walkUp2 = loadImage(path + "walkUp2.png");
            walkLeft1 = loadImage(path + "walkLeft1.png");
            walkLeft2 = loadImage(path + "walkLeft2.png");
            walkRight1 = loadImage(path + "walkRight1.png");
            walkRight2 = loadImage(path + "walkRight2.png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Bandit Visuals!", e);
        }
    }

    @Override
    protected void moveEntity() {
        boolean collided = false;

        // check for collision, move the entity & save last direction before collision
        if (direction.contains("Up")) {
            if (!collisionUp) { y -= Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "Up"; }
        }

        if (direction.contains("Down")) {
            if (!collisionDown) { y += Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "Down"; }
        }

        if (direction.contains("Left")) {
            if (!collisionLeft) { x -= Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "Left"; }
        }

        if (direction.contains("Right")) {
            if (!collisionRight) { x += Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "Right"; }
        }

        if (countCooldown > 0) countCooldown--; // decrease cooldown timer

        if (collided || countCooldown == 0) {  // if the entity collides or  the cooldown ends
            // gen random number between 0 & 1
            if (rand.nextDouble() < 0.65) direction = "standing";  // 65 % chance to be in standing mode
            else direction = setDirection(); // get new direction
            // reset cooldown timer to a random value
            countCooldown = MIN_DIRECTION_CHANGE_INTERVAL + rand.nextInt(MAX_DIRECTION_CHANGE_INTERVAL - MIN_DIRECTION_CHANGE_INTERVAL + 1);
        }
    }

    private void updateSpriteFlag() {
        spriteCounter++;
        int spriteThreshold = 20;
        if (spriteCounter > spriteThreshold) {
            spriteFlag = (spriteFlag == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private String setDirection() {
        List<String> validDirections = new ArrayList<>(Arrays.asList("Up", "Up&Left", "Up&Right", "Down", "Down&Left", "Down&Right", "Left", "Right", "standing"));

        // directions that can cause collision are removed
        if (collisionUp) { validDirections.remove("Up"); validDirections.remove("Up&Left"); validDirections.remove("Up&Right"); }
        if (collisionDown) { validDirections.remove("Down"); validDirections.remove("Down&Left"); validDirections.remove("Down&Right"); }
        if (collisionLeft) { validDirections.remove("Left"); validDirections.remove("Up&Left"); validDirections.remove("Down&Left"); }
        if (collisionRight) { validDirections.remove("Right"); validDirections.remove("Up&Right"); validDirections.remove("Down&Right"); }

        // entity does not go into the direction that made him collide
        validDirections.remove(lastCollisionDirection);

        if (validDirections.isEmpty()) validDirections.add("standing"); // standing if no direction is available

        return validDirections.get(rand.nextInt(validDirections.size()));
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSprite();

        int drawX = x - gamePanel.player.getEntityX() + gamePanel.player.getEntityScreenX();
        int drawY = y - gamePanel.player.getEntityY() + gamePanel.player.getEntityScreenY();
        g2.drawImage(image, drawX, drawY, width, height, null);

        drawWeapon(g2, drawX, drawY); // draw weapon after the bandit

        if(gamePanel.player.drawBoxes){ // collision and hit boxes for bandit
            drawEntityBox(g2, drawX, drawY, collisionBox, Color.red);
            drawEntityBox(g2, drawX, drawY, verticalHitBox, Color.blue);
            drawEntityBox(g2, drawX, drawY, horizontalHitBox, Color.blue);
        }
    }

    private void drawWeapon(Graphics2D g2, int drawX, int drawY) {

        BufferedImage weapon;

        BufferedImage standingWeapon = null;
        BufferedImage walkLeft2Weapon = null; BufferedImage walkLeft1Weapon = null;
        BufferedImage walkRight2Weapon = null; BufferedImage walkRight1Weapon = null;
        BufferedImage walkUp1Weapon = null; BufferedImage walkUp2Weapon = null;
        BufferedImage walkDown1Weapon = null; BufferedImage walkDown2Weapon = null;

        if (weaponType != null) {
            switch (weaponType) {
                case "baseballBat":
                    standingWeapon = standingBat;
                    walkDown1Weapon = walkDown1Bat; walkDown2Weapon = standingBat;
                    walkUp1Weapon = walkUp1Bat; walkUp2Weapon = walkUp2Bat;
                    walkLeft1Weapon = walkLeft1Bat; walkLeft2Weapon = walkLeft2Bat;
                    walkRight1Weapon = walkRight1Bat; walkRight2Weapon = walkRight2Bat;
                    break;
                case "dagger":
                    standingWeapon = standingDag;
                    walkDown1Weapon = walkDown1Dag; walkDown2Weapon = standingDag;
                    walkUp1Weapon = walkUp1Dag; walkUp2Weapon = walkUp2Dag;
                    walkLeft1Weapon = walkLeft1Dag; walkLeft2Weapon = walkLeft2Dag;
                    walkRight1Weapon = walkRight1Dag; walkRight2Weapon = walkRight2Dag;
                default: break;
            }
        }

        weapon = switch (direction) {
            case "Down", "Down&Left", "Down&Right" -> (spriteFlag == 1) ? walkDown1Weapon : walkDown2Weapon;
            case "Up", "Up&Left", "Up&Right" -> (spriteFlag == 1) ? walkUp1Weapon : walkUp2Weapon;
            case "Left" -> (spriteFlag == 1) ? walkLeft1Weapon : walkLeft2Weapon;
            case "Right" -> (spriteFlag == 1) ? walkRight1Weapon : walkRight2Weapon;
            case "standing" -> standingWeapon;
            default -> null;
        };

        if (weapon != null) {
            g2.drawImage(weapon, drawX, drawY, width, height, null);
        }
    }

    private void drawEntityBox(Graphics2D g2, int drawX, int drawY, Rectangle box, Color color) {
        g2.setColor(color); // color for the rectangle box
        g2.drawRect(drawX + box.x, drawY + box.y, box.width, box.height);
    }
}
