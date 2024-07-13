package entities;

import frame.Panel;

import javax.imageio.ImageIO;
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

        gamePanel.ck.checkTileCollision(this); // check if the bandit is colliding with the tile collision areas

        List<Entity> entities = gamePanel.getEntities();
        for (Entity entity : entities) {
            gamePanel.ck.checkEntityCollision(this, entity); // check if the bandit is colliding with other entities
        }
        moveEntity();

        updateHitbox();

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

        // check for collision, move the entity & save last direction before collision
        if (direction.contains("up")) {
            if (!collisionUp) { y -= Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "up"; }
        }

        if (direction.contains("down")) {
            if (!collisionDown) { y += Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "down"; }
        }

        if (direction.contains("left")) {
            if (!collisionLeft) { x -= Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "left"; }
        }

        if (direction.contains("right")) {
            if (!collisionRight) { x += Speed; lastCollisionDirection = "none"; }
            else { collided = true; lastCollisionDirection = "right"; }
        }

        if (countCooldown > 0) countCooldown--; // decrease cooldown timer

        if (collided || countCooldown == 0) {  // if the entity collides or  the cooldown ends
            // gen random number between 0 & 1
            if (rand.nextDouble() < 0.65) direction = "standing";  // 65 % chance to be in standing mode
            else direction = getDirection(); // get new direction
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

    private String getDirection() {
        List<String> validDirections = new ArrayList<>(Arrays.asList("up", "up&left", "up&right", "down", "down&left", "down&right", "left", "right", "standing"));

        // directions that can cause collision are removed
        if (collisionUp) { validDirections.remove("up"); validDirections.remove("up&left"); validDirections.remove("up&right"); }
        if (collisionDown) { validDirections.remove("down"); validDirections.remove("down&left"); validDirections.remove("down&right"); }
        if (collisionLeft) { validDirections.remove("left"); validDirections.remove("up&left"); validDirections.remove("down&left"); }
        if (collisionRight) { validDirections.remove("right"); validDirections.remove("up&right"); validDirections.remove("down&right"); }

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
        if(gamePanel.player.drawHitbox) drawEntityHitbox(g2, drawX, drawY);
    }

    private void drawEntityHitbox(Graphics2D g2, int drawX, int drawY) {
        g2.setColor(new Color(0, 255, 255, 100));
        g2.fillRect(drawX + hitbox.x, drawY + hitbox.y, hitbox.width, hitbox.height);
        g2.setColor(Color.BLUE);
        g2.drawRect(drawX + hitbox.x, drawY + hitbox.y, hitbox.width, hitbox.height);
    }
}
