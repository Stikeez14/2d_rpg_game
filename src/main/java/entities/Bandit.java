package entities;

import frame.Panel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Bandit extends Entity {

    private static final int DIRECTION_CHANGE_INTERVAL = 240;
    private int countCooldown;
    private final Random rand;
    private String lastCollisionDirection;

    public Bandit(int x, int y, Panel gamePanel) {
        super(x, y, gamePanel);
        this.rand = new Random();
        this.direction = "standing";
        this.Speed = 1;
        this.lastCollisionDirection = "none";
        loadEntityVisuals();
    }

    @Override
    public void setEntity() {

        gamePanel.ck.checkTileCollision(this);
        gamePanel.ck.checkEntityCollision(this, gamePanel.player);
        moveEntity();
        updateSpriteFlag();
    }

    @Override
    protected void loadEntityVisuals() {
        try {
            String path = "malePlayer" + File.separator;
            standing = loadImage(path + "standingPlayer.png");
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

    private BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(File.separator + path)));
    }

    @Override
    protected void moveEntity() {
        boolean collided = false;

        // check for collision & try moving the entity
        if (direction.contains("up")) {
            if (!collisionUp) {
                y -= Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "up";
            }
        }

        if (direction.contains("down")) {
            if (!collisionDown) {
                y += Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "down";
            }
        }

        if (direction.contains("left")) {
            if (!collisionLeft) {
                x -= Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "left";
            }
        }

        if (direction.contains("right")) {
            if (!collisionRight) {
                x += Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "right";
            }
        }

        if (countCooldown > 0) {
            countCooldown--; // decrease cooldown timer
        }

        if (collided || countCooldown == 0) { // if the entity collides or  the cooldown ends
            direction = getDirection(); // get new direction
            countCooldown = DIRECTION_CHANGE_INTERVAL; // reset cooldown timer
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

    private String getDirection() {
        String[] validDirections;
        if (direction.equals("standing")) {
            validDirections = new String[]{"up", "up&left", "up&right", "down", "down&right", "down&left", "left", "right", "standing"};
            validDirections = Arrays.stream(validDirections).filter(dir -> !dir.equals(lastCollisionDirection)).toArray(String[]::new);
        } else {
            validDirections = new String[]{"standing"};
        }

        return validDirections[rand.nextInt(validDirections.length)];
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSprite();
        int drawX = x - gamePanel.player.getEntityX() + gamePanel.player.getEntityScreenX();
        int drawY = y - gamePanel.player.getEntityY() + gamePanel.player.getEntityScreenY();
        g2.drawImage(image, drawX, drawY, width, height, null);
        if(gamePanel.player.drawHitbox) drawEntityHitbox(g2, drawX, drawY);
    }

    private void drawEntityHitbox(Graphics2D g2, int drawX, int drawY) {
        g2.setColor(new Color(0, 255, 255, 100));
        g2.fillRect(drawX + hitbox.x, drawY + hitbox.y, hitbox.width, hitbox.height);
        g2.setColor(Color.BLUE);
        g2.drawRect(drawX + hitbox.x, drawY + hitbox.y, hitbox.width, hitbox.height);
    }
}
